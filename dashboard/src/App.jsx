import React, { useState } from 'react';
import QueryForm from './components/QueryForm';
import ResultDisplay from './components/ResultDisplay';

function App() {
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState(null);

  const BACKEND_URL = import.meta.env.VITE_BACKEND_URL || 'https://tg-assignment-xr8i.onrender.com';

  const handleQuerySubmit = async (query) => {
    setLoading(true);
    setResult(null);
    setError(null);
    try {
      const response = await fetch(`${BACKEND_URL}/api/query/plan`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query })
      });
      const data = await response.json();
      if (data.error) {
        setError(data.error);
      } else {
        setResult(data);
      }
    } catch (err) {
      setError('Failed to connect to backend.');
    }
    setLoading(false);
  };

  const fetchStats = async () => {
    try {
      const response = await fetch(`${BACKEND_URL}/api/query/stats`);
      const data = await response.json();
      setStats(data);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div style={{ maxWidth: 600, margin: '40px auto', fontFamily: 'Arial, sans-serif' }}>
      <h2>SQL Query Plan Dashboard</h2>
      <QueryForm onSubmit={handleQuerySubmit} />
      {loading && <div>Loading...</div>}
      <ResultDisplay result={result} error={error} />
      
      <div style={{ marginTop: '30px', padding: '15px', borderTop: '1px solid #ccc' }}>
        <button onClick={fetchStats} style={{ padding: '8px 16px', cursor: 'pointer' }}>
          Refresh Cache Stats
        </button>
        {stats && (
          <div style={{ marginTop: '15px' }}>
            <strong>Total Cache Hits:</strong> {stats.hits} <br />
            <strong>Total Cache Misses:</strong> {stats.misses}
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
