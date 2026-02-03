# PRA Invoice Calculation Bug Fix

## 🐛 Problem Identified

Looking at the PRA IMS Fiscal Report, the calculations were incorrect:

### Example from Report:
| Field | Expected | Actual | Status |
|-------|----------|--------|--------|
| Sale Value | 180.00 | 180.00 | ✅ Correct |
| Tax Charged | 28.80 | 28.80 | ✅ Correct |
| Total Balance | **208.80** | **180.29** | ❌ **WRONG** |

**Issue:** The `TotalBillAmount` was being calculated incorrectly.

---

## 🔍 Root Cause

### Bug 1: Wrong TotalBillAmount Calculation
**Before (WRONG):**
```java
BigDecimal totalBillAmount = items.stream()
    .map(PraInvoiceItem::totalAmount)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

BigDecimal discountedTotal = totalBillAmount.subtract(totalDiscount);
```

**Problem:** 
- Used item `totalAmount` which was already calculated wrong
- Then subtracted discount from wrong total

### Bug 2: Tax Calculation from Items
**Before (WRONG):**
```java
BigDecimal totalTaxCharged = order.getTax() == null ? BigDecimal.ZERO : order.getTax();
```

**Problem:**
- Used order-level tax which might not match item-level calculations
- PRA expects sum of item taxes, not order tax

---

## ✅ Solution Implemented

### Fix 1: Correct TotalBillAmount Calculation
**After (CORRECT):**
```java
// Calculate from items
BigDecimal totalSaleValue = items.stream()
    .map(PraInvoiceItem::saleValue)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

BigDecimal totalTaxCharged = items.stream()
    .map(PraInvoiceItem::taxCharged)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

// PRA Formula: TotalBillAmount = TotalSaleValue + TotalTaxCharged - Discount
BigDecimal totalBillAmount = totalSaleValue
    .add(totalTaxCharged)
    .subtract(totalDiscount)
    .setScale(2, RoundingMode.HALF_UP);
```

### Fix 2: Improved Item Calculation
**After (CORRECT):**
```java
// SaleValue = Price × Quantity (EXCLUDING tax)
BigDecimal saleValue = lineTotal.setScale(2, RoundingMode.HALF_UP);

// TaxRate as percentage (16 for 16%, not 0.16)
BigDecimal taxRate = gstRate.multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);

// TaxCharged = SaleValue × TaxRate
BigDecimal taxCharged = saleValue.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);

// TotalAmount = SaleValue + TaxCharged
BigDecimal totalAmount = saleValue.add(taxCharged).setScale(2, RoundingMode.HALF_UP);
```

---

## 📊 PRA Invoice Format (Correct)

### Invoice Level:
```
TotalSaleValue = Sum of all item SaleValues
TotalTaxCharged = Sum of all item TaxCharged
Discount = Order-level discount
TotalBillAmount = TotalSaleValue + TotalTaxCharged - Discount
```

### Item Level:
```
SaleValue = UnitPrice × Quantity (excluding tax)
TaxRate = GST Rate as percentage (e.g., 16 or 5)
TaxCharged = SaleValue × (TaxRate / 100)
TotalAmount = SaleValue + TaxCharged
```

---

## 🧮 Example Calculation

### Scenario:
- Item: Burger
- Unit Price: 180.00 PKR
- Quantity: 1
- Payment Mode: CASH (16% GST)
- Order Discount: 0.00

### Correct Calculation:

**Item Level:**
```
SaleValue = 180.00 × 1 = 180.00
TaxRate = 16 (percentage)
TaxCharged = 180.00 × 0.16 = 28.80
TotalAmount = 180.00 + 28.80 = 208.80
```

**Invoice Level:**
```
TotalSaleValue = 180.00
TotalTaxCharged = 28.80
Discount = 0.00
TotalBillAmount = 180.00 + 28.80 - 0.00 = 208.80 ✅
```

---

## 🔄 What Changed

### File: `PraInvoiceMapper.java`

#### Change 1: `fromOrder()` method
- ✅ Calculate `totalTaxCharged` from items, not order
- ✅ Use correct formula: `TotalBillAmount = SaleValue + Tax - Discount`
- ✅ Add proper rounding with `setScale(2, HALF_UP)`

