'use client';

import { useMemo, useState, useEffect, useRef } from 'react';
import { api, Item, Order, OrderItem } from '@/lib/api';
import { QRCodeSVG } from 'qrcode.react';

const currency = new Intl.NumberFormat('en-PK', {
  style: 'currency',
  currency: 'PKR',
});

export default function PosPage() {
  const [items, setItems] = useState<Item[]>([]);
  const [order, setOrder] = useState<Order | null>(null);
  const [loadingItems, setLoadingItems] = useState(true);
  const [actionBusy, setActionBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('all');
  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [customerTaxId, setCustomerTaxId] = useState('');
  const [customerCnic, setCustomerCnic] = useState('');
  const [customerPntn, setCustomerPntn] = useState('');
  const [notes, setNotes] = useState('');
  const [discount, setDiscount] = useState('0');
  const [detailsSaved, setDetailsSaved] = useState(false);
  const [detailsSaving, setDetailsSaving] = useState(false);
  const lastSavedRef = useRef<string | null>(null);
  const [paymentMode, setPaymentMode] = useState<'CASH' | 'CARD'>('CASH');
  const [showDetails, setShowDetails] = useState(false);
  const posId = process.env.NEXT_PUBLIC_POS_ID || '189278';
  const formatError = (err: unknown) => {
    const message = err instanceof Error ? err.message : String(err);
    if (message.includes('PRA IMS unavailable')) {
      return 'Setup is not running, please start it.';
    }
    return message;
  };
  const computedTotals = useMemo(() => {
    if (!order) {
      return {
        subtotal: 0,
        discount: Number(discount) || 0,
        gstRate: paymentMode === 'CARD' ? 0.05 : 0.16,
        gstAmount: 0,
        total: 0,
      };
    }
    const subtotal = order.items.reduce((sum, item) => sum + item.lineTotal, 0);
    const discountVal = Number(discount) || 0;
    const gstRate = paymentMode === 'CARD' ? 0.05 : 0.16;
    const taxable = Math.max(0, subtotal - discountVal);
    const gstAmount = parseFloat((taxable * gstRate).toFixed(2));
    const total = parseFloat((taxable + gstAmount).toFixed(2));
    return {
      subtotal,
      discount: discountVal,
      gstRate,
      gstAmount,
      total,
    };
  }, [order, discount, paymentMode]);

  useEffect(() => {
    let active = true;
    setLoadingItems(true);
    api
      .listItems()
      .then((data) => {
        if (active) {
          setItems(data);
        }
      })
      .catch((err: Error) => {
        if (active) {
          setError(err.message);
        }
      })
      .finally(() => {
        if (active) {
          setLoadingItems(false);
        }
      });
    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    if (!order) {
      return;
    }
    setCustomerName(order.customerName ?? '');
    setCustomerPhone(order.customerPhone ?? '');
    setCustomerTaxId(order.customerTaxId ?? '');
    setCustomerCnic(order.customerCnic ?? '');
    setCustomerPntn(order.customerPntn ?? '');
    setNotes(order.notes ?? '');
    setDiscount(order.discount ? order.discount.toFixed(2) : '0');
    setPaymentMode(order.paymentMode ? (order.paymentMode as 'CASH' | 'CARD') : 'CASH');
  }, [order]);

  const categories = useMemo(() => {
    const unique = new Set(items.map((item) => item.category));
    return ['all', ...Array.from(unique)];
  }, [items]);

  const visibleItems = useMemo(() => {
    const search = query.trim().toLowerCase();
    return items.filter((item) => {
      const matchesCategory = category === 'all' || item.category === category;
      const matchesQuery =
        !search ||
        item.name.toLowerCase().includes(search) ||
        item.category.toLowerCase().includes(search);
      return matchesCategory && matchesQuery;
    });
  }, [items, query, category]);

  const ensureOrder = async () => {
    if (order) {
      return order;
    }
    const created = await api.createOrder();
    setOrder(created);
    setCustomerName('');
    setCustomerPhone('');
    setCustomerTaxId('');
    setCustomerCnic('');
    setCustomerPntn('');
    setNotes('');
    setDiscount('0');
    setDetailsSaved(false);
    lastSavedRef.current = null;
    return created;
  };

  const handleAddItem = async (item: Item) => {
    setError(null);
    setActionBusy(true);
    try {
      const current = await ensureOrder();
      const existing = current.items.find((entry) => entry.itemId === item.id);
      const nextQty = existing ? existing.quantity + 1 : 1;
      const updated = await api.addOrderItem(current.id, { itemId: item.id, quantity: nextQty });
      setOrder(updated);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setActionBusy(false);
    }
  };

  const handleUpdateQty = async (entry: OrderItem, nextQty: number) => {
    if (!order || nextQty < 1) {
      return;
    }
    setError(null);
    setActionBusy(true);
    try {
      const updated = await api.addOrderItem(order.id, { itemId: entry.itemId, quantity: nextQty });
      setOrder(updated);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setActionBusy(false);
    }
  };

  const handleRemoveItem = async (entry: OrderItem) => {
    if (!order) {
      return;
    }
    setError(null);
    setActionBusy(true);
    try {
      const updated = await api.removeOrderItem(order.id, entry.itemId);
      setOrder(updated);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setActionBusy(false);
    }
  };

  useEffect(() => {
    if (!order || order.status === 'PAID') {
      return;
    }

    const parsedDiscount = Number(discount);
    if (Number.isNaN(parsedDiscount) || parsedDiscount < 0) {
      setError('Discount must be 0 or more.');
      return;
    }

    const payload = {
      customerName: customerName.trim() || undefined,
      customerPhone: customerPhone.trim() || undefined,
      customerCnic: customerCnic.trim() || undefined,
      customerPntn: customerPntn.trim() || undefined,
      customerTaxId: customerTaxId.trim() || undefined,
      notes: notes.trim() || undefined,
      discount: parsedDiscount,
      paymentMode,
    };
    const serialized = JSON.stringify(payload);
    if (lastSavedRef.current === serialized) {
      return;
    }

    const timeoutId = window.setTimeout(async () => {
      setError(null);
      setDetailsSaving(true);
      try {
        const updated = await api.updateOrder(order.id, payload);
        lastSavedRef.current = serialized;
        setOrder(updated);
        setDetailsSaved(true);
        window.setTimeout(() => setDetailsSaved(false), 1500);
      } catch (err) {
        setError((err as Error).message);
      } finally {
        setDetailsSaving(false);
      }
    }, 600);

    return () => window.clearTimeout(timeoutId);
  }, [
    order,
    customerName,
    customerPhone,
    customerTaxId,
    customerCnic,
    customerPntn,
    notes,
    discount,
    paymentMode,
  ]);

  const handleCheckout = async () => {
    if (!order || order.items.length === 0) {
      return;
    }
    setError(null);
    setActionBusy(true);
    try {
      const updated = await api.checkoutOrder(order.id);
      setOrder(updated);
      window.setTimeout(() => window.print(), 200);
    } catch (err) {
      setError(formatError(err));
    } finally {
      setActionBusy(false);
    }
  };

  const handleNewInvoice = async () => {
    setError(null);
    setActionBusy(true);
    try {
      const created = await api.createOrder();
      setOrder(created);
      setCustomerName('');
      setCustomerPhone('');
      setCustomerTaxId('');
      setCustomerCnic('');
      setCustomerPntn('');
      setNotes('');
      setDiscount('0');
      setDetailsSaved(false);
      lastSavedRef.current = null;
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setActionBusy(false);
    }
  };

  return (
    <div className="grid grid-2 pos-layout">
      <section className="section-card pos-catalog">
        <div className="section-header">
          <div>
            <h1 className="panel-title">Item Catalog</h1>
            <p className="muted">Add items to the invoice in progress.</p>
          </div>
          <div className="controls">
            <input
              className="input"
              placeholder="Search items"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
            />
            <select
              className="input"
              value={category}
              onChange={(event) => setCategory(event.target.value)}
            >
              {categories.map((value) => (
                <option key={value} value={value}>
                  {value === 'all' ? 'All categories' : value}
                </option>
              ))}
            </select>
          </div>
        </div>
        <div className="pos-catalog-scroll">
          {loadingItems ? (
            <div className="muted">Loading items...</div>
          ) : (
            <div className="items-grid">
              {visibleItems.map((item) => (
                <div key={item.id} className="item-card">
                  <div>
                    <h3>{item.name}</h3>
                    <div className="muted">{item.category}</div>
                  </div>
                  <div className="item-meta">
                    <div className="price">{currency.format(item.price)}</div>
                    <button
                      className="button"
                      onClick={() => handleAddItem(item)}
                      disabled={actionBusy || order?.status === 'PAID'}
                    >
                      Add
                    </button>
                  </div>
                </div>
              ))}
              {visibleItems.length === 0 && <div className="muted">No items match this filter.</div>}
            </div>
          )}
        </div>
      </section>
      <aside className="section-card">
        <div className="section-header">
          <div>
            <h2 className="panel-title">Invoice</h2>
            <p className="muted">
              {order
                ? `${order.invoiceNumber ?? 'Draft invoice'} · ${order.status}`
                : 'Start an invoice to add items.'}
            </p>
          </div>
          <button className="button button-secondary" onClick={handleNewInvoice} disabled={actionBusy}>
            New invoice
          </button>
        </div>
        <div className="controls">
          <button
            className={`button ${paymentMode === 'CASH' ? 'button-primary' : 'button-ghost'}`}
            onClick={() => setPaymentMode('CASH')}
            disabled={!order || order.status === 'PAID'}
          >
            Cash (16% GST)
          </button>
          <button
            className={`button ${paymentMode === 'CARD' ? 'button-primary' : 'button-ghost'}`}
            onClick={() => setPaymentMode('CARD')}
            disabled={!order || order.status === 'PAID'}
          >
            Card (5% GST)
          </button>
        </div>
        {error && <div className="alert">{error}</div>}
        
        {/* FBR-Style Print Receipt - Only visible when printing */}
        <div className="print-receipt print-only">
          {/* Header */}
          <div className="receipt-header">
            <div className="receipt-logo">
              <img className="receipt-logo-image" src="/logo.jpeg" alt="Chef Cooks logo" />
            </div>
            <h2 className="business-name">Chef Cooks</h2>
            <div className="receipt-info">
              <div className="info-row">
                <span className="info-label">NTN #:</span>
                <span className="info-value">5513153-5</span>
              </div>
              <div className="info-row">
                <span className="info-label">POSID:</span>
                <span className="info-value">{posId}</span>
              </div>
              <div className="info-row">
                <span className="info-label">Invoice#:</span>
                <span className="info-value">{order?.invoiceNumber ?? 'Draft'}</span>
              </div>
              <div className="info-row">
                <span className="info-label">Date:</span>
                <span className="info-value">
                  {order?.createdAt ? new Date(order.createdAt).toLocaleDateString('en-CA') : '—'}
                </span>
              </div>
            </div>
          </div>

          <div className="receipt-divider"></div>

          {/* Customer Info */}
          <div className="receipt-customer">
            <div className="customer-row">
              <span className="customer-label">Payment Mode:</span>
              <span className="customer-value">{order?.paymentMode || paymentMode}</span>
            </div>
            <div className="customer-row">
              <span className="customer-label">Customer:</span>
              <span className="customer-value">{order?.customerName || 'Walk-in'}</span>
            </div>
            <div className="customer-row">
              <span className="customer-label">Mobile:</span>
              <span className="customer-value">{order?.customerPhone || '—'}</span>
            </div>
            <div className="customer-row">
              <span className="customer-label">NTN:</span>
              <span className="customer-value">{order?.customerPntn || '—'}</span>
            </div>
            <div className="customer-row">
              <span className="customer-label">CNIC:</span>
              <span className="customer-value">{order?.customerCnic || '—'}</span>
            </div>
          </div>

          {/* Items Table */}
          <table className="receipt-items">
            <thead>
              <tr>
                <th>#</th>
                <th>Description</th>
                <th>Rate</th>
                <th>GST%</th>
                <th>Qty</th>
                <th>GST</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              {order?.items.map((item, index) => {
                const gstRate = order.paymentMode === 'CARD' ? 0.05 : 0.16;
                const itemGst = parseFloat((item.lineTotal * gstRate).toFixed(2));
                return (
                  <tr key={item.id}>
                    <td>{index + 1}</td>
                    <td className="item-desc">{item.itemName}</td>
                    <td>{item.unitPrice.toFixed(0)}</td>
                    <td>{(gstRate * 100).toFixed(0)}</td>
                    <td>{item.quantity}</td>
                    <td>{itemGst.toLocaleString()}</td>
                    <td>{item.lineTotal.toLocaleString()}</td>
                  </tr>
                );
              })}
              {(!order?.items || order.items.length === 0) && (
                <tr>
                  <td colSpan={7} className="empty-row">No items</td>
                </tr>
              )}
            </tbody>
          </table>

          <div className="receipt-divider thick"></div>

          {/* Totals */}
          <div className="receipt-totals">
            <div className="total-row">
              <span>Total Amount:</span>
              <span>{(order?.subtotal ?? 0).toLocaleString()}</span>
            </div>
            <div className="total-row">
              <span>Sales Tax ({((order?.gstRate ?? 0.16) * 100).toFixed(0)}%):</span>
              <span>{(order?.gstAmount ?? order?.tax ?? 0).toLocaleString()}</span>
            </div>
            {(order?.discount ?? 0) > 0 && (
              <div className="total-row">
                <span>Discount:</span>
                <span>-{(order?.discount ?? 0).toLocaleString()}</span>
              </div>
            )}
            <div className="total-row total-final">
              <span>Payable:</span>
              <span>{(order?.total ?? 0).toLocaleString()}</span>
            </div>
            {order?.status === 'PAID' && (
              <div className="total-row">
                <span>Received:</span>
                <span>{(order?.total ?? 0).toLocaleString()}</span>
              </div>
            )}
          </div>

          <div className="receipt-divider"></div>

          {/* FBR Footer */}
          {order?.fiscalInvoiceNumber && (
            <div className="receipt-fbr">
              <div className="fbr-invoice">
                <span className="fbr-label">PRA Invoice#</span>
                <span className="fbr-value">{order.fiscalInvoiceNumber}</span>
              </div>
              
              <div className="fbr-branding">
                <div className="fbr-logo">
                  <div className="fbr-logo-text">
                    <span className="fbr-text">PRA</span>
                    <span className="pos-text">POS</span>
                    <span className="system-text">INVOICING SYSTEM</span>
                  </div>
                </div>
                {order.fiscalVerificationUrl && (
                  <div className="fbr-qr">
                    <QRCodeSVG 
                      value={order.fiscalVerificationUrl} 
                      size={80}
                      level="M"
                    />
                  </div>
                )}
              </div>

              <p className="fbr-verify-text">
                Verify this invoice at reg.pra.punjab.gov.pk or scan the QR code
              </p>
            </div>
          )}

          {/* Status Badge (for non-fiscal) */}
          {!order?.fiscalInvoiceNumber && order && (
            <div className="receipt-status">
              <span className={`status-badge status-${order.status.toLowerCase()}`}>
                {order.status}
              </span>
            </div>
          )}

          {/* Software Contact Info - shows after Feb 14, 2026 */}
          {new Date() >= new Date('2026-02-14') && (
            <>
              <div className="receipt-divider"></div>
              <div className="receipt-software-contact">
                <p>For POS Software inquiries:</p>
                <p>📞 0316-4985788</p>
                <p>✉️ h.asif.firdos@gmail.com</p>
              </div>
            </>
          )}
        </div>
        <div className="collapsible">
          <button
            className="button button-secondary"
            onClick={() => setShowDetails((prev) => !prev)}
            disabled={!order || order.status === 'PAID'}
          >
            {showDetails ? 'Hide customer details' : 'Show customer details'}
          </button>
          {showDetails && (
            <div className="invoice-details">
              <div className="field">
                <label>Customer name</label>
                <input
                  className="input"
                  placeholder="Optional"
                  value={customerName}
                  onChange={(event) => setCustomerName(event.target.value)}
                  disabled={!order || order.status === 'PAID'}
                />
              </div>
              <div className="field">
                <label>Customer phone</label>
                <input
                  className="input"
                  placeholder="Optional"
                  value={customerPhone}
                  onChange={(event) => setCustomerPhone(event.target.value)}
                  disabled={!order || order.status === 'PAID'}
                />
              </div>
              <div className="field">
                <label>Tax ID</label>
                <input
                  className="input"
                  placeholder="Optional"
                  value={customerTaxId}
                  onChange={(event) => setCustomerTaxId(event.target.value)}
                  disabled={!order || order.status === 'PAID'}
                />
              </div>
              <div className="field">
                <label>Customer CNIC</label>
                <input
                  className="input"
                  placeholder="Optional"
                  value={customerCnic}
                  onChange={(event) => setCustomerCnic(event.target.value)}
                  disabled={!order || order.status === 'PAID'}
                />
              </div>
              <div className="field">
                <label>Customer PNTN</label>
                <input
                  className="input"
                  placeholder="Optional"
                  value={customerPntn}
                  onChange={(event) => setCustomerPntn(event.target.value)}
                  disabled={!order || order.status === 'PAID'}
                />
              </div>
              <div className="field field-notes">
                <label>Notes</label>
                <textarea
                  className="input textarea"
                  placeholder="Add delivery, pickup, or invoice notes"
                  value={notes}
                  onChange={(event) => setNotes(event.target.value)}
                  disabled={!order || order.status === 'PAID'}
                />
              </div>
              <div className="details-actions">
                {detailsSaving && <span className="muted">Saving...</span>}
                {!detailsSaving && detailsSaved && <span className="muted">Saved</span>}
              </div>
            </div>
          )}
        </div>
        <table className="table-shell">
          <thead>
            <tr>
              <th>Item</th>
              <th>Qty</th>
              <th>Total</th>
              <th aria-label="Actions"></th>
            </tr>
          </thead>
          <tbody>
            {order?.items.map((entry) => (
              <tr key={entry.id}>
                <td>
                  <div className="table-main">{entry.itemName}</div>
                  <div className="muted">{currency.format(entry.unitPrice)}</div>
                </td>
                <td>
                  <div className="qty-controls">
                    <button
                      className="button button-ghost"
                      onClick={() => handleUpdateQty(entry, entry.quantity - 1)}
                      disabled={actionBusy || entry.quantity <= 1 || order.status === 'PAID'}
                    >
                      -
                    </button>
                    <span>{entry.quantity}</span>
                    <button
                      className="button button-ghost"
                      onClick={() => handleUpdateQty(entry, entry.quantity + 1)}
                      disabled={actionBusy || order.status === 'PAID'}
                    >
                      +
                    </button>
                  </div>
                </td>
                <td>{currency.format(entry.lineTotal)}</td>
                <td>
                  <button
                    className="button button-ghost"
                    onClick={() => handleRemoveItem(entry)}
                    disabled={actionBusy || order?.status === 'PAID'}
                  >
                    Remove
                  </button>
                </td>
              </tr>
            ))}
            {!order?.items.length && (
              <tr>
                <td colSpan={4} className="muted">
                  No items yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
        <br/>
        <label className="form-field">
          <span>Invoice discount</span>
          <input
            className="input"
            placeholder="0.00"
            type="number"
            min="0"
            step="0.01"
            value={discount}
            onChange={(event) => setDiscount(event.target.value)}
            disabled={!order || order.status === 'PAID'}
          />
        </label>
        <div className="totals">
          <div className="total-row">
            <span>Subtotal</span>
            <span>{currency.format(computedTotals.subtotal)}</span>
          </div>
          <div className="total-row">
            <span>Discount</span>
            <span>{currency.format(computedTotals.discount)}</span>
          </div>
          <div className="total-row">
            <span>GST ({paymentMode === 'CARD' ? '5%' : '16%'})</span>
            <span>{currency.format(computedTotals.gstAmount)}</span>
          </div>
          <div className="total-row total-strong">
            <span>Total</span>
            <span>{currency.format(computedTotals.total)}</span>
          </div>
          <button
            className="button button-primary"
            onClick={handleCheckout}
            disabled={actionBusy || !order || order.items.length === 0 || order.status === 'PAID'}
          >
            Checkout
          </button>
          <button
            className="button button-ghost"
            onClick={() => window.print()}
            disabled={!order}
          >
            Print bill
          </button>
        </div>
      </aside>
    </div>
  );
}
