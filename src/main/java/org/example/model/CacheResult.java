package org.example.model;

import java.util.List;

public class CacheResult {
    public String plan;
    public String normalizedSql;
    public List<Object> parameters;
    public boolean cacheHit;
    public long executionTimeMs;

    public CacheResult(String plan, String normalizedSql, List<Object> parameters, boolean cacheHit, long executionTimeMs) {
        this.plan = plan;
        this.normalizedSql = normalizedSql;
        this.parameters = parameters;
        this.cacheHit = cacheHit;
        this.executionTimeMs = executionTimeMs;
    }
}
