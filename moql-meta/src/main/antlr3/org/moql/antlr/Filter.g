grammar Filter;

options {
language = Java;
backtrack=true;
}

//tokens
//{
//}

@header {
package org.moql.antlr;

import java.util.LinkedList;
import org.moql.*;
import org.moql.metadata.*;
import org.moql.util.StringFormater;
}
@lexer::header {
package org.moql.antlr;
}
 
AND 		:	A_ N_ D_;
ASCENDING	:	A_ S_ C_;
BETWEEN		:	B_ E_ T_ W_ E_ E_ N_;
EXISTS		:	E_ X_ I_ S_ T_ S_;
FALSE		:	F_ A_ L_ S_ E_;
IN			:	I_ N_;
IS			:	I_ S_;
LIKE		:	L_ I_ K_ E_;
NOT			:	N_ O_ T_;
NULL		:	N_ U_ L_ L_;
OR			:	O_ R_;
TRUE		:	T_ R_ U_ E_;

searchCondition returns[OperationMetadata operationMetadata]
@after {
if (rOperationMetadata != null) {
	operationMetadata = new LogicOperationMetadata(op.getText(), lOperationMetadata, rOperationMetadata);
} else {
	operationMetadata = lOperationMetadata;
}
}
	: lOperationMetadata = booleanTerm (op = OR rOperationMetadata = searchCondition)?
	;
	
booleanTerm returns[OperationMetadata operationMetadata]
@after {
if (rOperationMetadata != null) {
	operationMetadata = new LogicOperationMetadata(op.getText(), lOperationMetadata, rOperationMetadata);
} else {
	operationMetadata = lOperationMetadata;
}
}
	: lOperationMetadata = booleanFactor (op = AND rOperationMetadata = booleanTerm)?
	;

booleanFactor returns[OperationMetadata operationMetadata]
@after {
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), rOperationMetadata);
} else {
	operationMetadata = rOperationMetadata;
}
}
	: not = NOT? rOperationMetadata = booleanPrimary
	;
	
booleanPrimary returns[OperationMetadata operationMetadata]
	: predicateMetadata = predicate {operationMetadata = predicateMetadata;}
	| '(' conditionMetadata = searchCondition {operationMetadata = new ParenMetadata(conditionMetadata);}')'
	| expr = expression {operationMetadata = new RelationOperationMetadata(SelectorConstants.EXPR, expr.expressionText);}
	;
	
predicate returns[OperationMetadata operationMetadata]
	: comparisonMetadata = comparisonPredicate {operationMetadata = comparisonMetadata;}
	| betweenMetadata = betweenPredicate {operationMetadata = betweenMetadata;}
	| inMetadata = inPredicate {operationMetadata = inMetadata;}
	| likeMetadata = likePredicate {operationMetadata = likeMetadata;}
	| nullMetadata = nullPredicate {operationMetadata = nullMetadata;}
	;
	
comparisonPredicate returns[OperationMetadata operationMetadata]
@after {
operationMetadata = new RelationOperationMetadata(op.getText(), lExpr.expressionText, rExpr.expressionText);
}
	: lExpr = expression op = ('=' | '<' | '<=' | '>' | '>=' | '<>') rExpr = expression
	;

betweenPredicate returns[OperationMetadata operationMetadata]
@after {
String rExpr = StringFormater.format("({},{})", rExpr1.expressionText, rExpr2.expressionText);
operationMetadata = new RelationOperationMetadata(SelectorConstants.BETWEEN, lExpr.expressionText, rExpr);
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), operationMetadata);
}
}
	: lExpr = expression not = NOT? BETWEEN rExpr1 = expression AND rExpr2 = expression
	;
	
inPredicate returns[OperationMetadata operationMetadata]
@after {
if (!lr.getText().equals("(")) {
    String[] tokens = rExpr.expressionText.split(",");
    if (tokens.length != 2)
        throw new MoqlRuntimeException("Expressions' count are not 2!");
}
String inExpressionText = StringFormater.format("{}{}{}", lr.getText(), rExpr.expressionText, rr.getText());
operationMetadata = new RelationOperationMetadata(SelectorConstants.IN, lExpr.expressionText, inExpressionText);
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), operationMetadata);
}
}
	: lExpr = expression not = NOT? IN lr = '(' rExpr = expressionList rr = ')'
	| lExpr = expression not = NOT? IN lr = ('{' | '[') rExpr = expressionList rr=(']' | '}')
	;

likePredicate returns[OperationMetadata operationMetadata]
@after {
operationMetadata = new RelationOperationMetadata(SelectorConstants.LIKE, lExpr.expressionText, rExpr.expressionText);
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), operationMetadata);
}
}
	: lExpr = expression not=NOT? LIKE rExpr = expression
	;
	
