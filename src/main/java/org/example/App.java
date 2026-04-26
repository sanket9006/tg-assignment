package org.example;

public class App {
    public static void main(String[] args) {
        QueryPlanCache cacheManager = new QueryPlanCache();

        // These queries are different but have the same structure
        String query1 = "SELECT * FROM orders WHERE customer_id = 101";
        String query2 = "SELECT * FROM orders WHERE customer_id = 999";

        System.out.println("--- Running Query 1 ---");
        System.out.println("Plan: " + cacheManager.getPlan(query1));

        System.out.println("\n--- Running Query 2 ---");
        System.out.println("Plan: " + cacheManager.getPlan(query2));
    }
}