import './globals.css';
import Link from 'next/link';
import type { ReactNode } from 'react';

export const metadata = {
  title: 'Chef Cooks POS',
  description: 'Chef Cooks POS with admin and reports',
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body>
        <header className="site-header">
          <div className="brand">Chef Cooks</div>
          <nav className="nav-links">
            <Link href="/pos">POS</Link>
            <Link href="/admin/items">Items</Link>
            <Link href="/admin/orders">Orders</Link>
          </nav>
        </header>
        <main className="site-main">{children}</main>
      </body>
    </html>
  );
}
