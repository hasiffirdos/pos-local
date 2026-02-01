import Link from 'next/link';

export default function HomePage() {
  return (
    <section className="section-card">
      <div className="section-header">
        <div>
          <h1 className="panel-title">Chef Cooks POS</h1>
          <p className="muted">
            Run invoices, manage catalog items, and keep a clean record of daily sales.
          </p>
        </div>
        <Link className="button button-primary" href="/pos">
          Open POS
        </Link>
      </div>
      <div className="grid grid-2" style={{ marginTop: 24 }}>
        <Link className="section-subcard" href="/pos">
          <h2 className="panel-title">POS Screen</h2>
          <p className="muted">Create invoices, add items, and checkout fast.</p>
        </Link>
        <Link className="section-subcard" href="/admin/items">
          <h2 className="panel-title">Catalog</h2>
          <p className="muted">Create and organize the items used on invoices.</p>
        </Link>
        <Link className="section-subcard" href="/admin/orders">
          <h2 className="panel-title">Invoices</h2>
          <p className="muted">Review invoice status and daily sales.</p>
        </Link>
      </div>
    </section>
  );
}
