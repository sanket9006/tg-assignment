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

1. **Expand ANTLR Grammar (`Query.g4`)**: Move beyond basic `SELECT` to support `JOIN`s, `GROUP BY`, `ORDER BY`, nested queries, and complex `WHERE` conditions (`AND`/`OR`/`IN`).
2. **LRU Cache Implementation**: Replace `ConcurrentHashMap` with an LRU cache (e.g., Caffeine) to automatically evict old queries and prevent OutOfMemory errors.
3. **Connect to a Real Planner**: Integrate with Apache Calcite or a real database (PostgreSQL via JDBC) to generate and cache *actual* execution plans instead of mocked JSON strings.
4. **AST Signatures**: Hash the Abstract Syntax Tree instead of strings. `SELECT a, b` and `SELECT b, a` should ideally resolve to the same plan if structurally identical.
5. **Parameter Sniffing Logic**: Invalidate the cache when parameter cardinality drastically changes (e.g. 1 row vs 1 million rows returned).

---

## 3. Java Knowledge Roadmap (VIMP)

To fully understand and maintain the code, you must master the following core Java concepts:

### A. The Collections Framework & Concurrency
* **What it is:** How Java stores and retrieves data structures in memory safely across multiple threads.
* **In your code:** `ConcurrentHashMap<String, String>` (thread-safe cache storage) and `ArrayList<Object>` (parameter storage).
* **Key Concept:** `AtomicInteger` is used for tracking `hits` and `misses` to ensure thread-safe counting without race conditions.

### B. Classes, DTOs, and Methods
* **What it is:** The organization of code into objects and Data Transfer Objects.
* **In your code:** `QueryPlanCache` is the Service, `CacheResult` is a DTO used to pass internal data, and `QueryResponse` maps it to JSON for the frontend.
* **Key Concept:** Separation of concerns. Controllers handle HTTP, Services handle logic, and Models/DTOs hold data.

### C. Spring Boot & REST APIs
* **What it is:** The framework running the backend server.
* **In your code:** `@RestController`, `@PostMapping`, and `@CrossOrigin`.
* **Key Concept:** How Java intercepts HTTP requests from the Vite/React frontend and maps JSON payloads to Java objects (`@RequestBody QueryRequest`).

### D. Unit Testing with JUnit
* **What it is:** Automated testing of code logic.
* **In your code:** `QueryPlanCacheTest.java` using `@Test`, `@Before`, `assertEquals()`, and `assertTrue()`.
* **Key Concept:** Ensuring small pieces of logic (like case-folding and hit/miss logic) stay unbroken as the application scales.

### E. Performance Measurement
* **What it is:** Benchmarking execution speed.
* **In your code:** `System.nanoTime()` inside `QueryPlanCache.java`.
* **Key Concept:** Capturing nanosecond-precision timestamps before and after execution to calculate latency.

---

## 4. ANTLR Knowledge Roadmap

ANTLR (Another Tool for Language Recognition) is the "Engine" that reads the SQL. You should know:

1. **The Grammar (`.g4`)**: The set of rules that define what "valid" SQL looks like for your system.
2. **The Lexer**: Turns the raw string into "Tokens" (Individual words like `SELECT`, `*`, `FROM`).
3. **The Parser**: Builds a "Parse Tree," which is a hierarchical map of the query structure.
4. **The Listener**: Your `QueryNormalizer` is a "Listener." As ANTLR "walks" through the Parse Tree, it triggers events. Your code listens for "Terminal Nodes" (values) and swaps them for placeholders.
