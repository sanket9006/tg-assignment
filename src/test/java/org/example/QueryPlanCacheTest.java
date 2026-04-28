package org.example;

import org.example.cache.QueryPlanCache;
import org.example.model.CacheResult;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryPlanCacheTest {
    private QueryPlanCache cache;

    @Before
    public void setUp() {
        cache = new QueryPlanCache();
    }

    @Test
    public void testCaseSensitivity() {
        CacheResult result1 = cache.getPlan("SELECT * FROM Employees WHERE EmployeeID = 1");
        CacheResult result2 = cache.getPlan("select * FROM employees where EmployeeID = 2");

        assertFalse("First call should be a miss", result1.cacheHit);
        assertTrue("Second call should be a hit because of case folding", result2.cacheHit);
        
        // Assert that the plan contains the correct operation and normalized structure
        String planUpper = result1.plan.toUpperCase();
        assertTrue(planUpper.contains("\"OPERATION\": \"SELECT\""));
        assertTrue(planUpper.contains("EMPLOYEES"));
    }

    @Test
    public void testParameterBinding() {
        CacheResult result = cache.getPlan("SELECT FirstName FROM Employees WHERE EmployeeID = 30 AND Title = 'Sales Manager'");
        assertEquals(2, result.parameters.size());
        assertEquals("30", result.parameters.get(0));
        assertEquals("'Sales Manager'", result.parameters.get(1));
    }

    @Test
    public void testWhitespaceNormalization() {
        CacheResult result1 = cache.getPlan("SELECT * FROM Categories WHERE CategoryID=1");
        CacheResult result2 = cache.getPlan("SELECT    *   \n FROM categories \t WHERE CategoryID= 1");
        assertTrue("Extra whitespaces should be ignored", result2.cacheHit);
    }
    
    @Test
    public void testHitMissRatios() {
        cache.getPlan("SELECT * FROM Shippers");
        cache.getPlan("SELECT * FROM Shippers");
        cache.getPlan("SELECT * FROM Suppliers");
        assertEquals(1, cache.getStats().get("hits").intValue());
        assertEquals(2, cache.getStats().get("misses").intValue());

        cache.getPlan("SELECT e.FirstName, COUNT(*) FROM Employees e JOIN Orders o ON e.EmployeeID = o.EmployeeID WHERE e.City = 'London' GROUP BY e.FirstName");
    }
    @Test
    public void testInvalidSyntax() {
        // This is invalid SQL
        String invalidSql = "SELECT * FROM Employees WHERE";
        CacheResult result = cache.getPlan(invalidSql);
        
        // The DB should return an error when trying to EXPLAIN this
        assertNotNull(result);
        assertTrue(result.plan.contains("Error generating plan"));
    }

    @Test
    public void testGarbageInput() {
        // Completely non-SQL input
        String garbage = "NOT_A_QUERY";
        CacheResult result = cache.getPlan(garbage);
        
        assertNotNull(result);
        // It should still return a response object
        assertTrue(result.plan.contains("execution_plan"));
    }
}
