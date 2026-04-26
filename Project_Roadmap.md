# Project Progress & Learning Roadmap: Query Plan Caching

This document outlines the current state of the assignment, the technical gaps, and the specific topics required to master the implementation.

---

## 1. Assignment Status Tracker
This table tracks the requirements defined in the "Open Project: Query Plan Caching" assignment against the current POC (Proof of Concept) implementation.

| Requirement Category | Specific Task | Status | Notes |
| :--- | :--- | :--- | :--- |
| **1. Design & Strategy** | Strategy for "Similar" queries | **Partial** | Structural normalization via placeholders is implemented, but case-folding (e.g., `SELECT` vs `select`) is missing. |
| | Storing & Looking up plans | **Completed** | Implemented using `ConcurrentHashMap` in Java for $O(1)$ lookups. |
| | Cache Invalidation | **Missing** | No logic currently exists to handle schema changes (e.g., `DROP TABLE`) or manual cache eviction. |
| **2. Implementation** | ANTLR Normalization | **Completed** | `QueryNormalizer.java` correctly walks the tree and replaces literals with `?`. |
| | Parameter Binding | **Partial** | Values are collected in a `List`, but not yet mapped back to the plan for execution. |
| | Cache Logic (Hit/Miss) | **Completed** | The `QueryPlanCache.getPlan()` method successfully demonstrates the reuse of plans. |
| **3. Testing** | Normal & Edge Cases | **Missing** | Only one basic `SELECT` case is tested. Need to test complex joins, varying whitespace, and invalid SQL. |
| | Hit/Miss Ratios | **Missing** | Logic to track and display cache statistics needs to be added. |
| | Performance Metrics | **Missing** | Need to measure the time difference (latency) between a Cache Miss and a Cache Hit. |

---

## 2. Java Knowledge Roadmap
Since the current code was generated/copied, here are the core Java concepts you need to understand to explain how it works:

### A. The Collections Framework
* **What it is:** How Java stores and retrieves data structures in memory.
* **In your code:** `ConcurrentHashMap<String, String>` (the cache storage) and `ArrayList<Object>` (the parameter storage).
* **Key Concept:** Understand how a "Key" (the normalized SQL) leads to a "Value" (the JSON plan).

### B. Classes and Methods
* **What it is:** The organization of code into objects.
* **In your code:** `QueryPlanCache` is the "Service," and `getPlan` is the "Action."
* **Key Concept:** How variables (like `private final Map cache`) persist inside a class instance across multiple calls.

### C. String Builders & Regex
* **What it is:** Efficiently modifying text.
* **In your code:** `StringBuilder` and `.replaceAll("\s+", " ")`.
* **Key Concept:** Why standard Strings are "immutable" and why we use `StringBuilder` to construct the normalized query bit-by-bit.

---

## 3. ANTLR Knowledge Roadmap
ANTLR (Another Tool for Language Recognition) is the "Engine" that reads the SQL. You should know:

1.  **The Grammar (`.g4`):** The set of rules that define what "valid" SQL looks like for your system.
2.  **The Lexer:** Turns the raw string into "Tokens" (Individual words like `SELECT`, `*`, `FROM`).
3.  **The Parser:** Builds a "Parse Tree," which is a hierarchical map of the query structure.
4.  **The Listener:** Your `QueryNormalizer` is a "Listener." As ANTLR "walks" through the Parse Tree, it triggers events. Your code listens for "Terminal Nodes" (values) and swaps them for placeholders.

---

## 4. Immediate Next Steps
1.  **Run the Build:** Use `mvn clean compile` to ensure the ANTLR files are generated.
2.  **Add Case Sensitivity Handling:** Modify the grammar or the normalizer to ensure `select` and `SELECT` are treated as the same key.
3.  **Implement Invalidation:** Add a `clearCache()` method to the `QueryPlanCache` class to satisfy the "Schema Change" requirement.
