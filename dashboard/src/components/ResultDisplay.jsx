import React from 'react';

const ResultDisplay = ({ result, error }) => {
  if (error) {
    return <div style={{ color: 'red' }}>Error: {error}</div>;
  }
  if (result) {
    return (
      <div style={{ background: '#f4f4f4', padding: 16, borderRadius: 4, marginTop: 15 }}>
        <strong>Query Plan:</strong>
        <pre style={{ background: '#ddd', padding: 10, borderRadius: 4 }}>{result.plan}</pre>
        <div style={{ marginTop: 10 }}>
          <strong>Extracted Parameters:</strong> {result.parameters && result.parameters.length > 0 ? JSON.stringify(result.parameters) : 'None'}
        </div>
        <div style={{ marginTop: 10 }}>
          <strong>Cache Status:</strong> {result.cacheHit ? <span style={{ color: 'green', fontWeight: 'bold' }}>HIT</span> : <span style={{ color: 'orange', fontWeight: 'bold' }}>MISS</span>}
        </div>
        <div style={{ marginTop: 10 }}>
          <strong>Execution Time:</strong> {result.executionTimeMs} ms
        </div>
      </div>
    );
  }
  return null;
};

export default ResultDisplay;
