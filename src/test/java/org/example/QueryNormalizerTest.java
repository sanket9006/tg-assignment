package org.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class QueryNormalizerTest {

    // Helper method to simulate exactly what happens in QueryPlanCache
    private QueryNormalizer normalizeQuery(String sql) {
        SQLiteLexer lexer = new SQLiteLexer(CharStreams.fromString(sql));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLiteParser parser = new SQLiteParser(tokens);
        ParseTree tree = parser.parse();

        QueryNormalizer normalizer = new QueryNormalizer();
        normalizer.normalize(tree);
        return normalizer;
    }

    @Test
    public void testEquivalence_SpacingAndCasing() {
        // Correctness Check: Structurally identical queries must resolve to the same cache key
        String queryA = "SELECT * FROM Employees WHERE EmployeeID > 5";
        String queryB = "select    * from EMPLOYEES   where EmployeeID > 10";

        QueryNormalizer normA = normalizeQuery(queryA);
        QueryNormalizer normB = normalizeQuery(queryB);

        // 1. Assert Cache Keys are EXACTLY the same
        assertEquals(normA.getNormalizedSql(), normB.getNormalizedSql());
        assertEquals("SELECT * FROM EMPLOYEES WHERE EMPLOYEEID > ?", normA.getNormalizedSql());

        // 2. Assert Parameters were correctly extracted and ordered
        assertEquals("5", normA.getParameters().get(0));
        assertEquals("10", normB.getParameters().get(0));
    }

    @Test
    public void testEquivalence_StringsAndQuotes() {
        String queryA = "SELECT * FROM Orders WHERE ShipCity = 'London'";
        String queryB = "SELECT * FROM Orders WHERE ShipCity = 'Paris'";

        QueryNormalizer normA = normalizeQuery(queryA);
        QueryNormalizer normB = normalizeQuery(queryB);

        assertEquals("Cache keys must match", normA.getNormalizedSql(), normB.getNormalizedSql());
        assertEquals("'London'", normA.getParameters().get(0));
        assertEquals("'Paris'", normB.getParameters().get(0));
    }

    @Test
    public void testDivergence_DifferentStructure() {
        // Correctness Check: Structurally different queries must NEVER resolve to the same cache key
        String queryC = "SELECT FirstName FROM Employees WHERE EmployeeID > 5";
        String queryD = "SELECT LastName FROM Employees WHERE EmployeeID > 5";

        QueryNormalizer normC = normalizeQuery(queryC);
        QueryNormalizer normD = normalizeQuery(queryD);

        assertNotEquals("Cache keys must NOT match for structurally different queries", 
                normC.getNormalizedSql(), normD.getNormalizedSql());
    }

    @Test
    public void testParameterExtractionCount() {
        // Correctness Check: Ensure all parameters are extracted in order
        String query = "UPDATE Products SET UnitsInStock = UnitsInStock - 5 WHERE ProductID = 10 AND ProductName = 'Chai'";
        QueryNormalizer norm = normalizeQuery(query);

        List<Object> params = norm.getParameters();
        assertEquals(3, params.size());
        assertEquals("5", params.get(0));
        assertEquals("10", params.get(1));
        assertEquals("'Chai'", params.get(2));
        
        // Assert the query structure has 3 placeholders
        assertEquals("UPDATE PRODUCTS SET UNITSINSTOCK = UNITSINSTOCK - ? WHERE PRODUCTID = ? AND PRODUCTNAME = ?", norm.getNormalizedSql());
    }

    @Test
    public void testEquivalence_ConditionOrder() {
        // Correctness Check: The order of AND conditions in WHERE clause should not affect the cache key
        String query1 = "SELECT * FROM Employees WHERE FirstName = 'Nancy' AND EmployeeID > 2";
        String query2 = "SELECT * FROM Employees WHERE EmployeeID > 2 AND FirstName = 'Nancy'";

        QueryNormalizer norm1 = normalizeQuery(query1);
        QueryNormalizer norm2 = normalizeQuery(query2);

        // Both should evaluate to the sorted form
        // "EMPLOYEEID > ?" and "FIRSTNAME = ?" sorted alphabetically -> "EMPLOYEEID > ? AND FIRSTNAME = ?"
        assertEquals(norm1.getNormalizedSql(), norm2.getNormalizedSql());
        assertEquals("SELECT * FROM EMPLOYEES WHERE EMPLOYEEID > ? AND FIRSTNAME = ?", norm1.getNormalizedSql());
        
        // Parameters for norm1 should be ['Nancy', 2]
        assertEquals("'Nancy'", norm1.getParameters().get(0));
        assertEquals("2", norm1.getParameters().get(1));
        
        // Parameters for norm2 should be [2, 'Nancy']
        assertEquals("2", norm2.getParameters().get(0));
        assertEquals("'Nancy'", norm2.getParameters().get(1));
    }
    @Test
    public void testEquivalence_SelectColumnOrder() {
        // Correctness Check: The order of columns in SELECT clause should not affect the cache key
        String query1 = "SELECT FirstName, LastName FROM Employees WHERE EmployeeID > 5";
        String query2 = "SELECT LastName, FirstName FROM Employees WHERE EmployeeID > 8";

        QueryNormalizer norm1 = normalizeQuery(query1);
        QueryNormalizer norm2 = normalizeQuery(query2);

        // Both should evaluate to the sorted form for the select clause
        // "FIRSTNAME" and "LASTNAME" sorted alphabetically -> "FIRSTNAME, LASTNAME"
        // Also the parameters should be 5 and 8, both normalized to ?
        assertEquals(norm1.getNormalizedSql(), norm2.getNormalizedSql());
        assertEquals("SELECT FIRSTNAME, LASTNAME FROM EMPLOYEES WHERE EMPLOYEEID > ?", norm1.getNormalizedSql());
    }

    @Test
    public void testEquivalence_NormalizedQuerySeparate() {
        // These queries are structurally different and MUST produce separate keys
        
        // 1. Different Tables
        assertNotEquals(normalizeQuery("SELECT * FROM Employees").getNormalizedSql(),
                       normalizeQuery("SELECT * FROM Products").getNormalizedSql());

        // 2. Different column counts
        assertNotEquals(normalizeQuery("SELECT FirstName FROM Employees").getNormalizedSql(),
                       normalizeQuery("SELECT FirstName, LastName FROM Employees").getNormalizedSql());

        // 3. Different operators (AND vs OR)
        assertNotEquals(normalizeQuery("SELECT * FROM Employees WHERE City = 'London' AND Region = 'WA'").getNormalizedSql(),
                       normalizeQuery("SELECT * FROM Employees WHERE City = 'London' OR Region = 'WA'").getNormalizedSql());

        // 4. Different comparison types
        assertNotEquals(normalizeQuery("SELECT * FROM Employees WHERE EmployeeID > 5").getNormalizedSql(),
                       normalizeQuery("SELECT * FROM Employees WHERE EmployeeID < 5").getNormalizedSql());
    }
}
