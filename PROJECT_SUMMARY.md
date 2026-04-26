# Project Summary: SQL Query Normalization & Plan Caching

## Overview
This full-stack application demonstrates how to normalize SQL queries, generate caching keys, and store execution plans to optimize database operations. By leveraging the official SQLite ANTLR grammar for parsing, it intelligently replaces constants with placeholders and standardizes queries (handling whitespace and case insensitivity) to reuse structurally identical plans. A React-based frontend dashboard allows users to interact with the caching engine in real-time, view hit/miss stats, clear the cache, and measure performance latency. The project is fully containerized and production-ready.

---

## Architecture

The application is split into a **Spring Boot Backend** and a **React (Vite) Frontend**, designed for seamless cloud deployment.

### 1. The Frontend (React + Vite)
- **Framework & Tooling**: Built with React and Vite. Features a dark-mode, glassmorphism UI.
- **Files**:
  - `dashboard/src/App.jsx`: Manages state, coordinates API requests, and handles cache clearing.
  - `dashboard/src/components/QueryForm.jsx`: The input interface featuring 10 complex preset queries.
  - `dashboard/src/components/ResultDisplay.jsx`: Visualizes the generated plan, extracted parameters, cache hit/miss status, and execution latency.
- **Deployment**: Configured for **Vercel**, communicating with the live backend via the `BACKEND_URL` variable.

### 2. The Backend (Java 21 + Spring Boot)
- **Controller Layer (`QueryController.java`)**: Handles REST API requests with `@CrossOrigin` support. Endpoints: `/api/query/plan`, `/api/query/clear-cache`, and `/api/query/stats`.
- **Service Layer (`QueryService.java`)**: Acts as a bridge between the REST controller and the underlying caching logic.
- **Caching Engine (`QueryPlanCache.java`)**: The core brain of the system. Tracks performance (`executionTimeMs`), atomic counters for `hits` and `misses`, and maintains an in-memory `ConcurrentHashMap`.
- **Data Transfer Objects (DTOs)**: 
  - `CacheResult.java`: An internal model for transferring caching statistics and extracted parameters.
  - `QueryRequest.java` & `QueryResponse.java`: The JSON schemas for client-server communication.
- **Deployment**: Containerized using a multi-stage `Dockerfile` to build an executable "Fat JAR" and hosted on **Render.com**.

### 3. The Parsing Engine (ANTLR4)
- **Grammars (`SQLiteLexer.g4` & `SQLiteParser.g4`)**: Uses the official SQLite grammar to support complex SQL syntax including JOINs, Subqueries, CTEs, and modifications.
- **Normalizer (`QueryNormalizer.java`)**: Extends `SQLiteParserBaseListener`. As the parser walks the tree, it intercepts tokens (`NUMERIC_LITERAL`, `STRING_LITERAL`), extracts parameters into a list, replaces them with `?` placeholders, and standardizes formatting to guarantee consistent cache keys.

### 4. Testing
- **Unit Tests (`QueryPlanCacheTest.java`)**: Validates the core logic using JUnit. Covers case insensitivity, whitespace normalization, parameter extraction, and cache counters.

---

## Workflow (How it works)

1. The **User** enters a raw SQL query (e.g., `sELect * from users WHERE id = 101   `) in the Frontend Dashboard.
2. The Dashboard sends a JSON POST request to the **Spring Boot Controller**.
3. The **Controller** delegates to the **QueryService**, which forwards the SQL to the **QueryPlanCache**.
4. The cache delegates parsing to **ANTLR** and **QueryNormalizer**. The query is transformed into `SELECT * FROM USERS WHERE ID = ?` and `101` is saved into an extracted parameter list.
5. The Cache checks its `ConcurrentHashMap`. 
   - If the normalized query exists, it increments the **Hit** counter.
   - If it doesn't, it generates a new mock execution plan, increments the **Miss** counter, and saves it.
6. The caching execution time is measured, and the plan, stats, and parameters are wrapped in a `CacheResult` and sent back to the controller.
7. The Controller transforms this into a `QueryResponse` and sends it to the Frontend.
8. The **Frontend Dashboard** updates the UI to reflect the parsed plan, cache status, and execution speed.
