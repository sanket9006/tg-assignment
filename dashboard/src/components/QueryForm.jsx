import React, { useState } from 'react';

const presetQueries = [
  { label: 'Deeply Nested Subqueries', query: "SELECT CustomerID, CompanyName, (SELECT SUM(UnitPrice * Quantity) FROM [Order Details] od JOIN Orders o2 ON od.OrderID = o2.OrderID WHERE o2.CustomerID = Customers.CustomerID) AS TotalSpent FROM Customers WHERE CustomerID IN (SELECT CustomerID FROM Orders WHERE OrderID IN (SELECT OrderID FROM [Order Details] WHERE ProductID IN (SELECT ProductID FROM Products WHERE CategoryID = 1))) ORDER BY TotalSpent DESC" },
  { label: '7-Table JOIN', query: "SELECT reg.RegionDescription, t.TerritoryDescription, e.FirstName || ' ' || e.LastName AS EmployeeName, cust.CompanyName, SUM(od.UnitPrice * od.Quantity * (1 - od.Discount)) AS OrderTotal FROM Region reg INNER JOIN Territories t ON reg.RegionID = t.RegionID INNER JOIN EmployeeTerritories et ON t.TerritoryID = et.TerritoryID INNER JOIN Employees e ON et.EmployeeID = e.EmployeeID INNER JOIN Orders o ON e.EmployeeID = o.EmployeeID INNER JOIN Customers cust ON o.CustomerID = cust.CustomerID INNER JOIN [Order Details] od ON o.OrderID = od.OrderID WHERE o.OrderDate >= '1997-01-01' GROUP BY reg.RegionDescription, t.TerritoryDescription, e.EmployeeID, cust.CustomerID HAVING OrderTotal > 5000 ORDER BY OrderTotal DESC" },
  { label: 'Complex CTE & Window', query: "WITH SalesCTE AS (SELECT o.EmployeeID, strftime('%Y', o.OrderDate) AS OrderYear, SUM(od.UnitPrice * od.Quantity) AS TotalSales FROM Orders o JOIN [Order Details] od ON o.OrderID = od.OrderID GROUP BY o.EmployeeID, strftime('%Y', o.OrderDate)), RankedSales AS (SELECT EmployeeID, OrderYear, TotalSales, RANK() OVER (PARTITION BY OrderYear ORDER BY TotalSales DESC) as SalesRank FROM SalesCTE) SELECT e.FirstName, e.LastName, rs.OrderYear, rs.TotalSales, rs.SalesRank FROM RankedSales rs JOIN Employees e ON rs.EmployeeID = e.EmployeeID WHERE rs.SalesRank <= 3 ORDER BY rs.OrderYear DESC, rs.SalesRank" },
  { label: 'Massive CASE Pivot', query: "SELECT o.CustomerID, SUM(CASE WHEN strftime('%m', o.OrderDate) = '01' THEN od.UnitPrice * od.Quantity ELSE 0 END) AS Jan_Sales, SUM(CASE WHEN strftime('%m', o.OrderDate) = '02' THEN od.UnitPrice * od.Quantity ELSE 0 END) AS Feb_Sales, SUM(CASE WHEN strftime('%m', o.OrderDate) = '03' THEN od.UnitPrice * od.Quantity ELSE 0 END) AS Mar_Sales, SUM(CASE WHEN strftime('%m', o.OrderDate) = '04' THEN od.UnitPrice * od.Quantity ELSE 0 END) AS Apr_Sales, SUM(CASE WHEN strftime('%m', o.OrderDate) = '05' THEN od.UnitPrice * od.Quantity ELSE 0 END) AS May_Sales, SUM(CASE WHEN strftime('%m', o.OrderDate) = '06' THEN od.UnitPrice * od.Quantity ELSE 0 END) AS Jun_Sales FROM Orders o JOIN [Order Details] od ON o.OrderID = od.OrderID GROUP BY o.CustomerID" },
  { label: 'Recursive CTE', query: "WITH RECURSIVE EmployeeHierarchy AS (SELECT EmployeeID, FirstName, LastName, ReportsTo, 1 AS Level FROM Employees WHERE ReportsTo IS NULL UNION ALL SELECT e.EmployeeID, e.FirstName, e.LastName, e.ReportsTo, eh.Level + 1 FROM Employees e INNER JOIN EmployeeHierarchy eh ON e.ReportsTo = eh.EmployeeID) SELECT * FROM EmployeeHierarchy ORDER BY Level, ReportsTo" },
  { label: 'Correlated EXISTS', query: "SELECT p.ProductName, p.UnitPrice, c.CategoryName FROM Products p JOIN Categories c ON p.CategoryID = c.CategoryID WHERE EXISTS (SELECT 1 FROM [Order Details] od JOIN Orders o ON od.OrderID = o.OrderID WHERE od.ProductID = p.ProductID AND o.OrderDate >= '1998-01-01') AND NOT EXISTS (SELECT 1 FROM [Order Details] od JOIN Orders o ON od.OrderID = o.OrderID WHERE od.ProductID = p.ProductID AND o.ShipCountry = 'France') AND p.UnitPrice > (SELECT AVG(UnitPrice) FROM Products WHERE CategoryID = p.CategoryID)" },
  { label: 'Massive UNION ALL', query: "SELECT 'Customer' AS Type, CustomerID AS ID, CompanyName AS Name, City, Country FROM Customers WHERE Country IN ('USA', 'UK', 'Canada') UNION ALL SELECT 'Supplier' AS Type, SupplierID AS ID, CompanyName AS Name, City, Country FROM Suppliers WHERE Country IN ('USA', 'UK', 'Canada') UNION ALL SELECT 'Employee' AS Type, EmployeeID AS ID, FirstName || ' ' || LastName AS Name, City, Country FROM Employees WHERE Country IN ('USA', 'UK') ORDER BY Country, Type, Name" },
  { label: 'Heavy String & Math', query: "SELECT p.ProductName, UPPER(SUBSTR(c.CategoryName, 1, 3)) || '-' || printf('%04d', p.ProductID) AS ProductCode, ROUND(p.UnitPrice * 1.25, 2) AS MarkupPrice, COALESCE(p.UnitsInStock, 0) + COALESCE(p.UnitsOnOrder, 0) AS TotalInventory, CASE WHEN p.UnitsInStock = 0 THEN 'OUT OF STOCK' WHEN p.UnitsInStock < p.ReorderLevel THEN 'REORDER NOW' ELSE 'HEALTHY' END AS StockStatus FROM Products p JOIN Categories c ON p.CategoryID = c.CategoryID WHERE p.Discontinued = 0" },
  { label: 'Self Join & Anti-Join', query: "SELECT e1.FirstName || ' ' || e1.LastName AS Manager, e2.FirstName || ' ' || e2.LastName AS Employee, COUNT(o.OrderID) AS EmployeeOrders FROM Employees e1 JOIN Employees e2 ON e1.EmployeeID = e2.ReportsTo LEFT JOIN Orders o ON e2.EmployeeID = o.EmployeeID LEFT JOIN Customers c ON o.CustomerID = c.CustomerID AND c.Region IS NULL GROUP BY e1.EmployeeID, e2.EmployeeID HAVING COUNT(o.OrderID) > 10 ORDER BY EmployeeOrders DESC" },
  { label: 'Parameter Extraction Stress', query: "SELECT * FROM Products WHERE ProductID IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80) AND UnitPrice BETWEEN 10.55 AND 99.99 AND UnitsInStock > 0 AND Discontinued = 0 ORDER BY UnitPrice DESC LIMIT 15 OFFSET 5" }
];

