const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options?.headers || {}),
    },
    cache: 'no-store',
  });

  if (!res.ok) {
    const message = await res.text();
    throw new Error(message || 'Request failed');
  }

  // Handle 204 No Content (empty response)
  if (res.status === 204) {
    return undefined as T;
  }

  return res.json() as Promise<T>;
}

export type Item = {
  id: string;
  name: string;
  price: number;
  category: string;
  itemCode: string;
  pctCode: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
};

export type OrderItem = {
  id: string;
  itemId: string;
  itemName: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
};

export type Order = {
  id: string;
  invoiceNumber: string | null;
  fiscalInvoiceNumber: string | null;
  fiscalQrText: string | null;
  fiscalVerificationUrl: string | null;
  subtotal: number;
  tax: number;
  total: number;
  status: 'DRAFT' | 'PAID' | 'CANCELLED';
  paymentMode: 'CASH' | 'CARD' | null;
  gstRate: number | null;
  gstAmount: number | null;
  customerName: string | null;
  customerPhone: string | null;
  customerCnic: string | null;
  customerPntn: string | null;
  customerTaxId: string | null;
  notes: string | null;
  discount: number | null;
  createdAt: string;
  items: OrderItem[];
};

export type DailySalesReport = {
  date: string;
  orderCount: number;
  totalSales: number;
};

export const api = {
  listItems: (includeInactive = false) =>
    request<Item[]>(`/api/items?includeInactive=${includeInactive}`),
  createItem: (body: {
    name: string;
    price: number;
    category: string;
    itemCode: string;
    pctCode: string;
  }) => request<Item>('/api/items', { method: 'POST', body: JSON.stringify(body) }),
  updateItem: (
    id: string,
    body: {
      name: string;
      price: number;
      category: string;
      itemCode: string;
      pctCode: string;
    }
  ) => request<Item>(`/api/items/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  deleteItem: (id: string) => request<void>(`/api/items/${id}`, { method: 'DELETE' }),
  toggleItemActive: (id: string) => request<Item>(`/api/items/${id}/toggle-active`, { method: 'PATCH' }),
  listOrders: (status?: 'DRAFT' | 'PAID' | 'CANCELLED') => {
    const suffix = status ? `?status=${status}` : '';
    return request<Order[]>(`/api/orders${suffix}`);
  },
  createOrder: () => request<Order>('/api/orders', { method: 'POST' }),
  getOrder: (id: string) => request<Order>(`/api/orders/${id}`),
  updateOrder: (
    id: string,
    body: {
      customerName?: string;
      customerPhone?: string;
      customerCnic?: string;
      customerPntn?: string;
      customerTaxId?: string;
      notes?: string;
      discount?: number;
      paymentMode?: 'CASH' | 'CARD';
    }
  ) => request<Order>(`/api/orders/${id}`, { method: 'PATCH', body: JSON.stringify(body) }),
  addOrderItem: (orderId: string, body: { itemId: string; quantity: number }) =>
    request<Order>(`/api/orders/${orderId}/items`, { method: 'POST', body: JSON.stringify(body) }),
  removeOrderItem: (orderId: string, itemId: string) =>
    request<Order>(`/api/orders/${orderId}/items/${itemId}`, { method: 'DELETE' }),
  checkoutOrder: (orderId: string) =>
    request<Order>(`/api/orders/${orderId}/checkout`, { method: 'POST' }),
  cancelOrder: (orderId: string) =>
    request<Order>(`/api/orders/${orderId}/cancel`, { method: 'POST' }),
  dailySales: (date: string) => request<DailySalesReport>(`/api/reports/daily-sales?date=${date}`),
};
