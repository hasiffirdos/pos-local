'use client';

import { useEffect, useMemo, useState } from 'react';
import { api, Item } from '@/lib/api';

const currency = new Intl.NumberFormat('en-PK', {
  style: 'currency',
  currency: 'PKR',
});

type ItemDraft = {
  name: string;
  category: string;
  price: string;
  itemCode: string;
  pctCode: string;
};

const emptyDraft: ItemDraft = {
  name: '',
  category: '',
  price: '',
  itemCode: '',
  pctCode: '00000000',
};

export default function AdminItemsPage() {
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [draft, setDraft] = useState<ItemDraft>(emptyDraft);
  const [editId, setEditId] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [query, setQuery] = useState('');
  const [showInactive, setShowInactive] = useState(false);
  const [categoryFilter, setCategoryFilter] = useState('all');
  const [showNewCategory, setShowNewCategory] = useState(false);

  const loadItems = async (includeInactive = showInactive) => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.listItems(includeInactive);
      setItems(data);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadItems();
  }, []);

  // Extract unique categories from items
  const categories = useMemo(() => {
    const unique = new Set(items.map((item) => item.category));
    return Array.from(unique).sort();
  }, [items]);

  const visibleItems = useMemo(() => {
    const search = query.trim().toLowerCase();
    return items.filter((item) => {
      const matchesCategory = categoryFilter === 'all' || item.category === categoryFilter;
      const matchesSearch = !search || 
        item.name.toLowerCase().includes(search) ||
        item.itemCode.toLowerCase().includes(search);
      return matchesCategory && matchesSearch;
    });
  }, [items, query, categoryFilter]);

  const resetForm = () => {
    setDraft(emptyDraft);
    setEditId(null);
    setShowNewCategory(false);
  };

  const handleEdit = (item: Item) => {
    setDraft({
      name: item.name,
      category: item.category,
      price: item.price.toFixed(2),
      itemCode: item.itemCode,
      pctCode: item.pctCode,
    });
    setEditId(item.id);
    setShowNewCategory(false);
  };

  const handleCategorySelect = (value: string) => {
    if (value === '__new__') {
      setShowNewCategory(true);
      setDraft((prev) => ({ ...prev, category: '' }));
    } else {
      setShowNewCategory(false);
      setDraft((prev) => ({ ...prev, category: value }));
    }
  };

  const handleSubmit = async () => {
    if (
      !draft.name.trim() ||
      !draft.category.trim() ||
      !draft.price.trim() ||
      !draft.itemCode.trim() ||
      !draft.pctCode.trim()
    ) {
      setError('All fields are required.');
      return;
    }
    const price = Number(draft.price);
    if (Number.isNaN(price) || price <= 0) {
      setError('Price must be a positive number.');
      return;
    }
    setBusy(true);
    setError(null);
    try {
      if (editId) {
        await api.updateItem(editId, {
          name: draft.name,
          category: draft.category,
          price,
          itemCode: draft.itemCode,
          pctCode: draft.pctCode,
        });
      } else {
        await api.createItem({
          name: draft.name,
          category: draft.category,
          price,
          itemCode: draft.itemCode,
          pctCode: draft.pctCode,
        });
      }
      resetForm();
      await loadItems();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusy(false);
    }
  };

  const handleToggleActive = async (id: string) => {
    setBusy(true);
    setError(null);
    try {
      await api.toggleItemActive(id);
      if (editId === id) {
        resetForm();
      }
      await loadItems();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <section className="section-card">
      <div className="section-header">
        <div>
          <h1 className="panel-title">Items Admin</h1>
          <p className="muted">Add items that will appear on the POS invoice screen.</p>
        </div>
        <div className="controls">
          <label className="toggle">
            <input
              type="checkbox"
              checked={showInactive}
              onChange={(event) => {
                const next = event.target.checked;
                setShowInactive(next);
                loadItems(next);
              }}
            />
            <span>Show inactive</span>
          </label>
          <button className="button button-secondary" onClick={() => loadItems()} disabled={loading || busy}>
            Refresh
          </button>
        </div>
      </div>
      {error && <div className="alert">{error}</div>}
      <div className="form-grid form-grid-2">
        <label className="form-field">
          <span>Item name</span>
          <input
            className="input"
            placeholder="e.g. Cappuccino"
            value={draft.name}
            onChange={(event) => setDraft((prev) => ({ ...prev, name: event.target.value }))}
          />
        </label>
        <div className="form-field">
          <span>Category</span>
          {showNewCategory ? (
            <div className="input-with-action">
              <input
                className="input"
                placeholder="Enter new category name"
                value={draft.category}
                onChange={(event) => setDraft((prev) => ({ ...prev, category: event.target.value }))}
                autoFocus
              />
              <button
                type="button"
                className="button button-ghost"
                onClick={() => {
                  setShowNewCategory(false);
                  setDraft((prev) => ({ ...prev, category: categories[0] || '' }));
                }}
              >
                Cancel
              </button>
            </div>
          ) : (
            <select
              className="input"
              value={draft.category || ''}
              onChange={(event) => handleCategorySelect(event.target.value)}
            >
              <option value="" disabled>Select a category</option>
              {categories.map((cat) => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
              <option value="__new__">+ Add new category</option>
            </select>
          )}
        </div>
        <label className="form-field">
          <span>Item code</span>
          <input
            className="input"
            placeholder="Internal SKU or item code"
            value={draft.itemCode}
            onChange={(event) => setDraft((prev) => ({ ...prev, itemCode: event.target.value }))}
          />
        </label>
        <label className="form-field">
          <span>PCT code (8 chars)</span>
          <input
            className="input"
            placeholder="e.g. 00000000"
            value={draft.pctCode}
            maxLength={8}
            onChange={(event) => setDraft((prev) => ({ ...prev, pctCode: event.target.value }))}
          />
        </label>
        <label className="form-field">
          <span>Price</span>
          <input
            className="input"
            placeholder="0.00"
            type="number"
            min="0"
            step="0.01"
            value={draft.price}
            onChange={(event) => setDraft((prev) => ({ ...prev, price: event.target.value }))}
          />
        </label>
        <div className="button-group">
          <button className="button button-primary" onClick={handleSubmit} disabled={busy}>
            {editId ? 'Update item' : 'Add item'}
          </button>
          {editId && (
            <button className="button button-ghost" onClick={resetForm} disabled={busy}>
              Cancel
            </button>
          )}
        </div>
      </div>
      <div className="section-separator" />
      <div className="section-header">
        <div className="muted">
          {categoryFilter === 'all' 
            ? `All items (${visibleItems.length})` 
            : `${categoryFilter} (${visibleItems.length})`}
        </div>
        <div className="controls">
          <select
            className="input input-compact"
            value={categoryFilter}
            onChange={(event) => setCategoryFilter(event.target.value)}
          >
            <option value="all">All categories</option>
            {categories.map((cat) => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </select>
          <input
            className="input input-compact"
            placeholder="Search by name or code"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
          />
        </div>
      </div>
      <table className="table-shell">
        <thead>
          <tr>
            <th>Name</th>
            <th>Item Code</th>
            <th>PCT Code</th>
            <th>Category</th>
            <th>Price</th>
            <th>Status</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr>
              <td colSpan={7} className="muted">
                Loading items...
              </td>
            </tr>
          ) : (
            visibleItems.map((item) => (
              <tr key={item.id}>
                <td>{item.name}</td>
                <td>{item.itemCode}</td>
                <td>{item.pctCode}</td>
                <td>{item.category}</td>
                <td>{currency.format(item.price)}</td>
                <td>
                  <span className="badge">{item.isActive ? 'Active' : 'Inactive'}</span>
                </td>
                <td>
                  <div className="row-actions">
                    <button className="button button-ghost" onClick={() => handleEdit(item)} disabled={busy}>
                      Edit
                    </button>
                    <button
                      className={`button ${item.isActive ? 'button-danger' : 'button-primary'}`}
                      onClick={() => handleToggleActive(item.id)}
                      disabled={busy}
                    >
                      {item.isActive ? 'Deactivate' : 'Activate'}
                    </button>
                  </div>
                </td>
              </tr>
            ))
          )}
          {!loading && visibleItems.length === 0 && (
            <tr>
              <td colSpan={7} className="muted">
                No items match this filter.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </section>
  );
}
