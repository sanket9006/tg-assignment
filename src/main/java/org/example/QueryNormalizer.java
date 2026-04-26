package org.example;

import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.List;

// This class listens to the "walk" through the query tree
public class QueryNormalizer extends SQLiteParserBaseListener {
    private StringBuilder normalizedQuery = new StringBuilder();
    private List<Object> parameters = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        int type = node.getSymbol().getType();

        if (type == SQLiteLexer.NUMERIC_LITERAL || type == SQLiteLexer.STRING_LITERAL) {
            normalizedQuery.append("?"); // Replace value with placeholder
            parameters.add(node.getText()); // Save the actual value
        } else if (type != SQLiteLexer.EOF) {
            normalizedQuery.append(node.getText()).append(" ");
        }
    }

    public String getNormalizedSql() {
        return normalizedQuery.toString().trim().replaceAll("\\s+", " ").toUpperCase();
    }

    public List<Object> getParameters() {
        return parameters;
    }
}