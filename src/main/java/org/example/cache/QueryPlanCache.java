package org.example.cache;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.example.SQLiteLexer;
import org.example.SQLiteParser;
import org.example.QueryNormalizer;
import org.example.model.CacheResult;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class QueryPlanCache {
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final AtomicInteger hits = new AtomicInteger(0);
    private final AtomicInteger misses = new AtomicInteger(0);

    public CacheResult getPlan(String sql) {
        long startTime = System.nanoTime();

        SQLiteLexer lexer = new SQLiteLexer(CharStreams.fromString(sql));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLiteParser parser = new SQLiteParser(tokens);
        ParseTree tree = parser.parse();

        ParseTreeWalker walker = new ParseTreeWalker();
        QueryNormalizer normalizer = new QueryNormalizer();
        walker.walk(normalizer, tree);
        String key = normalizer.getNormalizedSql();

        boolean isHit = cache.containsKey(key);
        String plan;

        if (isHit) {
            hits.incrementAndGet();
            plan = cache.get(key);
        } else {
            misses.incrementAndGet();
            plan = "{ \"execution_steps\": [\"Scan Table\", \"Filter by " + key + "\"] }";
            cache.put(key, plan);
        }

        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1000000;
        
        return new CacheResult(plan, normalizer.getParameters(), isHit, executionTimeMs);
    }

    public void clearCache() {
        cache.clear();
        hits.set(0);
        misses.set(0);
    }

    public Map<String, Integer> getStats() {
        return Map.of("hits", hits.get(), "misses", misses.get());
    }
}
