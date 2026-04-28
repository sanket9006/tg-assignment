package org.example.model;

import java.util.List;
import java.time.Instant;

public class CachedEntry {
    private String plan;
    private String normalizedSql;
    private List<Object> parameters;
    private String version;
    private long cachedAt;

    public CachedEntry() {}

    public CachedEntry(String plan, String normalizedSql, List<Object> parameters, String version) {
        this.plan = plan;
        this.normalizedSql = normalizedSql;
        this.parameters = parameters;
        this.version = version;
        this.cachedAt = Instant.now().getEpochSecond();
    }

    public String getPlan() { return plan; }
    public String getNormalizedSql() { return normalizedSql; }
    public List<Object> getParameters() { return parameters; }
    public String getVersion() { return version; }
    public long getCachedAt() { return cachedAt; }
}
