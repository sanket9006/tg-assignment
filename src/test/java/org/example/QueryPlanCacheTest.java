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
        CacheResult result1 = cache.getPlan("SELECT * FROM users WHERE id = 1");
        CacheResult result2 = cache.getPlan("select * FROM users where id = 2");

        assertFalse("First call should be a miss", result1.cacheHit);
        assertTrue("Second call should be a hit because of case folding", result2.cacheHit);
        assertEquals("Both should have same normalized key", 
                     "SELECT * FROM USERS WHERE ID = ?", 
                     result1.plan.substring(result1.plan.indexOf("Filter by ") + 10, result1.plan.length() - 4));
    }

    @Test
    public void testParameterBinding() {
        CacheResult result = cache.getPlan("SELECT name FROM employees WHERE age = 30 AND dept = 'Sales'");
        assertEquals(2, result.parameters.size());
        assertEquals("30", result.parameters.get(0));
        assertEquals("'Sales'", result.parameters.get(1));
    }

    @Test
    public void testWhitespaceNormalization() {
        CacheResult result1 = cache.getPlan("SELECT * FROM table WHERE id=1");
        CacheResult result2 = cache.getPlan("SELECT    *   \n FROM table \t WHERE id= 1");
        assertTrue("Extra whitespaces should be ignored", result2.cacheHit);
    }
    
    @Test
    public void testHitMissRatios() {
        cache.getPlan("SELECT * FROM test");
        cache.getPlan("SELECT * FROM test");
        cache.getPlan("SELECT * FROM test2");
        assertEquals(1, cache.getStats().get("hits").intValue());
        assertEquals(2, cache.getStats().get("misses").intValue());

        cache.getPlan("SELECT u.name, COUNT(*) FROM users u JOIN orders o ON u.id = o.user_id WHERE u.status = 'ACTIVE' GROUP BY u.name");
    }
}
