package org.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class QueryPlanCache {
    // Our storage: Key is "SELECT * FROM table WHERE id = ?"
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String getPlan(String sql) {
        // 1. Convert raw text into a stream of characters
        QueryLexer lexer = new QueryLexer(CharStreams.fromString(sql));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 2. Parse the SQL
        QueryParser parser = new QueryParser(tokens);
        ParseTree tree = parser.statement();

        // 3. Normalize: replace constants with '?'
        ParseTreeWalker walker = new ParseTreeWalker();
        QueryNormalizer normalizer = new QueryNormalizer();
        walker.walk(normalizer, tree);

        String key = normalizer.getNormalizedSql();

        // 4. Check if we've seen this pattern before
        if (cache.containsKey(key)) {
            System.out.println("[CACHE HIT] Reusing plan for: " + key);
            return cache.get(key);
        }

        System.out.println("[CACHE MISS] Generating new plan for: " + key);
        String newPlan = "{ \"execution_steps\": [\"Scan Table\", \"Filter by " + key + "\"] }";
        cache.put(key, newPlan);
        return newPlan;
    }
}