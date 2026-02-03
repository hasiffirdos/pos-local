# PRA Logging Guide

This document explains the comprehensive logging added to track all PRA requests and responses.

## 📋 What's Logged

### 1. Order Checkout Process
**Location:** `OrderService.checkout()`

```
╔════════════════════════════════════════╗
║     ORDER CHECKOUT STARTED             ║
╚════════════════════════════════════════╝
Order ID: xxx
Invoice Number: INV-20260203-XXXX
Status: DRAFT
Payment Mode: CASH
Items Count: 2
Recalculating order totals...
Order Totals:
  Subtotal: 180.00
  Tax: 28.80
  Discount: 0.00
  Total: 208.80
  GST Rate: 0.16
Starting PRA fiscalization...
```

### 2. Invoice Mapping
**Location:** `PraInvoiceMapper.fromOrder()`

```
=== PRA Invoice Mapping Started ===
Order ID: xxx, Invoice Number: INV-xxx, Payment Mode: CASH
GST Rate: 16.0% (0.16), Invoice Type: 1
  Item: Burger (ITEM001)
    Quantity: 1, Unit Price: 180.00, Line Total: 180.00
    Sale Value: 180.00, Tax Rate: 16.00%, Tax Charged: 28.80, Total Amount: 208.80
    PCT Code: 98211000
=== Invoice Totals Calculation ===
Total Sale Value: 180.00
Total Tax Charged: 28.80
Total Discount: 0.00
Total Bill Amount: 208.80 (Formula: 180.00 + 28.80 - 0.00)
Total Quantity: 1
=== PRA Invoice Mapping Completed ===
```

### 3. IMS Fiscalization Request
**Location:** `ImsPraFiscalizationClient.fiscalize()`

```
========================================
PRA IMS Fiscalization Request
========================================
URL: http://localhost:8524/api/IMSFiscal/GetInvoiceNumberByModel
USIN: INV-20260203-XXXX
POS ID: 189278
Date/Time: 2026-02-03 12:00:00
Payment Mode: 1
Invoice Type: 1
----------------------------------------
Total Sale Value: 180.00
Total Tax Charged: 28.80
Discount: 0.00
Total Bill Amount: 208.80
Total Quantity: 1
----------------------------------------
Items Count: 1
  - Burger x 1 = 180.00 (Tax: 28.80, Total: 208.80)
----------------------------------------
Customer: John Doe
Customer Phone: 03001234567
Customer CNIC: 12345-1234567-1
Customer PNTN: 1234567-8
========================================
```

### 4. IMS Fiscalization Response
**Success:**
```
========================================
PRA IMS Fiscalization Response
========================================
Invoice Number: 9000052026020300001
Code: 100
Response: Fiscal Invoice Number generated successfully.
Errors: null
========================================
✅ Fiscalization SUCCESS - Invoice: 9000052026020300001
```

**Failure:**
```
========================================
PRA IMS Fiscalization FAILED
========================================
HTTP Status: 500
Response Body: {"error": "Internal server error"}
========================================
```

### 5. Cloud Fiscalization Request
**Location:** `CloudPraFiscalizationClient.fiscalize()`

```
========================================
PRA Cloud Fiscalization Request
========================================
Environment: SANDBOX
URL: https://ims.pral.com.pk/ims/sandbox/api/Live/PostData
Token: 24d8fab3...a2a2
USIN: INV-20260203-XXXX
POS ID: 189278
Date/Time: 2026-02-03 12:00:00
Payment Mode: 1
Invoice Type: 1
----------------------------------------
Total Sale Value: 180.00
Total Tax Charged: 28.80
Discount: 0.00
Total Bill Amount: 208.80
Total Quantity: 1
----------------------------------------
Items Count: 1
  - Burger x 1 = 180.00 (Tax: 28.80, Total: 208.80)
----------------------------------------
Customer: John Doe
Customer Phone: 03001234567
Customer CNIC: 12345-1234567-1
Customer PNTN: 1234567-8
========================================
```

### 6. Cloud Fiscalization Response
**Success:**
```
========================================
PRA Cloud Fiscalization Response
========================================
HTTP Status: 200 OK
Invoice Number: 9000052026020300001
Code: 100
Response: Fiscal Invoice Number generated successfully.
Errors: null
========================================
✅ Fiscalization SUCCESS - Invoice: 9000052026020300001
```

**Failure:**
```
========================================
PRA Cloud Fiscalization FAILED
========================================
HTTP Status: 403 FORBIDDEN
Response Body: {"error": "IP not whitelisted"}
Headers: {Content-Type=[application/json]}
========================================
```

### 7. Order Checkout Completion
```
✅ PRA Fiscalization SUCCESS
Fiscal Invoice Number: 9000052026020300001
QR Text: https://reg.pra.punjab.gov.pk/...
Verification URL: https://reg.pra.punjab.gov.pk/...
Message: Fiscal Invoice Number generated successfully.
Order saved with status: PAID
╔════════════════════════════════════════╗
║     ORDER CHECKOUT COMPLETED           ║
╚════════════════════════════════════════╝
```

---

## 🔍 How to View Logs

### Console Output
Logs appear in the terminal where you run the backend:

```bash
cd backend
./gradlew bootRun
```

### Log Levels

| Level | When Used | Example |
|-------|-----------|---------|
| **INFO** | Normal operations | Request/response details |
| **DEBUG** | Detailed calculations | Totals recalculation |
| **WARN** | Recoverable issues | Deprecated features |
| **ERROR** | Failures | API errors, exceptions |

---

