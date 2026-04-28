package org.example.model;

import java.util.List;

public class QueryResponse {
    private String plan;
    private String error;
    private List<Object> parameters;
    private Boolean cacheHit;
    private Long executionTimeMs;
    private String normalizedSql;

    public QueryResponse() {}
    public QueryResponse(String plan, String error) {
        this.plan = plan;
        this.error = error;
    }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public List<Object> getParameters() { return parameters; }
    public void setParameters(List<Object> parameters) { this.parameters = parameters; }

    public Boolean getCacheHit() { return cacheHit; }
    public void setCacheHit(Boolean cacheHit) { this.cacheHit = cacheHit; }

    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public String getNormalizedSql() { return normalizedSql; }
    public void setNormalizedSql(String normalizedSql) { this.normalizedSql = normalizedSql; }
}
