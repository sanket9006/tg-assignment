# Project Summary: SQL Query Normalization & Plan Caching

## Overview
This Java project demonstrates how to normalize SQL queries and cache their execution plans. It uses ANTLR to parse SQL-like queries, replaces constants with placeholders, and caches execution plans for structurally identical queries. This approach is useful for database engines or middleware to avoid redundant plan generation.

---

## Project Structure
- **ANTLR Grammar**: Defines the SQL syntax and parsing rules.
- **QueryNormalizer**: Walks the parse tree, replaces constants with `?`, and collects parameters.
- **QueryPlanCache**: Normalizes queries, checks the cache, and stores/retrieves execution plans.
- **App**: Runs sample queries to demonstrate cache behavior.

---

## Key Files & Code

### 1. Query Grammar (`Query.g4`)
```
grammar Query;

statement : selectStatement EOF ;

selectStatement
    : 'SELECT' columns 'FROM' table ('WHERE' condition)? ;

columns   : '*' | ID (',' ID)* ;
table     : ID ;
condition : ID '=' literal ;

literal   : INT | STRING ;

ID      : [a-zA-Z_][a-zA-Z0-9_]* ;
INT     : [0-9]+ ;
STRING  : '\'' .*? '\'' ;
WS      : [ \t\r\n]+ -> skip ;
```

---

### 2. Query Normalizer
**File:** `src/main/java/org/example/QueryNormalizer.java`
```java
public class QueryNormalizer extends QueryBaseListener {
    private StringBuilder normalizedQuery = new StringBuilder();
    private List<Object> parameters = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        int type = node.getSymbol().getType();
        if (type == QueryLexer.INT || type == QueryLexer.STRING) {
            normalizedQuery.append("?");
            parameters.add(node.getText());
        } else if (type != QueryLexer.EOF) {
            normalizedQuery.append(node.getText()).append(" ");
        }
    }

    public String getNormalizedSql() {
        return normalizedQuery.toString().trim().replaceAll("\\s+", " ");
    }
}
```

---

### 3. Query Plan Cache
**File:** `src/main/java/org/example/QueryPlanCache.java`
```java
public class QueryPlanCache {
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String getPlan(String sql) {
        QueryLexer lexer = new QueryLexer(CharStreams.fromString(sql));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QueryParser parser = new QueryParser(tokens);
        ParseTree tree = parser.statement();

        ParseTreeWalker walker = new ParseTreeWalker();
        QueryNormalizer normalizer = new QueryNormalizer();
        walker.walk(normalizer, tree);
        String key = normalizer.getNormalizedSql();

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
```

---

### 4. Main Application
**File:** `src/main/java/org/example/App.java`
```java
public class App {
    public static void main(String[] args) {
        QueryPlanCache cacheManager = new QueryPlanCache();

        String query1 = "SELECT * FROM orders WHERE customer_id = 101";
        String query2 = "SELECT * FROM orders WHERE customer_id = 999";

        System.out.println("--- Running Query 1 ---");
        System.out.println("Plan: " + cacheManager.getPlan(query1));

        System.out.println("\n--- Running Query 2 ---");
        System.out.println("Plan: " + cacheManager.getPlan(query2));
    }
}
```

---

### 5. Maven Configuration
**File:** `pom.xml`
- Uses Java 21
- Integrates ANTLR for code generation
- Includes JUnit for testing

---

## How It Works
1. User submits a SQL query.
2. `QueryPlanCache` parses and normalizes the query.
3. `QueryNormalizer` replaces constants with `?`.
4. The normalized query is used as a cache key.
5. If a plan exists, it is reused; otherwise, a new plan is generated and cached.

---

## Example Output
```
--- Running Query 1 ---
[CACHE MISS] Generating new plan for: SELECT * FROM orders WHERE customer_id = ?
Plan: { "execution_steps": ["Scan Table", "Filter by SELECT * FROM orders WHERE customer_id = ?"] }

--- Running Query 2 ---
[CACHE HIT] Reusing plan for: SELECT * FROM orders WHERE customer_id = ?
Plan: { "execution_steps": ["Scan Table", "Filter by SELECT * FROM orders WHERE customer_id = ?"] }
```

---

## Summary
This project efficiently reuses query execution plans for structurally similar SQL queries by normalizing and caching them, improving performance in systems like databases or query engines.
