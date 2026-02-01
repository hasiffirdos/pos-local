UPDATE orders SET payment_mode = 'CASH' WHERE payment_mode IS NULL AND status = 'DRAFT';
