'use client';

import { useEffect, useMemo, useState } from 'react';
import { api, DailySalesReport, Order } from '@/lib/api';

const currency = new Intl.NumberFormat('en-PK', {
  style: 'currency',
  currency: 'PKR',
});

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [selected, setSelected] = useState<Order | null>(null);
  const [statusFilter, setStatusFilter] = useState<'all' | 'DRAFT' | 'PAID' | 'CANCELLED'>('all');
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [reportDate, setReportDate] = useState('');
  const [report, setReport] = useState<DailySalesReport | null>(null);
  const [reportError, setReportError] = useState<string | null>(null);

  const loadOrders = async (status = statusFilter) => {
    setLoading(true);
    setError(null);
    try {
      const apiStatus = status === 'all' ? undefined : status;
      const data = await api.listOrders(apiStatus);
      setOrders(data);
      if (data.length && (!selected || !data.find((order) => order.id === selected.id))) {
        setSelected(data[0]);
      }
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  const visibleOrders = useMemo(() => {
    const search = query.trim().toLowerCase();
    if (!search) {
      return orders;
    }
    return orders.filter((order) => {
      return (
        order.id.toLowerCase().includes(search) ||
        (order.invoiceNumber ?? '').toLowerCase().includes(search)
      );
    });
  }, [orders, query]);

  const handleCancel = async () => {
    if (!selected || selected.status !== 'DRAFT') {
      return;
    }
    setBusy(true);
    setError(null);
    try {
      const updated = await api.cancelOrder(selected.id);
      setSelected(updated);
      await loadOrders();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusy(false);
    }
  };

  const handleReport = async () => {
    if (!reportDate) {
      setReportError('Select a date to generate the report.');
      return;
    }
    setBusy(true);
    setReportError(null);
    try {
      const data = await api.dailySales(reportDate);
      setReport(data);
    } catch (err) {
      setReport(null);
      setReportError((err as Error).message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <section className="section-card">
      <div className="section-header">
        <div>
          <h1 className="panel-title">Invoices</h1>
          <p className="muted">Track, review, and manage paid or draft invoices.</p>
        </div>
        <button className="button button-secondary" onClick={() => loadOrders()} disabled={loading || busy}>
          Refresh
        </button>
      </div>
      {error && <div className="alert">{error}</div>}
      <div className="orders-layout">
        <div className="section-subcard">
          <div className="controls">
            <input
              className="input"
              placeholder="Search by invoice or order id"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
            />
            <select
              className="input"
              value={statusFilter}
              onChange={(event) => {
                const next = event.target.value as typeof statusFilter;
                setStatusFilter(next);
                loadOrders(next);
              }}
            >
              <option value="all">All statuses</option>
              <option value="DRAFT">Draft</option>
              <option value="PAID">Paid</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
          </div>
          <div className="orders-list">
            {loading ? (
              <div className="muted">Loading invoices...</div>
            ) : (
              visibleOrders.map((order) => (
                <button
                  key={order.id}
                  className={`order-row ${selected?.id === order.id ? 'is-active' : ''}`}
                  onClick={() => setSelected(order)}
                >
                  <div>
                    <div className="table-main">{order.invoiceNumber ?? 'Draft invoice'}</div>
                    <div className="muted">{order.id}</div>
                  </div>
                  <div className="order-meta">
                    <span className="badge">{order.status}</span>
                    <span>{currency.format(order.total)}</span>
                  </div>
                </button>
              ))
            )}
            {!loading && visibleOrders.length === 0 && <div className="muted">No invoices found.</div>}
          </div>
        </div>
        <div className="section-subcard">
          <div className="section-header">
            <div>
              <h2 className="panel-title">Invoice details</h2>
              <p className="muted">Review customer and line item totals.</p>
            </div>
            <button
              className="button button-danger"
              onClick={handleCancel}
              disabled={!selected || selected.status !== 'DRAFT' || busy}
            >
              Cancel invoice
            </button>
          </div>
          {!selected ? (
            <div className="muted">Select an invoice to see details.</div>
          ) : (
            <div className="order-summary">
              <div className="summary-row">
                <span>Invoice</span>
                <span>{selected.invoiceNumber ?? 'Draft invoice'}</span>
              </div>
              <div className="summary-row">
                <span>Fiscal Invoice</span>
                <span>{selected.fiscalInvoiceNumber || '—'}</span>
              </div>
              <div className="summary-row">
                <span>Status</span>
                <span className="badge">{selected.status}</span>
              </div>
              <div className="summary-row">
                <span>Payment</span>
                <span>{selected.paymentMode || '—'}</span>
              </div>
              <div className="summary-row">
                <span>Customer</span>
                <span>{selected.customerName || 'Walk-in'}</span>
              </div>
              <div className="summary-row">
                <span>Contact</span>
                <span>{selected.customerPhone || '—'}</span>
              </div>
              <div className="summary-row">
                <span>Tax ID</span>
                <span>{selected.customerTaxId || '—'}</span>
              </div>
              <div className="summary-row">
                <span>CNIC</span>
                <span>{selected.customerCnic || '—'}</span>
              </div>
              <div className="summary-row">
                <span>PNTN</span>
                <span>{selected.customerPntn || '—'}</span>
              </div>
              {selected.notes && (
                <div className="summary-row">
                  <span>Notes</span>
                  <span>{selected.notes}</span>
                </div>
              )}
              <table className="table-shell">
                <thead>
                  <tr>
                    <th>Item</th>
                    <th>Qty</th>
                    <th>Line total</th>
                  </tr>
                </thead>
                <tbody>
                  {selected.items.map((entry) => (
                    <tr key={entry.id}>
                      <td>
                        <div className="table-main">{entry.itemName}</div>
                        <div className="muted">{currency.format(entry.unitPrice)}</div>
                      </td>
                      <td>{entry.quantity}</td>
                      <td>{currency.format(entry.lineTotal)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <div className="totals">
                <div className="total-row">
                  <span>Subtotal</span>
                  <span>{currency.format(selected.subtotal)}</span>
                </div>
                <div className="total-row">
                  <span>Tax</span>
                  <span>
                    {currency.format(selected.tax)} ({selected.gstRate ? `${(selected.gstRate * 100).toFixed(2)}%` : '—'})
                  </span>
                </div>
                <div className="total-row">
                  <span>Discount</span>
                  <span>{currency.format(selected.discount ?? 0)}</span>
                </div>
                <div className="total-row total-strong">
                  <span>Total</span>
                  <span>{currency.format(selected.total)}</span>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="section-subcard">
        <div className="section-header">
          <div>
            <h2 className="panel-title">Daily sales</h2>
            <p className="muted">Pull totals for a single business day.</p>
          </div>
          <div className="controls">
            <input
              className="input"
              type="date"
              value={reportDate}
              onChange={(event) => setReportDate(event.target.value)}
            />
            <button className="button button-primary" onClick={handleReport} disabled={busy}>
              Run report
            </button>
          </div>
        </div>
        {reportError && <div className="alert">{reportError}</div>}
        {report && (
          <div className="report-grid">
            <div className="report-card">
              <div className="muted">Date</div>
              <div className="panel-title">{report.date}</div>
            </div>
            <div className="report-card">
              <div className="muted">Orders</div>
              <div className="panel-title">{report.orderCount}</div>
            </div>
            <div className="report-card">
              <div className="muted">Total sales</div>
              <div className="panel-title">{currency.format(report.totalSales)}</div>
            </div>
          </div>
        )}
      </div>
    </section>
  );
}
