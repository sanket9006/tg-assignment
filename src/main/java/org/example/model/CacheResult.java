package org.example.model;

import java.util.List;

public class CacheResult {
    public String plan;
    public List<Object> parameters;
    public boolean cacheHit;
    public long executionTimeMs;

    public CacheResult(String plan, List<Object> parameters, boolean cacheHit, long executionTimeMs) {
        this.plan = plan;
        this.parameters = parameters;
        this.cacheHit = cacheHit;
        this.executionTimeMs = executionTimeMs;
    }
}
