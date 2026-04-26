package org.example;

import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.List;

// This class listens to the "walk" through the query tree
public class QueryNormalizer extends QueryBaseListener {
    private StringBuilder normalizedQuery = new StringBuilder();
    private List<Object> parameters = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        int type = node.getSymbol().getType();

        // Use the token types generated from your Query.g4
        // 5 is usually INT, 6 is usually STRING, but we check by name
        if (type == QueryLexer.INT || type == QueryLexer.STRING) {
            normalizedQuery.append("?"); // Replace value with placeholder
            parameters.add(node.getText()); // Save the actual value
        } else if (type != QueryLexer.EOF) {
            normalizedQuery.append(node.getText()).append(" ");
        }
    }

    public String getNormalizedSql() {
        return normalizedQuery.toString().trim().replaceAll("\\s+", " ");
    }
}