nullPredicate returns[OperationMetadata operationMetadata]
@after {
operationMetadata = new RelationOperationMetadata(SelectorConstants.IS, lExpr.expressionText, rExpr.getText());
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), operationMetadata);
}
}
	: lExpr = expression IS not = NOT? rExpr = NULL
	;

expressionList returns[String expressionText]
    	: expression (',' expression)* {$expressionText = $text;}
    	;

parExpression
    	:  '('expression')'
    	;

expression returns[String expressionText]
    	: exclusiveOrExpression ( '|' exclusiveOrExpression)* {$expressionText = $text;}
    	;

exclusiveOrExpression
    	: andExpression ('^' andExpression)*
    	;

andExpression
    	: additiveExpression ('&' additiveExpression)*
    	;
    
additiveExpression
    	: multiplicativeExpression (('+' | '-')  multiplicativeExpression)*
    	;

multiplicativeExpression
    	: primary (( '*' | '/' | '%' ) primary)*
    	;

primary
    	: parExpression
    	| function
    	| member
    	| constant
    	;
    	
member
	: variable('['expression?']')* (('.' (variable| function) ('['expression?']')*)*)
	;

function
	: Identifier '('expressionList?')'
	;
	
variable
	: Identifier
	;

constant
	: IntegerLiteral
	| FloatingPointLiteral
	| StringLiteral
	| NULL
	| TRUE
	| FALSE
	;

//Lexer
Percent
	: ('100' | '1'..'9' '0'..'9'?) '%'
	;

IntegerLiteral
	: DecimalLiteral
	| HexLiteral
	| OctalLiteral;

fragment
HexLiteral : '0' ('x'|'X') HexDigit+ ;

fragment
//DecimalLiteral : ('+'|'-')? ('0' | '1'..'9' '0'..'9'*) ;
DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) ;

fragment
OctalLiteral : '0' ('0'..'7')+ ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;


FloatingPointLiteral
    	:   ('0'..'9')+ '.' ('0'..'9')* Exponent?
    	|   '.' ('0'..'9')+ Exponent? 
    	|   ('0'..'9')+ Exponent 
    	|   ('0'..'9')+
    	;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

StringLiteral
    :  '\'' ( ~'\'' | Escape)*  '\''
    |  '"' (~'"' | '\\"')* '"'
    ;
fragment
Escape	: '\'' '\'';

Identifier
	: Letter (Letter | Digital)*;
	
fragment
Letter	:	
	'\u0024' |
       	'\u0041'..'\u005a' |
       	'\u005f' |
       	'\u0061'..'\u007a' |
       	'\u00c0'..'\u00d6' |
       	'\u00d8'..'\u00f6' |
       	'\u00f8'..'\u00ff' |
       	'\u0100'..'\u1fff' |
       	'\u3040'..'\u318f' |
       	'\u3300'..'\u337f' |
       	'\u3400'..'\u3d2d' |
       	'\u4e00'..'\u9fff' |
       	'\uf900'..'\ufaff';
fragment
Digital	:	
	'\u0030'..'\u0039' |
       	'\u0660'..'\u0669' |
       	'\u06f0'..'\u06f9' |
       	'\u0966'..'\u096f' |
       	'\u09e6'..'\u09ef' |
       	'\u0a66'..'\u0a6f' |
       	'\u0ae6'..'\u0aef' |
       	'\u0b66'..'\u0b6f' |
       	'\u0be7'..'\u0bef' |
       	'\u0c66'..'\u0c6f' |
       	'\u0ce6'..'\u0cef' |
       	'\u0d66'..'\u0d6f' |
       	'\u0e50'..'\u0e59' |
       	'\u0ed0'..'\u0ed9' |
       	'\u1040'..'\u1049';
       	
fragment A_ : 'a' | 'A';
fragment B_ : 'b' | 'B';
fragment C_ : 'c' | 'C';
fragment D_ : 'd' | 'D';
fragment E_ : 'e' | 'E';
fragment F_ : 'f' | 'F';
fragment G_ : 'g' | 'G';
fragment H_ : 'h' | 'H';
fragment I_ : 'i' | 'I';
fragment J_ : 'j' | 'J';
fragment K_ : 'k' | 'K';
fragment L_ : 'l' | 'L';
fragment M_ : 'm' | 'M';
fragment N_ : 'n' | 'N';
fragment O_ : 'o' | 'O';
fragment P_ : 'p' | 'P';
fragment Q_ : 'q' | 'Q';
fragment R_ : 'r' | 'R';
fragment S_ : 's' | 'S';
fragment T_ : 't' | 'T';
fragment U_ : 'u' | 'U';
fragment V_ : 'v' | 'V';
fragment W_ : 'w' | 'W';
fragment X_ : 'x' | 'X';
fragment Y_ : 'y' | 'Y';
fragment Z_ : 'z' | 'Z';

WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;