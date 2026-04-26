grammar Query;

statement : selectStatement EOF ;

selectStatement
    : 'SELECT' columns 'FROM' table ('WHERE' condition)? ;

columns   : '*' | ID (',' ID)* ;
table     : ID ;
condition : ID '=' literal ;

literal   : INT | STRING ;

ID      : [a-zA-Z_][a-zA-Z0-9_]* ;
INT     : [0-9]+ ;
STRING  : '\'' .*? '\'' ;
WS      : [ \t\r\n]+ -> skip ;