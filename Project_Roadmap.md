# Project Progress & Learning Roadmap: Query Plan Caching

This document outlines the current state of the assignment, the technical gaps we've bridged, and the advanced topics required to take the implementation to a production-ready level.

---

## 1. Assignment Status Tracker

| Requirement Category | Specific Task | Status | Notes |
| :--- | :--- | :--- | :--- |
| **1. Design & Strategy** | Strategy for "Similar" queries | **Completed** | Structural normalization via placeholders is implemented, including whitespace stripping and case-folding. |
| | Storing & Looking up plans | **Completed** | Implemented using `ConcurrentHashMap` in Java for $O(1)$ lookups. |
| | Cache Invalidation | **Completed** | Implemented `/api/query/clear-cache` endpoint and `clearCache()` logic. |
| **2. Implementation** | ANTLR Normalization | **Completed** | `QueryNormalizer.java` correctly walks the tree and replaces literals with `?`. |
| | Parameter Binding | **Completed** | Values are collected into a List and passed back via the `CacheResult` DTO. |
| | Cache Logic (Hit/Miss) | **Completed** | Successfully tracking Cache Hits vs. Misses atomically via `AtomicInteger`. |
| **3. Testing & Analytics** | Normal & Edge Cases | **Completed** | JUnit tests added to cover case sensitivity, whitespaces, and extraction. |
| | Hit/Miss Ratios | **Completed** | Implemented an API endpoint `/api/query/stats` to fetch hit/miss metrics. |
| | Performance Metrics | **Completed** | Measured latency in milliseconds using `System.nanoTime()`. |

---

## 2. Advanced Phase: Taking it to the Next Level (V2.0)

To scale this application for real-world scenarios, the following steps are required:

1. **Expand ANTLR Grammar (Official SQLite)**: **Completed** - Replaced the custom basic grammar with the official ANTLR SQLite grammar, enabling parsing of complex `JOIN`s, `GROUP BY`s, and nested sub-queries.
2. **LRU Cache Implementation**: Replace `ConcurrentHashMap` with an LRU cache (e.g., Caffeine) to automatically evict old queries and prevent OutOfMemory errors.
3. **Connect to a Real Planner**: Integrate with Apache Calcite or a real database (PostgreSQL via JDBC) to generate and cache *actual* execution plans instead of mocked JSON strings.
4. **AST Signatures**: Hash the Abstract Syntax Tree instead of strings. `SELECT a, b` and `SELECT b, a` should ideally resolve to the same plan if structurally identical.
5. **Parameter Sniffing Logic**: Invalidate the cache when parameter cardinality drastically changes (e.g. 1 row vs 1 million rows returned).

---

## 3. Core Knowledge Roadmap (VIMP)

To fully master this full-stack project, you need to understand the architectural pillars that hold it together. Below is a structured learning guide:

### A. Java & Concurrency Mastery
* **The Collections Framework**: Understanding how Java stores data in memory. `ConcurrentHashMap<String, String>` is specifically used because it is thread-safe, meaning if 1,000 users hit your API at once, the cache won't crash.
* **Atomic Variables**: `AtomicInteger` is used for the `hits` and `misses` counters to prevent "Race Conditions" during high concurrency.
* **Separation of Concerns**: 
  * *Controllers* handle HTTP traffic.
  * *Services* contain business logic.
  * *DTOs (Data Transfer Objects)* like `CacheResult` and `QueryResponse` carry structured data between layers.

### B. Spring Boot & REST API Architecture
* **Annotations**: How `@RestController`, `@PostMapping`, and `@CrossOrigin` magically configure a Java class to act as a web server.
* **Serialization**: How Spring Boot automatically converts raw JSON from your React frontend into Java Objects (`@RequestBody QueryRequest`).
* **Maven & Executable JARs**: How the `spring-boot-maven-plugin` repackages the compiled code into a single "Fat JAR" containing an embedded Tomcat server so the app can run anywhere with `java -jar`.

### C. ANTLR4 Deep Dive (The Parsing Engine)
ANTLR (Another Tool for Language Recognition) is the "Engine" reading the SQL.
1. **The Grammar (`.g4`)**: The strict rules defining valid SQL. We integrated the massive official `SQLiteParser.g4` to handle complex queries.
2. **The Lexer**: Turns the raw SQL string into "Tokens" (e.g., separating `SELECT`, `*`, `FROM`).
3. **The Parser**: Takes those tokens and builds a hierarchical "Parse Tree."
4. **The Listener Pattern**: Your `QueryNormalizer` extends `SQLiteParserBaseListener`. As ANTLR "walks" through the Parse Tree, it triggers events. Our code listens for literal tokens (`NUMERIC_LITERAL`, `STRING_LITERAL`), extracts them, and swaps them for `?` placeholders.

### D. Deployment & DevOps
* **Multi-stage Docker Builds**: Our `Dockerfile` first uses a Maven image to build the `.jar` file, and then copies *only* the compiled `.jar` into a lightweight JRE image. This makes the production container incredibly small and secure.
* **Platform as a Service (PaaS)**:
  * **Render.com**: Runs our persistent Spring Boot Docker container. Serverless platforms aren't ideal for Spring Boot due to "cold starts", so a persistent container is used.
  * **Vercel**: Hosts the React (Vite) frontend. It reads the `VITE_BACKEND_URL` environment variable to securely connect to the live Render API in production.
