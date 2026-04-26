package org.example;

import org.antlr.v4.runtime.*;
import org.example.SQLiteLexer;
import org.example.SQLiteParser;

public class TestMain {
    public static void main(String[] args) {
        String query = "SELECT u.name, COUNT(*) FROM users u JOIN orders o ON u.id = o.user_id WHERE u.status = 'ACTIVE' GROUP BY u.name";
        System.out.println("Query length: " + query.length());
        SQLiteLexer lexer = new SQLiteLexer(CharStreams.fromString(query));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLiteParser parser = new SQLiteParser(tokens);
        parser.parse();
        System.out.println("Done parsing.");
    }
}
