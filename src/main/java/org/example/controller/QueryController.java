package org.example.controller;

import org.example.model.QueryRequest;
import org.example.model.QueryResponse;
import org.example.service.QueryService;
import org.example.model.CacheResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/query")
@CrossOrigin
public class QueryController {
    @Autowired
    private QueryService queryService;

    @PostMapping("/plan")
    public QueryResponse getPlan(@RequestBody QueryRequest request) {
        try {
            CacheResult result = queryService.getPlan(request.getQuery());
            QueryResponse response = new QueryResponse(result.plan, null);
            response.setParameters(result.parameters);
            response.setCacheHit(result.cacheHit);
            response.setExecutionTimeMs(result.executionTimeMs);
            return response;
        } catch (Exception e) {
            return new QueryResponse(null, e.getMessage());
        }
    }

    @PostMapping("/clear-cache")
    public void clearCache() {
        queryService.clearCache();
    }

    @GetMapping("/stats")
    public Map<String, Integer> getStats() {
        return queryService.getStats();
    }

    @GetMapping("/cache-dump")
    public Map<String, String> getCacheDump() {
        return queryService.getCacheDump();
    }
}
