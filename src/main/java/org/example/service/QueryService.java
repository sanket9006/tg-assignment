package org.example.service;

import org.example.cache.QueryPlanCache;
import org.example.model.CacheResult;
import org.example.model.CachedEntry;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class QueryService {
    private final QueryPlanCache cache = new QueryPlanCache();

    public CacheResult getPlan(String sql) {
        return cache.getPlan(sql);
    }

    public void clearCache() {
        cache.clearCache();
    }

    public Map<String, Integer> getStats() {
        return cache.getStats();
    }

    public Map<Long, CachedEntry> getCacheDump() {
        return cache.getCacheDump();
    }
}
