import React, { useState } from 'react';

const presetQueries = [
  { label: 'Basic SELECT', query: "SELECT * FROM users WHERE age > 25 ORDER BY created_at DESC LIMIT 10" },
  { label: 'JOIN & GROUP', query: "SELECT u.name, COUNT(*) FROM users u JOIN orders o ON u.id = o.user_id WHERE u.status = 'ACTIVE' GROUP BY u.name" },
  { label: 'HAVING Clause', query: "SELECT p.category, AVG(p.price) FROM products p WHERE p.stock > 0 GROUP BY p.category HAVING AVG(p.price) > 50" },
  { label: 'Subquery', query: "SELECT name, email FROM customers WHERE id IN (SELECT customer_id FROM orders WHERE total > 1000)" },
  { label: 'CTE (WITH)', query: "WITH top_users AS (SELECT id FROM users WHERE score > 90) SELECT * FROM top_users JOIN profiles ON top_users.id = profiles.user_id" },
  { label: 'INSERT', query: "INSERT INTO audit_logs (event_type, description, user_id) VALUES ('LOGIN', 'User logged in successfully', 42)" },
  { label: 'UPDATE', query: "UPDATE inventory SET stock = stock - 5 WHERE product_id = 101 AND warehouse_id = 'WH-EAST'" },
  { label: 'DELETE', query: "DELETE FROM expired_sessions WHERE last_active < '2023-01-01' AND session_type = 'WEB'" },
  { label: 'Aggregations', query: "SELECT date(transaction_time) as day, SUM(amount) FROM sales GROUP BY day ORDER BY SUM(amount) DESC" },
  { label: 'CROSS JOIN', query: "SELECT a.id, b.id FROM nodes a CROSS JOIN nodes b WHERE a.parent_id = b.id" }
];

const QueryForm = ({ onSubmit }) => {
  const [query, setQuery] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (query.trim()) {
      onSubmit(query);
    }
  };

  return (
    <div style={{ textAlign: 'left', marginBottom: 30 }}>
      <div style={{ display: 'flex', gap: '8px', marginBottom: '15px', flexWrap: 'wrap' }}>
        {presetQueries.map((q, i) => (
          <button 
            key={i} 
            type="button"
            onClick={() => setQuery(q.query)}
            style={{ fontSize: '12px', padding: '6px 12px', borderRadius: '20px', background: 'var(--accent-bg)', border: '1px solid var(--accent-border)', color: 'var(--accent)', cursor: 'pointer', transition: 'all 0.2s ease', fontWeight: '500' }}
            className="preset-btn"
          >
            {q.label}
          </button>
        ))}
      </div>
      <form onSubmit={handleSubmit}>
        <textarea
          id="query"
          value={query}
          onChange={e => setQuery(e.target.value)}
          rows={5}
          style={{ width: '100%', padding: '15px', borderRadius: '12px', border: '1px solid var(--border)', background: 'var(--bg)', color: 'var(--text-h)', fontSize: '16px', fontFamily: 'var(--mono)', boxSizing: 'border-box', resize: 'vertical', boxShadow: 'inset 0 2px 4px rgba(0,0,0,0.05)' }}
          placeholder="Type your SQL query here..."
        />
        <button className="submit-btn" type="submit" style={{ marginTop: 15, width: '100%', padding: '14px', borderRadius: '12px', background: 'var(--accent)', color: '#fff', border: 'none', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer', transition: 'all 0.2s ease', boxShadow: '0 4px 6px var(--accent-border)' }}>Analyze Query</button>
      </form>
    </div>
  );
};

export default QueryForm;
