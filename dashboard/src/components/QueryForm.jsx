import React, { useState } from 'react';

const QueryForm = ({ onSubmit }) => {
  const [query, setQuery] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (query.trim()) {
      onSubmit(query);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: 20 }}>
      <label htmlFor="query">Enter SQL Query:</label>
      <textarea
        id="query"
        value={query}
        onChange={e => setQuery(e.target.value)}
        rows={4}
        style={{ width: '100%', marginTop: 8 }}
        placeholder="SELECT * FROM orders WHERE customer_id = 101"
      />
      <button type="submit" style={{ marginTop: 10 }}>Submit</button>
    </form>
  );
};

export default QueryForm;