## 📊 What Each Log Shows

### Request Logs Show:
✅ **URL** - Where the request is sent  
✅ **Environment** - Sandbox or Production (Cloud only)  
✅ **Token** - First 8 and last 4 characters (security)  
✅ **Invoice Details** - USIN, POS ID, Date/Time  
✅ **Payment Info** - Mode, Type  
✅ **Amounts** - Sale Value, Tax, Discount, Total  
✅ **Items** - Each item with calculations  
✅ **Customer** - Name, Phone, CNIC, PNTN  

### Response Logs Show:
✅ **HTTP Status** - 200, 403, 500, etc.  
✅ **Invoice Number** - PRA fiscal invoice number  
✅ **Code** - PRA response code (100 = success)  
✅ **Response Message** - Success or error message  
✅ **Errors** - Any error details from PRA  
✅ **Headers** - HTTP response headers (on error)  

---

## 🐛 Debugging with Logs

### Problem: Wrong Total Amount

**Look for:**
```
=== Invoice Totals Calculation ===
Total Sale Value: 180.00
Total Tax Charged: 28.80
Total Discount: 0.00
Total Bill Amount: 208.80 (Formula: 180.00 + 28.80 - 0.00)
```

**Check:**
- Is the formula correct?
- Are the individual values correct?
- Does it match PRA report?

### Problem: PRA Returns Error

**Look for:**
```
========================================
PRA IMS Fiscalization FAILED
========================================
HTTP Status: 500
Response Body: {"error": "Invalid PCT Code"}
```

**Check:**
- What's the error message?
- What's the HTTP status?
- Is there a specific field mentioned?

### Problem: Wrong Tax Calculation

**Look for:**
```
Item: Burger (ITEM001)
  Quantity: 1, Unit Price: 180.00, Line Total: 180.00
  Sale Value: 180.00, Tax Rate: 16.00%, Tax Charged: 28.80, Total Amount: 208.80
```

**Check:**
- Is Tax Rate correct? (16% for CASH, 5% for CARD)
- Is Tax Charged = Sale Value × (Tax Rate / 100)?
- Is Total Amount = Sale Value + Tax Charged?

### Problem: Cloud API Not Working

**Look for:**
```
Environment: PRODUCTION
Token: 24d8fab3...a2a2
```

**Check:**
- Is environment correct?
- Is token showing (not NULL)?
- Is URL correct for environment?

---

## 📝 Log Examples

### Example 1: Successful CASH Order
```
╔════════════════════════════════════════╗
║     ORDER CHECKOUT STARTED             ║
╚════════════════════════════════════════╝
Order ID: 123e4567-e89b-12d3-a456-426614174000
Invoice Number: INV-20260203-ABC123
Payment Mode: CASH
Items Count: 1

=== PRA Invoice Mapping Started ===
GST Rate: 16.0% (0.16)
  Item: Burger x 1 = 180.00 (Tax: 28.80, Total: 208.80)
Total Bill Amount: 208.80

========================================
PRA IMS Fiscalization Request
========================================
Total Sale Value: 180.00
Total Tax Charged: 28.80
Total Bill Amount: 208.80

========================================
PRA IMS Fiscalization Response
========================================
Invoice Number: 9000052026020300001
Code: 100
✅ Fiscalization SUCCESS

╔════════════════════════════════════════╗
║     ORDER CHECKOUT COMPLETED           ║
╚════════════════════════════════════════╝
```

### Example 2: Successful CARD Order
```
Payment Mode: CARD
GST Rate: 5.0% (0.05)
  Item: Coffee x 2 = 100.00 (Tax: 5.00, Total: 105.00)
Total Bill Amount: 105.00
✅ Fiscalization SUCCESS
```

### Example 3: Failed - Token Not Configured
```
========================================
PRA Cloud Fiscalization Request
========================================
Environment: PRODUCTION
Token: NULL...
❌ PRA Cloud token not configured for environment: production
```

### Example 4: Failed - IP Not Whitelisted
```
========================================
PRA Cloud Fiscalization FAILED
========================================
HTTP Status: 403 FORBIDDEN
Response Body: {"error": "IP address not whitelisted"}
```

---

## 🎯 Quick Troubleshooting

| Log Message | Problem | Solution |
|-------------|---------|----------|
| `Token: NULL...` | Token not set | Set token in application.yml |
| `HTTP Status: 403` | IP not whitelisted | Email PRA to whitelist IP |
| `HTTP Status: 500` | Server error | Check PRA service status |
| `Invalid PCT Code` | Wrong PCT code | Update item PCT code |
| `IMS unavailable` | IMS not running | Start IMS service |
| `Connection timeout` | Network issue | Check internet/firewall |

---

## 🔧 Adjusting Log Levels

To see more or less detail, edit `application.yml`:

```yaml
logging:
  level:
    com.example.pos.pra: DEBUG  # More detail
    com.example.pos.service: INFO  # Normal detail
```

**Levels (most to least detail):**
- `TRACE` - Everything (very verbose)
- `DEBUG` - Detailed debugging info
- `INFO` - Normal operations (default)
- `WARN` - Warnings only
- `ERROR` - Errors only

---

## 📌 Summary

**All PRA requests now log:**
✅ Complete request details  
✅ All calculations with formulas  
✅ Full response from PRA  
✅ Success/failure status  
✅ Error details when failures occur  

**Benefits:**
- Easy debugging of calculation issues
- Track all PRA API calls
- Verify correct data is sent
- Identify errors quickly
- Audit trail for transactions

**Location:** Check your terminal where backend is running!
