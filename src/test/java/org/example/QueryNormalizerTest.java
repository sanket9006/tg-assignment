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

        ParseTreeWalker walker = new ParseTreeWalker();
        QueryNormalizer normalizer = new QueryNormalizer();
        walker.walk(normalizer, tree);
        return normalizer;
    }

    @Test
    public void testEquivalence_SpacingAndCasing() {
        // Correctness Check: Structurally identical queries must resolve to the same cache key
        String queryA = "SELECT * FROM users WHERE age > 25";
        String queryB = "select    * from USERS   where age > 99";

        QueryNormalizer normA = normalizeQuery(queryA);
        QueryNormalizer normB = normalizeQuery(queryB);

        // 1. Assert Cache Keys are EXACTLY the same
        assertEquals(normA.getNormalizedSql(), normB.getNormalizedSql());
        assertEquals("SELECT * FROM USERS WHERE AGE > ?", normA.getNormalizedSql());

        // 2. Assert Parameters were correctly extracted and ordered
        assertEquals("25", normA.getParameters().get(0));
        assertEquals("99", normB.getParameters().get(0));
    }

    @Test
    public void testEquivalence_StringsAndQuotes() {
        String queryA = "SELECT * FROM orders WHERE status = 'SHIPPED'";
        String queryB = "SELECT * FROM orders WHERE status = 'PENDING'";

        QueryNormalizer normA = normalizeQuery(queryA);
        QueryNormalizer normB = normalizeQuery(queryB);

        assertEquals("Cache keys must match", normA.getNormalizedSql(), normB.getNormalizedSql());
        assertEquals("'SHIPPED'", normA.getParameters().get(0));
        assertEquals("'PENDING'", normB.getParameters().get(0));
    }

    @Test
    public void testDivergence_DifferentStructure() {
        // Correctness Check: Structurally different queries must NEVER resolve to the same cache key
        String queryC = "SELECT name FROM users WHERE age > 25";
        String queryD = "SELECT email FROM users WHERE age > 25";

        QueryNormalizer normC = normalizeQuery(queryC);
        QueryNormalizer normD = normalizeQuery(queryD);

        assertNotEquals("Cache keys must NOT match for structurally different queries", 
                normC.getNormalizedSql(), normD.getNormalizedSql());
    }

    @Test
    public void testParameterExtractionCount() {
        // Correctness Check: Ensure all parameters are extracted in order
        String query = "UPDATE inventory SET stock = stock - 5 WHERE id = 10 AND warehouse = 'EAST'";
        QueryNormalizer norm = normalizeQuery(query);

        List<Object> params = norm.getParameters();
        assertEquals(3, params.size());
        assertEquals("5", params.get(0));
        assertEquals("10", params.get(1));
        assertEquals("'EAST'", params.get(2));
        
        // Assert the query structure has 3 placeholders
        assertEquals("UPDATE INVENTORY SET STOCK = STOCK - ? WHERE ID = ? AND WAREHOUSE = ?", norm.getNormalizedSql());
    }
}
