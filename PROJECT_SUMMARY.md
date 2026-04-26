# Project Summary: SQL Query Normalization & Plan Caching

## Overview
This full-stack application demonstrates how to normalize SQL queries, generate caching keys, and store execution plans to optimize database operations. By leveraging ANTLR for parsing, it intelligently replaces constants with placeholders and standardizes queries (handling whitespace and case insensitivity) to reuse structurally identical plans. A React-based frontend dashboard allows users to interact with the caching engine in real-time, view hit/miss stats, and measure performance latency.

---

## Architecture

The application is split into a **Spring Boot Backend** and a **React (Vite) Frontend**, working together to simulate an intelligent query planner.

### 1. The Frontend (React + Vite)
- **Framework & Tooling**: Built with React and Vite. It serves as the user dashboard.
- **Files**:
  - `dashboard/src/App.jsx`: Manages state and coordinates API requests.
  - `dashboard/src/components/QueryForm.jsx`: The input interface for writing SQL queries.
  - `dashboard/src/components/ResultDisplay.jsx`: Visualizes the generated plan, extracted parameters, cache hit/miss status, and execution latency.
- **Proxy/CORS**: Vite proxies API calls from `/api/...` directly to the Spring Boot server to bypass CORS during development (configured in `vite.config.js`).

### 2. The Backend (Java + Spring Boot)
- **Controller Layer (`src/main/java/org/example/controller/QueryController.java`)**: Handles REST API requests from the dashboard. Provides endpoints for generating plans (`/api/query/plan`), clearing the cache (`/api/query/clear-cache`), and fetching hit/miss statistics (`/api/query/stats`).
- **Service Layer (`src/main/java/org/example/service/QueryService.java`)**: Acts as a bridge between the REST controller and the underlying caching logic.
- **Caching Engine (`src/main/java/org/example/cache/QueryPlanCache.java`)**: The core brain of the system. It tracks performance (`executionTimeMs`), atomic counters for `hits` and `misses`, and maintains an in-memory `ConcurrentHashMap` linking normalized SQL queries to their generated plans.
- **Data Transfer Objects (DTOs)**: 
  - `src/main/java/org/example/model/CacheResult.java`: An internal model for transferring caching statistics and extracted parameters within the backend.
  - `src/main/java/org/example/model/QueryRequest.java` & `QueryResponse.java`: The JSON schemas for client-server communication.

### 3. The Parsing Engine (ANTLR)
- **Grammar (`src/main/antlr4/org/example/Query.g4`)**: Defines the structural rules of valid SQL queries. During the Maven build phase, ANTLR generates the lexer and parser classes.
- **Normalizer (`src/main/java/org/example/QueryNormalizer.java`)**: Implements the ANTLR Listener pattern. As the parser walks through the SQL tree, the normalizer identifies terminal nodes (like integers or strings), extracts them into a parameter list, replaces them with `?` placeholders, strips extra whitespace, and converts the query to uppercase to guarantee consistent cache keys.

### 4. Testing
- **Unit Tests (`src/test/java/org/example/QueryPlanCacheTest.java`)**: Validates the core logic of the application using JUnit. It includes tests for case insensitivity, whitespace normalization, parameter extraction binding, and cache hit/miss ratio counters.

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
