# Project Summary: SQL Query Normalization & Plan Caching

## Overview
This full-stack application demonstrates the core mechanics of a modern database query optimizer. It accepts raw SQL queries, intelligently normalizes them by abstracting away hardcoded variables, and utilizes an in-memory caching mechanism to store and retrieve execution plans. This significantly reduces the computational overhead of parsing and planning structurally identical queries. 

A React-based dashboard allows users to interact with the engine in real-time, view hit/miss statistics, monitor execution latency, and experiment with a massive suite of supported SQL operations (powered by the official ANTLR SQLite grammar).

---

## 🧠 Core System Logic

The system is built on four primary logical pillars: Lexical Parsing, Structural Normalization, Parameter Extraction, and Concurrent Caching.

### 1. The Parsing Logic (ANTLR4)
Traditional regex is insufficient for understanding nested SQL statements (like CTEs or Subqueries). Instead, we use **ANTLR4** with the official `SQLite` grammar.
- **Lexical Analysis:** The `SQLiteLexer` takes the raw string and breaks it down into structured tokens (e.g., identifying `SELECT` as a keyword, and `100` as a `NUMERIC_LITERAL`).
- **Tree Generation:** The `SQLiteParser` organizes these tokens into a hierarchical Abstract Syntax Tree (AST), understanding the mathematical and logical relationships between clauses.

### 2. The Normalization & Extraction Logic (The Listener)
The `QueryNormalizer` class implements the `SQLiteParserBaseListener` interface. As the `ParseTreeWalker` traverses the AST node by node, the normalizer listens for specific token types.
- **Parameter Extraction:** When it encounters a `NUMERIC_LITERAL` or `STRING_LITERAL`, it intercepts the raw value, saves it into a sequential `List<Object>`, and dynamically replaces the node's output with a `? ` placeholder.
- **Structural Standardization:** As the tree is flattened back into a string, the normalizer aggressively strips out redundant whitespaces and converts all keywords/identifiers to UPPERCASE. 
- **Result:** Queries like `sELect *    from USERS where id=1` and `SELECT * FROM users WHERE id = 999` are perfectly reduced to the exact same cache key: `SELECT * FROM USERS WHERE ID = ? `.

### 3. The Thread-Safe Caching Logic
Web applications must handle hundreds of concurrent requests. Traditional `HashMap`s or `int` counters will crash or corrupt data under high concurrency.
- **Storage:** We utilize Java's `ConcurrentHashMap<String, String>` to store the normalized query string as the key, and the Execution Plan JSON as the value, ensuring safe, lock-free $O(1)$ lookups.
- **Telemetry:** Cache hits and misses are tracked using `AtomicInteger`. This guarantees that if 50 requests hit the cache simultaneously, the counter increments safely without race conditions.

### 4. The Real Database Planner Logic
When a Cache Miss occurs, the system natively connects to an embedded **SQLite Northwind Database** via JDBC. It executes an `EXPLAIN QUERY PLAN` directly against the database, intercepting the true database execution strategy (e.g., Table Scans vs Index Searches) and converting it into a serialized JSON format for caching and frontend consumption.

---

## 🔄 The Execution Flow (Step-by-Step)

The lifecycle of a single query request flows through five distinct phases:

### Phase 1: Ingestion & Routing
1. The user inputs a complex SQL query into the React frontend and clicks "Analyze".
2. The Vite proxy forwards the JSON payload to the Spring Boot backend (`@PostMapping("/plan")`).
3. The `QueryController` receives the `QueryRequest` and delegates it down to the `QueryService` and ultimately the `QueryPlanCache`.

### Phase 2: Lexical Analysis
4. The system starts a high-precision nanosecond timer (`System.nanoTime()`).
5. The raw SQL string is fed into the `CharStreams` reader.
6. The `SQLiteLexer` processes the stream into a `CommonTokenStream`.
7. The `SQLiteParser` evaluates the tokens and constructs the raw Parse Tree.

### Phase 3: Traversal & Normalization
8. The `ParseTreeWalker` begins traversing the generated tree.
9. At each node, the `QueryNormalizer` applies our custom logic:
   - Identifies literals, pushes them to the `parameters` array, and inserts a `?`.
   - Normalizes spacing and casing.
10. The walker finishes, outputting the finalized **Cache Key** (the normalized SQL string).

### Phase 4: Cache Evaluation
11. The engine checks the `ConcurrentHashMap` for the generated Cache Key.
    - **Branch A (Cache HIT):** 
      - The `hits` atomic counter increments.
      - The stored JSON execution plan is instantly retrieved from memory.
    - **Branch B (Cache MISS):**
      - The `misses` atomic counter increments.
      - The embedded SQLite Database generates a real Execution Plan via JDBC, serializes it to JSON, and stores it in the `ConcurrentHashMap` for future use.

### Phase 5: Telemetry & Response
12. The nanosecond timer stops, and the total execution time is calculated in milliseconds.
13. The Execution Plan, Extracted Parameters list, Cache Status boolean, and Latency are packaged into a `CacheResult` DTO.
14. The `QueryController` maps this to a `QueryResponse` and returns the HTTP 200 JSON response.
15. The React Dashboard updates its state, rendering the beautifully formatted JSON plan, pill-shaped parameter tags, and the live Hit/Miss dashboard widgets.

---

## 🛠 Tech Stack Architecture
- **Frontend:** React, Vite, CSS3 (Glassmorphism, Dark-mode), Vercel.
- **Backend:** Java 21, Spring Boot 3.x, Maven.
- **Engine Core:** ANTLR4 (Official SQLite Grammar), `java.util.concurrent`.
- **Deployment & DevOps:** Multi-stage Dockerfile (Eclipse Temurin JRE), Render.com PaaS.
