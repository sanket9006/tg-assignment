import React, { useState } from 'react';

const presetQueries = [
  "SELECT u.name, COUNT(*) FROM users u JOIN orders o ON u.id = o.user_id WHERE u.status = 'ACTIVE' GROUP BY u.name",
  "SELECT p.category, AVG(p.price) FROM products p WHERE p.stock > 0 GROUP BY p.category HAVING AVG(p.price) > 50"
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
      <div style={{ display: 'flex', gap: '10px', marginBottom: '15px', flexWrap: 'wrap' }}>
        {presetQueries.map((q, i) => (
          <button 
            key={i} 
            type="button"
            onClick={() => setQuery(q)}
            style={{ fontSize: '13px', padding: '8px 16px', borderRadius: '20px', background: 'var(--accent-bg)', border: '1px solid var(--accent-border)', color: 'var(--accent)', cursor: 'pointer', transition: 'all 0.2s ease', fontWeight: '500' }}
            className="preset-btn"
          >
            Example Query {i + 1}
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
