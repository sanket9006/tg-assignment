import React from 'react';

const ResultDisplay = ({ result, error }) => {
  if (error) {
    return <div style={{ color: '#ef4444', background: '#fee2e2', padding: '15px', borderRadius: '8px', border: '1px solid #f87171' }}><strong>Error:</strong> {error}</div>;
  }
  if (result) {
    return (
      <div style={{ background: 'var(--social-bg)', padding: 25, borderRadius: 12, marginTop: 20, textAlign: 'left', border: '1px solid var(--border)', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
        <h3 style={{ marginTop: 0, color: 'var(--text-h)', fontSize: '20px' }}>Normalized Structure</h3>
        <div style={{ background: 'var(--bg)', padding: '12px 15px', borderRadius: '8px', border: '1px solid var(--border)', marginBottom: '20px', fontFamily: 'var(--mono)', fontSize: '14px', color: 'var(--accent)', wordBreak: 'break-word' }}>
          {result.normalizedSql}
        </div>

        <h3 style={{ marginTop: 0, color: 'var(--text-h)', fontSize: '20px' }}>Query Plan</h3>
        <pre style={{ background: 'var(--code-bg)', padding: 15, borderRadius: 8, whiteSpace: 'pre-wrap', wordWrap: 'break-word', border: '1px solid var(--border)', fontFamily: 'var(--mono)', fontSize: '15px', color: 'var(--text-h)' }}>
          {result.plan}
        </pre>
        <div style={{ marginTop: 20, display: 'grid', gap: '10px' }}>
          <div style={{ background: 'var(--bg)', padding: '12px 15px', borderRadius: '8px', border: '1px solid var(--border)' }}>
            <strong style={{ color: 'var(--text-h)' }}>Extracted Parameters:</strong> <code style={{ color: 'var(--accent)' }}>{result.parameters && result.parameters.length > 0 ? JSON.stringify(result.parameters) : 'None'}</code>
          </div>
          <div style={{ background: 'var(--bg)', padding: '12px 15px', borderRadius: '8px', border: '1px solid var(--border)' }}>
            <strong style={{ color: 'var(--text-h)' }}>Cache Status:</strong> {result.cacheHit ? <span style={{ color: '#10b981', fontWeight: 'bold', background: 'rgba(16, 185, 129, 0.1)', padding: '4px 8px', borderRadius: '4px' }}>HIT</span> : <span style={{ color: '#f59e0b', fontWeight: 'bold', background: 'rgba(245, 158, 11, 0.1)', padding: '4px 8px', borderRadius: '4px' }}>MISS</span>}
          </div>
          <div style={{ background: 'var(--bg)', padding: '12px 15px', borderRadius: '8px', border: '1px solid var(--border)' }}>
            <strong style={{ color: 'var(--text-h)' }}>Execution Time:</strong> <span style={{ color: 'var(--text)' }}>{result.executionTimeMs} ms</span>
          </div>
        </div>
      </div>
    );
  }
  return null;
};

export default ResultDisplay;
