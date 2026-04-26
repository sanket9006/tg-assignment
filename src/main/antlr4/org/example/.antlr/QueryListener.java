// Generated from c:/Users/SanketPatil/Downloads/tg-assignment-v2/src/main/antlr4/org/example/Query.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QueryParser}.
 */
public interface QueryListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QueryParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(QueryParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(QueryParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectStatement(QueryParser.SelectStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectStatement(QueryParser.SelectStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#columns}.
	 * @param ctx the parse tree
	 */
	void enterColumns(QueryParser.ColumnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#columns}.
	 * @param ctx the parse tree
	 */
	void exitColumns(QueryParser.ColumnsContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#table}.
	 * @param ctx the parse tree
	 */
	void enterTable(QueryParser.TableContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#table}.
	 * @param ctx the parse tree
	 */
	void exitTable(QueryParser.TableContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(QueryParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(QueryParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(QueryParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(QueryParser.LiteralContext ctx);
}