import React, { useState } from 'react';
import QueryForm from './components/QueryForm';
import ResultDisplay from './components/ResultDisplay';

function App() {
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState(null);
  const [cacheDump, setCacheDump] = useState(null);

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

  const fetchCacheDump = async () => {
    try {
      const response = await fetch(`${BACKEND_URL}/api/query/cache-dump`);
      const data = await response.json();
      setCacheDump(data);
    } catch (err) {
      console.error(err);
    }
  };

  const clearCache = async () => {
    try {
      await fetch(`${BACKEND_URL}/api/query/clear-cache`, { method: 'POST' });
      fetchStats();
      setResult(null);
      setCacheDump(null);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div style={{ width: '100%', maxWidth: '100%' }}>
      {/* Top dashboard section (constrained width with padding) */}
      <div style={{ padding: '50px 20px', maxWidth: '800px', margin: '0 auto' }}>
        <h1 style={{ background: '-webkit-linear-gradient(45deg, var(--accent), #f06)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', marginBottom: '20px', paddingBottom: '10px', lineHeight: '1.2' }}>Query Plan Dashboard</h1>
        
        <div style={{ marginBottom: '30px', padding: '15px 20px', borderRadius: '8px', backgroundColor: 'var(--social-bg)', borderLeft: '4px solid #f59e0b', color: 'var(--text)', fontSize: '14px', textAlign: 'left', display: 'flex', alignItems: 'flex-start', gap: '12px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
          <span style={{ fontSize: '24px', marginTop: '2px' }}>💡</span>
          <div style={{ lineHeight: '1.5' }}>
            <p style={{ marginBottom: '8px' }}>
              <strong>Notice:</strong> The backend is deployed on Render.com. If the service is idle, it takes around <strong>50 seconds</strong> to start the container, so you might experience an initial delay.
            </p>
            <p>
              This app is powered by an <strong>SQLite database</strong> using the sample <a href="https://github.com/sanket9006/tg-assignment/blob/master/northwind.db" target="_blank" rel="noopener noreferrer" style={{ color: 'var(--accent)', textDecoration: 'underline' }}>northwind.db</a> schema. You are welcome to submit your own custom queries to test it out!
            </p>
          </div>
        </div>
        <QueryForm onSubmit={handleQuerySubmit} />
        {loading && <div style={{ padding: '20px', color: 'var(--accent)', fontWeight: 'bold' }}>Analyzing Query...</div>}
        <ResultDisplay result={result} error={error} />
      </div>

      {/* Cache dump section (full width) */}
      <div style={{ padding: '40px 0', background: 'var(--social-bg)', borderTop: '1px solid var(--border)', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div style={{ display: 'flex', gap: '15px' }}>
          <button onClick={fetchStats} className="submit-btn" style={{ padding: '10px 20px', borderRadius: '8px', background: 'transparent', border: '2px solid var(--accent)', color: 'var(--accent)', cursor: 'pointer', fontWeight: 'bold', transition: 'all 0.2s ease' }}>
            Refresh Cache Stats
          </button>
          <button onClick={fetchCacheDump} className="submit-btn" style={{ padding: '10px 20px', borderRadius: '8px', background: 'transparent', border: '2px solid #8b5cf6', color: '#8b5cf6', cursor: 'pointer', fontWeight: 'bold', transition: 'all 0.2s ease' }}>
            View Cache Dump
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
        
        {cacheDump && (
          <div style={{ marginTop: '30px', width: '100%', padding: '0 20px', boxSizing: 'border-box', textAlign: 'left' }}>
            <h3 style={{ marginTop: 0, color: 'var(--text-h)', marginBottom: '20px', fontSize: '20px', paddingLeft: '10px' }}>Cache Contents ({Object.keys(cacheDump).length} entries)</h3>
            {Object.keys(cacheDump).length === 0 ? (
              <p style={{ color: 'var(--text)', opacity: 0.7, paddingLeft: '10px' }}>Cache is empty.</p>
            ) : (
              <div style={{ maxHeight: '600px', overflowY: 'auto', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)', background: 'var(--bg)' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
                  <thead style={{ position: 'sticky', top: 0, background: 'var(--bg)', zIndex: 1, borderBottom: '2px solid var(--border)' }}>
                    <tr>
                      <th style={{ padding: '12px 15px', textAlign: 'left', color: 'var(--text-h)', fontWeight: 'bold', width: '15%' }}>Hash Key</th>
                      <th style={{ padding: '12px 15px', textAlign: 'left', color: 'var(--text-h)', fontWeight: 'bold', width: '30%' }}>Normalized SQL</th>
                      <th style={{ padding: '12px 15px', textAlign: 'left', color: 'var(--text-h)', fontWeight: 'bold', width: '5%' }}>Ver</th>
                      <th style={{ padding: '12px 15px', textAlign: 'left', color: 'var(--text-h)', fontWeight: 'bold', width: '10%' }}>Parameters</th>
                      <th style={{ padding: '12px 15px', textAlign: 'left', color: 'var(--text-h)', fontWeight: 'bold', width: '30%' }}>Strategy</th>
                      <th style={{ padding: '12px 15px', textAlign: 'left', color: 'var(--text-h)', fontWeight: 'bold', width: '10%' }}>Cached At</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(cacheDump).map(([key, value], index) => {
                      // Handle both old String format and new CachedEntry object format
                      const isObject = typeof value === 'object' && value !== null;
                      const entry = isObject ? value : { plan: value, normalizedSql: 'N/A', version: 'N/A', parameters: [], cachedAt: 0 };
                      
                      let planInfo = {};
                      try {
                        const parsed = JSON.parse(entry.plan);
                        planInfo = parsed.execution_plan || {};
                      } catch (e) {
                        planInfo = { strategy: 'Invalid JSON' };
                      }
                      
                      const cachedTime = entry.cachedAt > 0 
                        ? new Date(entry.cachedAt * 1000).toLocaleTimeString() 
                        : 'Unknown';
                      
                      return (
                        <tr key={index} style={{ borderBottom: '1px solid var(--border)', background: index % 2 === 0 ? 'transparent' : 'rgba(0,0,0,0.1)' }}>
                          <td style={{ padding: '15px', color: '#94a3b8', fontFamily: 'var(--mono)', fontSize: '12px' }}>
                            {key}
                          </td>
                          <td style={{ padding: '15px', color: 'var(--accent)', fontFamily: 'var(--mono)', fontSize: '13px', wordBreak: 'break-word', lineHeight: '1.5' }}>
                            {isObject ? entry.normalizedSql : key}
                          </td>
                          <td style={{ padding: '15px', color: 'var(--text)', fontSize: '13px' }}>
                            {entry.version}
                          </td>
                          <td style={{ padding: '15px', color: 'var(--text)', fontSize: '13px', fontFamily: 'var(--mono)' }}>
                            {entry.parameters && entry.parameters.length > 0 ? JSON.stringify(entry.parameters) : '[]'}
                          </td>
                          <td style={{ padding: '15px', color: 'var(--text)', fontSize: '13px', lineHeight: '1.5' }}>
                            {planInfo.strategy || 'N/A'}
                          </td>
                          <td style={{ padding: '15px', color: 'var(--text)', fontSize: '12px', opacity: 0.8 }}>
                            {cachedTime}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
            <div style={{ marginTop: '15px', padding: '15px', borderRadius: '8px', backgroundColor: 'rgba(56, 189, 248, 0.05)', border: '1px solid rgba(56, 189, 248, 0.2)', fontSize: '13px', color: 'var(--text)', lineHeight: '1.6' }}>
              <strong style={{ color: 'var(--accent)' }}>💡 Technical Note:</strong> The cache uses a <strong>64-bit Long Hash Key</strong> (derived from SHA-256) instead of raw SQL strings to optimize memory usage. Each entry stores a <strong>CachedEntry object</strong> containing the execution plan, normalized SQL structure, extracted parameters, and metadata for future-proofing.
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
