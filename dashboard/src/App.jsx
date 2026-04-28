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

  const clearCache = async () => {
    try {
      await fetch(`${BACKEND_URL}/api/query/clear-cache`, { method: 'POST' });
      fetchStats();
      setResult(null);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div style={{ width: '100%', maxWidth: '100%' }}>
      <h1 style={{ background: '-webkit-linear-gradient(45deg, var(--accent), #f06)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', marginBottom: '20px', paddingBottom: '10px', lineHeight: '1.2' }}>Query Plan Dashboard</h1>
      
      <div style={{ marginBottom: '30px', padding: '15px 20px', borderRadius: '8px', backgroundColor: 'var(--social-bg)', borderLeft: '4px solid #f59e0b', color: 'var(--text)', fontSize: '14px', textAlign: 'left', display: 'flex', alignItems: 'center', gap: '12px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
        <span style={{ fontSize: '24px' }}>⏳</span>
        <div>
          <strong>Notice:</strong> The backend is deployed on Render.com. If the service is idle, it takes around <strong>50 seconds</strong> to start the container, so you might experience an initial delay.
        </div>
      </div>
      <QueryForm onSubmit={handleQuerySubmit} />
      {loading && <div style={{ padding: '20px', color: 'var(--accent)', fontWeight: 'bold' }}>Analyzing Query...</div>}
      <ResultDisplay result={result} error={error} />

      <div style={{ marginTop: '40px', padding: '25px', borderRadius: '12px', background: 'var(--social-bg)', border: '1px solid var(--border)', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div style={{ display: 'flex', gap: '15px' }}>
          <button onClick={fetchStats} className="submit-btn" style={{ padding: '10px 20px', borderRadius: '8px', background: 'transparent', border: '2px solid var(--accent)', color: 'var(--accent)', cursor: 'pointer', fontWeight: 'bold', transition: 'all 0.2s ease' }}>
            Refresh Cache Stats
          </button>
          <button onClick={clearCache} className="submit-btn" style={{ padding: '10px 20px', borderRadius: '8px', background: 'transparent', border: '2px solid #ef4444', color: '#ef4444', cursor: 'pointer', fontWeight: 'bold', transition: 'all 0.2s ease' }}>
            Clear Cache
          </button>
        </div>
        {stats && (
          <div style={{ marginTop: '20px', display: 'flex', gap: '30px', fontSize: '18px' }}>
            <div style={{ background: 'var(--bg)', padding: '15px 25px', borderRadius: '8px', border: '1px solid var(--border)', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
              <span style={{ color: 'var(--text)' }}>Total Hits</span><br />
              <strong style={{ fontSize: '28px', color: '#10b981' }}>{stats.hits}</strong>
            </div>
            <div style={{ background: 'var(--bg)', padding: '15px 25px', borderRadius: '8px', border: '1px solid var(--border)', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
              <span style={{ color: 'var(--text)' }}>Total Misses</span><br />
              <strong style={{ fontSize: '28px', color: '#f59e0b' }}>{stats.misses}</strong>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
