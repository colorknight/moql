grammar Operand;

options {
language = Java;
backtrack=true;
}

tokens {
LPAREN = '(';
RPAREN = ')';
PLUS = '+';
MINUS = '-';
ASTERRISK = '*';
SOLIDUS = '/';
PERCENT = '%';
AMPERSAND = '&';
VERTICAL = '|';
CIRCUMFLEX = '^';
}
 
@header {
package org.datayoo.moql.antlr;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import org.apache.commons.lang3.Validate;

import org.datayoo.moql.*;
import org.datayoo.moql.operand.*;
import org.datayoo.moql.operand.constant.*;
import org.datayoo.moql.operand.variable.*;
import org.datayoo.moql.operand.function.*;
import org.datayoo.moql.operand.function.factory.*;
import org.datayoo.moql.operand.expression.*;
import org.datayoo.moql.operand.expression.arithmetic.*;
import org.datayoo.moql.operand.expression.bit.*;
import org.datayoo.moql.operand.expression.member.*;
import org.datayoo.moql.operand.expression.array.*;
}
@lexer::header {
package org.datayoo.moql.antlr;

}

@members {
private FunctionFactory functionFactory;
private Set<MemberVisitor> memberVisitors = new HashSet();

public FunctionFactory getFunctionFactory() {
	return functionFactory;
}

public void setFunctionFactory(FunctionFactory functionFactory) {
	Validate.notNull(functionFactory, "Parameter 'functionFactory' is null!");
	this.functionFactory = functionFactory;
}

public void setMemberVisitors(Set<MemberVisitor> memberVisitors) {
    this.memberVisitors = memberVisitors;
}

public Set<MemberVisitor> getMemberVisitor() {
    return memberVisitors;
}
}

NULL		:	N_ U_ L_ L_;
TRUE		:	T_ R_ U_ E_;
FALSE		:	F_ A_ L_ S_ E_;

operand returns[Operand operand]
	: ('('expression',')=>'(' expList = expressionList {operand = new OperandsExpression(expList);} ')'
	| exp = rangeExpression {operand = exp;}
	| exp = expression {operand = exp;}
	;

expressionList returns[List<Operand> operands]
@init {
operands = new LinkedList<Operand>();
}
    	: exp = expression {operands.add(exp);} (',' exp = expression {operands.add(exp);})*
    	;

rangeExpression returns[Operand operand]
        :   l = ('['|'{') lExp = expression ',' rExp = expression r = (']' | '}')
        {
            boolean lClosure = false;
            boolean rClosure = false;
            if (l.getText().equals("["))
                lClosure = true;
            if (r.getText().equals("]"))
                rClosure = true;
            operand = new RangeExpression(lExp, rExp, lClosure, rClosure);
        }
        ;
parExpression returns[Operand operand]
    	:  '(' exp = expression ')' { operand = new ParenExpression(exp);}
    	;

expression returns[Operand operand] 
    	: exp = exclusiveOrExpression {operand = exp;} ( t = '|' exp = exclusiveOrExpression {operand = BitwiseExpressionFactory.createBitwiseExpression(t.getText(), operand, exp);})*
    	;

exclusiveOrExpression returns[Operand operand]
    	: exp = andExpression {operand = exp;} ( t = '^' exp = andExpression {operand = BitwiseExpressionFactory.createBitwiseExpression(t.getText(), operand, exp);})*
    	;

andExpression returns[Operand operand]
    	: exp = shiftExpression {operand = exp;} ( t = '&'  exp = shiftExpression {operand = BitwiseExpressionFactory.createBitwiseExpression(t.getText(), operand, exp);})*
    	;

shiftExpression returns[Operand operand]
    	: exp = additiveExpression {operand = exp;} ( t = ('<<'|'>>') exp = additiveExpression {operand = BitwiseExpressionFactory.createBitwiseExpression(t.getText(), operand, exp);})*
    	;

additiveExpression returns[Operand operand]
    	: exp = multiplicativeExpression {operand = exp;} ( t = ('+'|'-')  exp = multiplicativeExpression {operand = ArithmeticExpressionFactory.createArithmeticExpression(t.getText(), operand, exp);})*
    	;

multiplicativeExpression returns[Operand operand]
    	: exp = notExpression {operand = exp;} ( t = ('*'|'/'|'%' ) exp = notExpression {operand = ArithmeticExpressionFactory.createArithmeticExpression(t.getText(), operand, exp);})*
    	;

notExpression returns[Operand operand]
    	: t = '~'? exp = primary
    	{
    	    if (t != null)
    	        operand = BitwiseExpressionFactory.createBitwiseExpression(t.getText(), null, exp);
    	    else
    	        operand = exp;
    	}
    	;

primary returns[Operand operand]
@after{
operand = exp;
}
    	: exp = parExpression
    	| exp = member
    	| exp = constant
    	;
    	
member returns[Operand operand]
	: (exp = variable {operand = exp;} | func = function { operand = func;})
	('[' index = expression? {operand = new ArrayExpression(operand, index);}']')*
	('.' (var = variable { operand = new MemberVariableExpression(operand, var, memberVisitors);}
	| func = function { operand = new MemberFunctionExpression(operand, func);}) 
	('[' index = expression? {operand = new ArrayExpression(operand, index);}']')*)*
	;

function returns[Function function]
	: t = Identifier '(' expList = expressionList? ')' {
	if (functionFactory == null)
		throw new NullPointerException("functionFactory is null!");
	function = functionFactory.createFunction(t.getText(), expList);
	}
	| t = Identifier '(' exp1 = expression 'as' exp2 = expression ')' {
	List<Operand> params = new LinkedList<Operand>();
	params.add(exp1);
	params.add(exp2);
	if (functionFactory == null)
		throw new NullPointerException("functionFactory is null!");
	function = functionFactory.createFunction(t.getText(), params);
	} 
	;
	
variable returns[Variable variable]
	: t = Identifier {variable = new SingleVariable(t.getText());}
	;
	
constant returns[Operand operand]	
@init {}
@after {
((OperandSourceAware)operand).setSource(t);
}
	: t = IntegerLiteral {operand = new LongConstant(t.getText());}
	| t = FloatingPointLiteral {operand = new DoubleConstant(t.getText());}
	| t = StringLiteral {operand = new StringConstant(t.getText());}
	| t = NULL {operand = new NullConstant();}
	| t = TRUE {operand = new BooleanConstant(t.getText());}
	| t = FALSE {operand = new BooleanConstant(t.getText());}
	;

//Lexer

IntegerLiteral
	: HexLiteral
	| DecimalLiteral
	| OctalLiteral;

fragment
HexLiteral : '0' ('x'|'X') HexDigit+ ;

fragment
DecimalLiteral : ('+'|'-')? ('0' | '1'..'9' '0'..'9'*) ;
//DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) ;

fragment
OctalLiteral : '0' ('0'..'7')+ ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    	:   ('+'|'-')? ('0'..'9')+ '.' ('0'..'9')* Exponent?
    	|   '.' ('0'..'9')+ Exponent?
    	|   ('+'|'-')? ('0'..'9')+ Exponent
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
	: Letter (Letter | Digital)*
	| '`' (~'`')* '`'
	;
	
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