const QueryForm = ({ onSubmit }) => {
  const [query, setQuery] = useState('');
  const [copiedIndex, setCopiedIndex] = useState(null);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (query.trim()) {
      onSubmit(query);
    }
  };

  const handlePresetClick = (q, i) => {
    setQuery(q.query);
    navigator.clipboard.writeText(q.query);
    setCopiedIndex(i);
    setTimeout(() => setCopiedIndex(null), 2000);
  };

  return (
    <div style={{ textAlign: 'left', marginBottom: 30 }}>
      <div style={{ display: 'flex', gap: '8px', marginBottom: '15px', flexWrap: 'wrap' }}>
        {presetQueries.map((q, i) => (
          <button 
            key={i} 
            type="button"
            onClick={() => handlePresetClick(q, i)}
            style={{ 
              fontSize: '12px', 
              padding: '6px 12px', 
              borderRadius: '20px', 
              background: copiedIndex === i ? '#10b981' : 'var(--accent-bg)', 
              border: copiedIndex === i ? '1px solid #10b981' : '1px solid var(--accent-border)', 
              color: copiedIndex === i ? '#fff' : 'var(--accent)', 
              cursor: 'pointer', 
              transition: 'all 0.2s ease', 
              fontWeight: '500' 
            }}
            className="preset-btn"
          >
            {copiedIndex === i ? 'Copied!' : q.label}
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
