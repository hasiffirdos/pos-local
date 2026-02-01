ALTER TABLE orders ADD COLUMN invoice_number TEXT;
ALTER TABLE orders ADD COLUMN customer_name TEXT;
ALTER TABLE orders ADD COLUMN customer_phone TEXT;
ALTER TABLE orders ADD COLUMN customer_tax_id TEXT;
ALTER TABLE orders ADD COLUMN notes TEXT;

UPDATE orders SET invoice_number = 'INV-' || strftime('%Y%m%d', created_at) || '-' || substr(lower(hex(randomblob(4))), 1, 8)
WHERE invoice_number IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_orders_invoice_number ON orders(invoice_number);
