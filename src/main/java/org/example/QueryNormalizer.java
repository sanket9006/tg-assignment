package org.example;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class visits the query tree to normalize it and sort conditions
public class QueryNormalizer extends SQLiteParserBaseVisitor<String> {
    private String normalizedQuery = "";
    private List<Object> parameters = new ArrayList<>();

    public void normalize(ParseTree tree) {
        String result = visit(tree);
        if (result != null) {
            this.normalizedQuery = result.trim().replaceAll("\\s+", " ").toUpperCase();
        }
    }

    @Override
    public String visitTerminal(TerminalNode node) {
        int type = node.getSymbol().getType();
        if (type == SQLiteLexer.NUMERIC_LITERAL || type == SQLiteLexer.STRING_LITERAL) {
            parameters.add(node.getText());
            return "?";
        } else if (type == SQLiteLexer.EOF) {
            return "";
        } else {
            return node.getText();
        }
    }

    @Override
    public String visitExpr_and(SQLiteParser.Expr_andContext ctx) {
        if (ctx.expr_not().size() > 1) {
            List<String> conditions = new ArrayList<>();
            for (SQLiteParser.Expr_notContext exprNot : ctx.expr_not()) {
                String normalizedExpr = visit(exprNot);
                if (normalizedExpr != null) {
                    conditions.add(normalizedExpr.trim());
                }
            }
            Collections.sort(conditions);
            return String.join(" AND ", conditions);
        }
        return visitChildren(ctx);
    }

    @Override
    public String visitSelect_core(SQLiteParser.Select_coreContext ctx) {
        if (ctx.SELECT_() == null) {
            return visitChildren(ctx);
        }

        StringBuilder sb = new StringBuilder();
        List<String> resultColumns = new ArrayList<>();
        boolean inResultColumns = false;

        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);

            if (child instanceof SQLiteParser.Result_columnContext) {
                inResultColumns = true;
                String col = visit(child);
                if (col != null) {
                    resultColumns.add(col.trim());
                }
            } else if (inResultColumns && child instanceof TerminalNode && child.getText().equals(",")) {
                // Skip the comma separating result columns
            } else {
                if (inResultColumns) {
                    Collections.sort(resultColumns);
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(String.join(", ", resultColumns));
                    inResultColumns = false;
                }

                String childText = visit(child);
                if (childText != null && !childText.trim().isEmpty()) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(childText.trim());
                }
            }
        }

        if (inResultColumns) {
            Collections.sort(resultColumns);
            if (sb.length() > 0) sb.append(" ");
            sb.append(String.join(", ", resultColumns));
        }

        return sb.toString();
    }

    @Override
    public String visitExpr_or(SQLiteParser.Expr_orContext ctx) {
        if (ctx.expr_and().size() > 1) {
            List<String> conditions = new ArrayList<>();
            for (SQLiteParser.Expr_andContext exprAnd : ctx.expr_and()) {
                String normalizedExpr = visit(exprAnd);
                if (normalizedExpr != null) {
                    conditions.add(normalizedExpr.trim());
                }
            }
            Collections.sort(conditions);
            return String.join(" OR ", conditions);
        }
        return visitChildren(ctx);
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (aggregate == null) return nextResult;
        if (nextResult == null || nextResult.trim().isEmpty()) return aggregate;
        return aggregate + " " + nextResult;
    }

    public String getNormalizedSql() {
        return normalizedQuery;
    }

    public List<Object> getParameters() {
        return parameters;
    }
}