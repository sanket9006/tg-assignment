// Generated from c:/Users/SanketPatil/Downloads/tg-assignment-v2/src/main/antlr4/org/example/Query.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class QueryParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, ID=7, INT=8, STRING=9, 
		WS=10;
	public static final int
		RULE_statement = 0, RULE_selectStatement = 1, RULE_columns = 2, RULE_table = 3, 
		RULE_condition = 4, RULE_literal = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"statement", "selectStatement", "columns", "table", "condition", "literal"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'SELECT'", "'FROM'", "'WHERE'", "'*'", "','", "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "ID", "INT", "STRING", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Query.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QueryParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public TerminalNode EOF() { return getToken(QueryParser.EOF, 0); }
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			selectStatement();
			setState(13);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectStatementContext extends ParserRuleContext {
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public TableContext table() {
			return getRuleContext(TableContext.class,0);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public SelectStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterSelectStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitSelectStatement(this);
		}
	}

	public final SelectStatementContext selectStatement() throws RecognitionException {
		SelectStatementContext _localctx = new SelectStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_selectStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			match(T__0);
			setState(16);
			columns();
			setState(17);
			match(T__1);
			setState(18);
			table();
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(19);
				match(T__2);
				setState(20);
				condition();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColumnsContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(QueryParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(QueryParser.ID, i);
		}
		public ColumnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterColumns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitColumns(this);
		}
	}

	public final ColumnsContext columns() throws RecognitionException {
		ColumnsContext _localctx = new ColumnsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_columns);
		int _la;
		try {
			setState(32);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				enterOuterAlt(_localctx, 1);
				{
				setState(23);
				match(T__3);
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(24);
				match(ID);
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(25);
					match(T__4);
					setState(26);
					match(ID);
					}
					}
					setState(31);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TableContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(QueryParser.ID, 0); }
		public TableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitTable(this);
		}
	}

	public final TableContext table() throws RecognitionException {
		TableContext _localctx = new TableContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_table);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConditionContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(QueryParser.ID, 0); }
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitCondition(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(ID);
			setState(37);
			match(T__5);
			setState(38);
			literal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(QueryParser.INT, 0); }
		public TerminalNode STRING() { return getToken(QueryParser.STRING, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==STRING) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\n+\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u0016"+
		"\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002\u001c"+
		"\b\u0002\n\u0002\f\u0002\u001f\t\u0002\u0003\u0002!\b\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0000\u0000\u0006\u0000\u0002\u0004\u0006\b\n"+
		"\u0000\u0001\u0001\u0000\b\t\'\u0000\f\u0001\u0000\u0000\u0000\u0002\u000f"+
		"\u0001\u0000\u0000\u0000\u0004 \u0001\u0000\u0000\u0000\u0006\"\u0001"+
		"\u0000\u0000\u0000\b$\u0001\u0000\u0000\u0000\n(\u0001\u0000\u0000\u0000"+
		"\f\r\u0003\u0002\u0001\u0000\r\u000e\u0005\u0000\u0000\u0001\u000e\u0001"+
		"\u0001\u0000\u0000\u0000\u000f\u0010\u0005\u0001\u0000\u0000\u0010\u0011"+
		"\u0003\u0004\u0002\u0000\u0011\u0012\u0005\u0002\u0000\u0000\u0012\u0015"+
		"\u0003\u0006\u0003\u0000\u0013\u0014\u0005\u0003\u0000\u0000\u0014\u0016"+
		"\u0003\b\u0004\u0000\u0015\u0013\u0001\u0000\u0000\u0000\u0015\u0016\u0001"+
		"\u0000\u0000\u0000\u0016\u0003\u0001\u0000\u0000\u0000\u0017!\u0005\u0004"+
		"\u0000\u0000\u0018\u001d\u0005\u0007\u0000\u0000\u0019\u001a\u0005\u0005"+
		"\u0000\u0000\u001a\u001c\u0005\u0007\u0000\u0000\u001b\u0019\u0001\u0000"+
		"\u0000\u0000\u001c\u001f\u0001\u0000\u0000\u0000\u001d\u001b\u0001\u0000"+
		"\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e!\u0001\u0000\u0000"+
		"\u0000\u001f\u001d\u0001\u0000\u0000\u0000 \u0017\u0001\u0000\u0000\u0000"+
		" \u0018\u0001\u0000\u0000\u0000!\u0005\u0001\u0000\u0000\u0000\"#\u0005"+
		"\u0007\u0000\u0000#\u0007\u0001\u0000\u0000\u0000$%\u0005\u0007\u0000"+
		"\u0000%&\u0005\u0006\u0000\u0000&\'\u0003\n\u0005\u0000\'\t\u0001\u0000"+
		"\u0000\u0000()\u0007\u0000\u0000\u0000)\u000b\u0001\u0000\u0000\u0000"+
		"\u0003\u0015\u001d ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}