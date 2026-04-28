package org.example;

import org.antlr.v4.runtime.*;
import org.example.SQLiteLexer;
import org.example.SQLiteParser;

public class TestMain {
    public static void main(String[] args) {
        String query = "SELECT e.FirstName, COUNT(*) FROM Employees e JOIN Orders o ON e.EmployeeID = o.EmployeeID WHERE e.City = 'London' GROUP BY e.FirstName";
        System.out.println("Query length: " + query.length());
        SQLiteLexer lexer = new SQLiteLexer(CharStreams.fromString(query));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLiteParser parser = new SQLiteParser(tokens);
        parser.parse();
        System.out.println("Done parsing.");
    }
}