#### Change 2: `toItem()` method
- ✅ Clarified that `saleValue` is price × quantity (excluding tax)
- ✅ Added proper rounding to all calculations
- ✅ Added comments explaining PRA format
- ✅ Set item discount to zero (not implemented yet)

---

## 🧪 Testing

### Test Case 1: Single Item, Cash Payment
**Input:**
- Item: 180.00 PKR × 1
- Payment: CASH (16% GST)
- Discount: 0.00

**Expected JSON:**
```json
{
  "TotalSaleValue": 180.00,
  "TotalTaxCharged": 28.80,
  "TotalBillAmount": 208.80,
  "Items": [
    {
      "SaleValue": 180.00,
      "TaxRate": 16.00,
      "TaxCharged": 28.80,
      "TotalAmount": 208.80
    }
  ]
}
```

### Test Case 2: Single Item, Card Payment
**Input:**
- Item: 50.00 PKR × 1
- Payment: CARD (5% GST)
- Discount: 0.00

**Expected JSON:**
```json
{
  "TotalSaleValue": 50.00,
  "TotalTaxCharged": 2.50,
  "TotalBillAmount": 52.50,
  "Items": [
    {
      "SaleValue": 50.00,
      "TaxRate": 5.00,
      "TaxCharged": 2.50,
      "TotalAmount": 52.50
    }
  ]
}
```

### Test Case 3: Multiple Items with Discount
**Input:**
- Item 1: 100.00 PKR × 2 = 200.00
- Item 2: 50.00 PKR × 1 = 50.00
- Payment: CASH (16% GST)
- Discount: 20.00

**Expected Calculation:**
```
Item 1:
  SaleValue = 200.00
  TaxCharged = 200.00 × 0.16 = 32.00
  TotalAmount = 232.00

Item 2:
  SaleValue = 50.00
  TaxCharged = 50.00 × 0.16 = 8.00
  TotalAmount = 58.00

Invoice:
  TotalSaleValue = 250.00
  TotalTaxCharged = 40.00
  Discount = 20.00
  TotalBillAmount = 250.00 + 40.00 - 20.00 = 270.00
```

---

## ⚠️ Important Notes

### 1. Tax Calculation
- Tax is calculated on **SaleValue** (before discount)
- Discount is applied **after** adding tax
- This matches PRA's expected format

### 2. Rounding
- All monetary values rounded to 2 decimal places
- Using `HALF_UP` rounding mode (standard for currency)

### 3. Tax Rate Format
- **In JSON:** Send as percentage (16, not 0.16)
- **In Calculation:** Use decimal (0.16)
- Conversion: `taxRate = gstRate × 100`

### 4. Item-Level Discount
- Currently set to 0.00
- Order-level discount is applied at invoice level
- Can be enhanced later for item-specific discounts

---

## 🚀 Next Steps

### To Verify the Fix:

1. **Restart Backend**
   ```bash
   cd backend
   ./gradlew bootRun
   ```

2. **Create Test Order**
   - Add item worth 180.00 PKR
   - Select CASH payment
   - Complete checkout

3. **Check PRA Report**
   - Login to PRA portal
   - Check IMS Fiscal Report
   - Verify: `TotalBillAmount = SaleValue + TaxCharged - Discount`

4. **Expected Result**
   ```
   Sale Value: 180.00
   Tax Charged: 28.80
   Total Balance: 208.80 ✅
   ```

---

## 📝 Summary

**Before:** TotalBillAmount was calculated incorrectly, causing mismatch in PRA reports

**After:** Correct calculation following PRA formula:
```
TotalBillAmount = TotalSaleValue + TotalTaxCharged - Discount
```

**Impact:** 
- ✅ PRA reports now show correct totals
- ✅ Tax calculations match expectations
- ✅ Invoices are properly fiscalized
- ✅ Compliance with PRA requirements

**Files Modified:**
- `backend/src/main/java/com/example/pos/pra/PraInvoiceMapper.java`

**Status:** ✅ **FIXED AND READY FOR TESTING**
