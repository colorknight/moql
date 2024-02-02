grammar Selector;

options {
language = Java;
backtrack=true;
}

//tokens {
//}

@header {
package org.datayoo.moql.antlr;

import java.util.LinkedList;
import org.datayoo.moql.*;
import org.datayoo.moql.metadata.*;
import org.datayoo.moql.util.*;

}
@lexer::header {
package org.datayoo.moql.antlr;

}

ALL		:	A_ L_ L_;
AND		:	A_ N_ D_;
AS		:	A_ S_;
ASCENDING	: A_ S_ C_;
BETWEEN	:	B_ E_ T_ W_ E_ E_ N_;
BY		:	B_ Y_;
CACHE	:	C_ A_ C_ H_ E_;
CASE	:	C_ A_ S_ E_;
COMPLEMENTATION	:	C_ O_ M_ P_ L_ E_ M_ E_ N_ T_ A_ T_ I_ O_ N_;
DECORATE	:	D_ E_ C_ O_ R_ A_ T_ E_;
DESCENDING	: 	D_ E_ S_ C_;
DISTINCT	:	D_ I_ S_ T_ I_ N_ C_ T_;
ELSE		:	E_ L_ S_ E_;
END		    :	E_ N_ D_;
EXCEPT		:	E_ X_ C_ E_ P_ T_;
EXISTS		:	E_ X_ I_ S_ T_ S_;
FALSE		:	F_ A_ L_ S_ E_;
FIFO		:	F_ I_ F_ O_;
FILO		:	F_ I_ L_ O_;
FROM		:	F_ R_ O_ M_;
FULL		:	F_ U_ L_ L_;
GROUP		:	G_ R_ O_ U_ P_;
HAVING		:	H_ A_ V_ I_ N_ G_;
IN			:	I_ N_;
INNER		:	I_ N_ N_ E_ R_;
INTERSECT	:	I_ N_ T_ E_ R_ S_ E_ C_ T_;
IS			:	I_ S_;
JOIN		:	J_ O_ I_ N_;
LEFT		:	L_ E_ F_ T_;
LFU			:	L_ F_ U_;
LIKE		:	L_ I_ K_ E_;
LRU			:	L_ R_ U_;
NOT			:	N_ O_ T_;
NULL		:	N_ U_ L_ L_;
ON			:	O_ N_;
OR			:	O_ R_;
ORDER		:	O_ R_ D_ E_ R_;
OUTER		:	O_ U_ T_ E_ R_;
RIGHT		:	R_ I_ G_ H_ T_;
SELECT		:	S_ E_ L_ E_ C_ T_;
SYMEXCEPT	:	S_ Y_ M_ E_ X_ C_ E_ P_ T_;
THEN        :   T_ H_ E_ N_;
LIMIT		:	L_ I_ M_ I_ T_;
TRUE		:	T_ R_ U_ E_;
UNION		:	U_ N_ I_ O_ N_;
WHERE		:	W_ H_ E_ R_ E_;
WHEN		:	W_ H_ E_ N_;

selector returns[SelectorDefinition selectorDefinition]
	: queryMetadata = queryExpression {selectorDefinition = queryMetadata;}
	;
	
queryExpression returns[SelectorDefinition selectorDefinition]
	: unionMetadata = unionExpression {selectorDefinition = unionMetadata;}
	| exceptMetadata = exceptExpression {selectorDefinition = exceptMetadata;}
	| symexceptMetadata = symexceptExpression {selectorDefinition = symexceptMetadata;}
	| complementationMetadata = complementationExpression {selectorDefinition = complementationMetadata;}
	| selectorMetadata = queryTerm {selectorDefinition = selectorMetadata;}
	;

unionExpression returns[SelectorDefinition selectorDefinition]
@init {
SetlectorMetadata setlectorMetadata = new SetlectorMetadata();
ColumnsMetadata columns = new ColumnsMetadata();
List<SelectorDefinition> sets = new LinkedList<SelectorDefinition>();
}
	: '('lSelectorDefinition2 = queryTerm ')' UNION unionAll = ALL?  '(' rSelectorDefinition2 = queryExpression ')' orderBy = orderByClause? limitMetadata = limitClause? decorateBy = decorateByClause?
	{
	if (unionAll == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition2);
	sets.add(rSelectorDefinition2);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.UNION);
	if (orderBy != null) {
		setlectorMetadata.setOrderBy(orderBy);
	}
	if (limitMetadata != null) {
		setlectorMetadata.setLimit(limitMetadata);
	}
	if (decorateBy != null) {
		setlectorMetadata.setDecorateBy(decorateBy);
	}
	selectorDefinition = setlectorMetadata;
	}
	| lSelectorDefinition1 = queryTerm UNION unionAll = ALL? rSelectorDefinition1 = queryExpression
	{
	if (unionAll == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition1);
	sets.add(rSelectorDefinition1);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.UNION);
	selectorDefinition = setlectorMetadata;
	}
	;

exceptExpression returns[SelectorDefinition selectorDefinition]
@init {
SetlectorMetadata setlectorMetadata = new SetlectorMetadata();
ColumnsMetadata columns = new ColumnsMetadata();
List<SelectorDefinition> sets = new LinkedList<SelectorDefinition>();
}
	: '('lSelectorDefinition2 = queryTerm ')' EXCEPT exceptAll = ALL? '(' rSelectorDefinition2 = queryExpression ')' orderBy = orderByClause? limitMetadata = limitClause? decorateBy = decorateByClause? 
	{
	if (exceptAll == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition2);
	sets.add(rSelectorDefinition2);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.EXCEPT);
	if (orderBy != null) {
		setlectorMetadata.setOrderBy(orderBy);
	}
	if (limitMetadata != null) {
		setlectorMetadata.setLimit(limitMetadata);
	}
	if (decorateBy != null) {
		setlectorMetadata.setDecorateBy(decorateBy);
	}
	selectorDefinition = setlectorMetadata;
	}
	| lSelectorDefinition1 = queryTerm EXCEPT exceptAll = ALL? rSelectorDefinition1 = queryExpression
	{
	if (exceptAll == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition1);
	sets.add(rSelectorDefinition1);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.EXCEPT);
	selectorDefinition = setlectorMetadata;
	}
	;

symexceptExpression returns[SelectorDefinition selectorDefinition]
@init {
SetlectorMetadata setlectorMetadata = new SetlectorMetadata();
ColumnsMetadata columns = new ColumnsMetadata();
List<SelectorDefinition> sets = new LinkedList<SelectorDefinition>();
}
	: '('lSelectorDefinition2 = queryTerm ')' SYMEXCEPT symexceptAll = ALL? '(' rSelectorDefinition2 = queryExpression ')' orderBy = orderByClause? limitMetadata = limitClause? decorateBy = decorateByClause?
	{
	if (symexceptAll == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition2);
	sets.add(rSelectorDefinition2);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.SYMEXCEPT);
	if (orderBy != null) {
		setlectorMetadata.setOrderBy(orderBy);
	}
	if (limitMetadata != null) {
		setlectorMetadata.setLimit(limitMetadata);
	}
	if (decorateBy != null) {
		setlectorMetadata.setDecorateBy(decorateBy);
	}
	selectorDefinition = setlectorMetadata;
	}
	| lSelectorDefinition1 = queryTerm SYMEXCEPT symexceptAll = ALL? rSelectorDefinition1 = queryExpression
	{
	if (symexceptAll == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition1);
	sets.add(rSelectorDefinition1);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.SYMEXCEPT);
	selectorDefinition = setlectorMetadata;
	}
	;
	
complementationExpression returns[SelectorDefinition selectorDefinition]
@init {
SetlectorMetadata setlectorMetadata = new SetlectorMetadata();
ColumnsMetadata columns = new ColumnsMetadata();
List<SelectorDefinition> sets = new LinkedList<SelectorDefinition>();
}
	: '('lSelectorDefinition2 = queryTerm ')' SYMEXCEPT symexceptAll = ALL? '(' rSelectorDefinition2 = queryExpression ')' orderBy = orderByClause? limitMetadata = limitClause? decorateBy = decorateByClause?
	{
	if (complementationAll == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition2);
	sets.add(rSelectorDefinition2);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.COMPLEMENTATION);
	if (orderBy != null) {
		setlectorMetadata.setOrderBy(orderBy);
	}
	if (limitMetadata != null) {
		setlectorMetadata.setLimit(limitMetadata);
	}
	if (decorateBy != null) {
		setlectorMetadata.setDecorateBy(decorateBy);
	}
	selectorDefinition = setlectorMetadata;
	}
	| lSelectorDefinition1 = queryTerm COMPLEMENTATION complementationAll = ALL? rSelectorDefinition1 = queryExpression
	{
	if (complementationAll == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition1);
	sets.add(rSelectorDefinition1);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.COMPLEMENTATION);
	selectorDefinition = setlectorMetadata;
	}
	;
orderByClause returns[List<OrderMetadata> orders]
@init{
orders = new LinkedList<OrderMetadata>();
}
	: ORDER BY order = sortSpecification {orders.add(order);} (',' order = sortSpecification {orders.add(order);})*
	;

sortSpecification returns[OrderMetadata order]
@after {
if (type == null) {
	order = new OrderMetadata($expr.expressionText);
} else {
	order = new OrderMetadata($expr.expressionText, type);
}
}
	: expr = expression type = orderingSpecification?
	;

orderingSpecification returns[OrderType type]
@after {
type = OrderType.valueOf(t.getText().toUpperCase());
}
	: t = ASCENDING | t = DESCENDING
	;
	
queryTerm returns[SelectorDefinition selectorDefinition]
@init {
SetlectorMetadata setlectorMetadata = new SetlectorMetadata();
ColumnsMetadata columns = new ColumnsMetadata();
List<SelectorDefinition> sets = new LinkedList<SelectorDefinition>();
}
	: intersectMetadata = intersectExpression {selectorDefinition = intersectMetadata;}
	| queryMetadata = queryPrimary {selectorDefinition = queryMetadata;}
	;

intersectExpression returns[SelectorDefinition selectorDefinition]
@init {
SetlectorMetadata setlectorMetadata = new SetlectorMetadata();
ColumnsMetadata columns = new ColumnsMetadata();
List<SelectorDefinition> sets = new LinkedList<SelectorDefinition>();
}
	: '('lSelectorDefinition2 = queryPrimary ')' INTERSECT all = ALL? '(' rSelectorDefinition2 = queryTerm ')' orderBy = orderByClause? limitMetadata = limitClause? decorateBy = decorateByClause?
	{
	if (all == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition2);
	sets.add(rSelectorDefinition2);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.INTERSECT);
	if (orderBy != null) {
		setlectorMetadata.setOrderBy(orderBy);
	}
	if (limitMetadata != null) {
		setlectorMetadata.setLimit(limitMetadata);
	}
	if (decorateBy != null) {
		setlectorMetadata.setDecorateBy(decorateBy);
	}
	selectorDefinition = setlectorMetadata;
	}
	| lSelectorDefinition1 = queryPrimary INTERSECT all = ALL? rSelectorDefinition1 = queryTerm
	{
	if (all == null) {
		columns.setDistinct(true);
	}
	setlectorMetadata.setColumns(columns);
	sets.add(lSelectorDefinition1);
	sets.add(rSelectorDefinition1);
	setlectorMetadata.setSets(sets);
	setlectorMetadata.setCombinationType(CombinationType.INTERSECT);
	selectorDefinition = setlectorMetadata;
	}
	;


queryPrimary returns[SelectorDefinition selectorDefinition]
	: selectorMetadata = querySpecification {selectorDefinition = selectorMetadata;}
	| '(' queryMetadata = queryExpression {selectorDefinition = queryMetadata;}')'
	;
 
querySpecification returns[SelectorMetadata selectorMetadata]
@init {
selectorMetadata = new SelectorMetadata();
}
@after {
if (cacheMetadata != null)
	selectorMetadata.setCache(cacheMetadata);
columnsMetadata.setDistinct(distinct);
selectorMetadata.setColumns(columnsMetadata);
selectorMetadata.setTables(tablesMetadata);
if (whereMetadata != null)
	selectorMetadata.setWhere(whereMetadata);
if (groupBy != null)
	selectorMetadata.setGroupBy(groupBy);
if (havingMetadata != null)
	selectorMetadata.setHaving(havingMetadata);
if (orderBy != null) 
	selectorMetadata.setOrderBy(orderBy);
if (limitMetadata != null)
	selectorMetadata.setLimit(limitMetadata);
if (decorateBy != null)
	selectorMetadata.setDecorateBy(decorateBy);
}	
	: SELECT cacheMetadata = cache? distinct = setQuantifier? columnsMetadata = selectList 
	tablesMetadata = fromClause whereMetadata = whereClause? groupBy = groupByClause? havingMetadata = havingClause?
	 orderBy = orderByClause? limitMetadata = limitClause? decorateBy = decorateByClause?
	;

cache returns[CacheMetadata cache]
@after {
if (strategy == null) {
	cache = new CacheMetadata(Integer.valueOf(size.getText()));
} else {
    cache = new CacheMetadata(Integer.valueOf(size.getText()), strategy); 
}
}
	: CACHE '(' size = IntegerLiteral (',' strategy = washoutStrategy )?')'
	;

washoutStrategy returns[WashoutStrategy strategy]
@after {
strategy = WashoutStrategy.valueOf(t.getText().toUpperCase());
}
	: t = FIFO | t = FILO | t = LRU | t = LFU
	;
	
setQuantifier returns[boolean distinct]
	: DISTINCT {distinct = true;} | ALL {distinct = false;}
	;
	
limitClause returns[LimitMetadata limit]
@after {
	int nStartPos = 0;
	if (startPos != null) {
		nStartPos = Integer.valueOf(startPos.getText()).intValue();
	}
	String text = size.getText();
	if (percent == null) 
		limit = new LimitMetadata(nStartPos, Integer.valueOf(text), false);
	else {
		limit = new LimitMetadata(nStartPos, Integer.valueOf(text), true);
	}
}
	: LIMIT (startPos = IntegerLiteral ',')? size = IntegerLiteral (percent = '%')? 
	;

selectList returns[ColumnsMetadata columns]
@init{
columns = new ColumnsMetadata();
List<ColumnMetadata> columnList = new LinkedList<ColumnMetadata>();
}
@after{
columns.setColumns(columnList);
}
	: col = column {columnList.add(col);} (',' col = column {columnList.add(col);})*
	;
	
column returns[ColumnMetadata column]
@init {
String name = null;
}
	: expr = expression (AS? t = Identifier)?
	{
	  if (t != null) {
	  	name = t.getText();
	  } else {
	  	name = $expr.expressionText;
	  }
	  int index = name.lastIndexOf('.');
	  if (index != -1) {
	    name = name.substring(index+1);
	  }
	  column = new ColumnMetadata(name, $expr.expressionText);
	}
	| (expr2 = Identifier '.')? '*'
	{
	    name = "*";
	    if (expr2 != null) {
            name = expr2.getText()+".*";
	    }
	  column = new ColumnMetadata(name, name);
	}
	| selectorDefinition = queryExpression AS? t = Identifier
	{
	  column = new ColumnMetadata(t.getText(), selectorDefinition);
	}
	| caseMetadata = caseClause AS? t = Identifier
	{
	  column = new ColumnMetadata(t.getText(), caseMetadata);
	}
	;

caseClause returns[CaseMetadata caseMetadata]
@init {
List<WhenMetadata> whenMetadatas = new LinkedList<WhenMetadata>();
}
@after {
if (whenMetadatas.size() > 0) {
    caseMetadata = new CaseMetadata(whenMetadatas, $expr.expressionText);
}
}
:
    CASE whenMetadata = whenClause { whenMetadatas.add(whenMetadata);}
    (whenMetadata = whenClause { whenMetadatas.add(whenMetadata);})*
    ELSE expr = expression?
    END
;

whenClause returns[WhenMetadata whenMetadata]
:
    WHEN conditionMetadata = searchCondition THEN expr = expression { whenMetadata = new WhenMetadata(conditionMetadata, $expr.expressionText);}
;
  
fromClause returns[TablesMetadata tablesMetadata]
@init {
List<QueryableMetadata> queryableMetadatas = new LinkedList<QueryableMetadata>();
}
@after {
tablesMetadata = new TablesMetadata(queryableMetadatas);
}
	: FROM queryableMetadata = tableReference {queryableMetadatas.add(queryableMetadata);} 
	(',' queryableMetadata = tableReference {queryableMetadatas.add(queryableMetadata);})* 
	;

tableReference returns[QueryableMetadata queryableMetadata]
	: joinMetadata1 = qualifiedJoin {queryableMetadata = joinMetadata1;}
	| '(' joinMetadata2 = qualifiedJoin {queryableMetadata = joinMetadata2;}')'
	| tableMetadata = nonJoinTableReference {queryableMetadata = tableMetadata;}
	;

nonJoinTableReference returns[TableMetadata tableMetadata]
	: value = tableName (AS? name = Identifier)?
{
    String tableName = $value.tableName;
    if (name != null) {
        tableName = name.getText();
    } else {
        if (TlcMoqlMode.isMoqlMode()) {
            throw new IllegalStateException("Table clause in moql must has table alias!");
        }
    }
    int index = tableName.lastIndexOf('.');
    if (index != -1) {
        tableName = tableName.substring(index+1);
    }
    tableMetadata = new TableMetadata(tableName,$value.tableName);
}
	| '(' queryMetadata = queryExpression ')' AS? name = Identifier {tableMetadata = new TableMetadata(name.getText(), queryMetadata);}
	;

tableName returns[String tableName]
    : Identifier('.'Identifier)* {$tableName=$text;}
    ;

whereClause returns[ConditionMetadata whereMetadata]
	: WHERE operationMetadata = searchCondition {whereMetadata = new ConditionMetadata(operationMetadata);}
	;

groupByClause returns[List<GroupMetadata> groupBy]
@init {
groupBy = new LinkedList<GroupMetadata>();
}
	: GROUP BY expr = expression {groupBy.add(new GroupMetadata($expr.expressionText));}(',' expr = expression {groupBy.add(new GroupMetadata($expr.expressionText));})*
	;
	
havingClause returns[ConditionMetadata havingMetadata]
	: HAVING operationMetadata = searchCondition { havingMetadata = new ConditionMetadata(operationMetadata);}
	;
	
decorateByClause returns[List<DecorateMetadata> decorateBy]
@init {
decorateBy = new LinkedList<DecorateMetadata>();
}
	: DECORATE BY dec = decorator {decorateBy.add(new DecorateMetadata($dec.expressionText));}(',' dec = decorator {decorateBy.add(new DecorateMetadata($dec.expressionText));})*
	;
	
decorator returns[String expressionText]
	: function {$expressionText = $text;}
	;
	
qualifiedJoin returns[JoinMetadata joinMetadata]
@init {
QueryableMetadata lQueryableMetadata = null;
}
	: tableMetadata = nonJoinTableReference {lQueryableMetadata = tableMetadata;} (type = joinType? JOIN rQueryableMetadata = joinReference on = joinCondition?
	{
	if (type == null) {
		type = JoinType.INNER;
	}
	joinMetadata = new JoinMetadata(type, lQueryableMetadata, rQueryableMetadata);
	if (on != null) {
		joinMetadata.setOn(on);
	}
	lQueryableMetadata = joinMetadata;
	}
	)+
	;

joinType returns[JoinType type]
	: t = INNER {type = JoinType.valueOf(t.getText().toUpperCase());}
	| outType = outJoinType OUTER? {type = outType;}
	;

outJoinType returns[JoinType type]
@after {
type = JoinType.valueOf(t.getText().toUpperCase());
}
	: t = LEFT | t = RIGHT | t = FULL
	;

joinReference returns[QueryableMetadata queryableMetadata]
	:  '(' joinMetadata = qualifiedJoin ')' {queryableMetadata = joinMetadata;}
	| tableMetadata = nonJoinTableReference {queryableMetadata = tableMetadata;}
	;

joinCondition returns[ConditionMetadata onMetadata]
	: ON operationMetadata = searchCondition {onMetadata = new ConditionMetadata(operationMetadata);}
	;

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
	| expr = expression {operationMetadata = new RelationOperationMetadata(SelectorConstants.EXPR, $expr.expressionText);}
	;
	
predicate returns[OperationMetadata operationMetadata]
	: comparisonMetadata = comparisonPredicate {operationMetadata = comparisonMetadata;}
	| betweenMetadata = betweenPredicate {operationMetadata = betweenMetadata;}
	| inMetadata = inPredicate {operationMetadata = inMetadata;}
	| likeMetadata = likePredicate {operationMetadata = likeMetadata;}
	| nullMetadata = nullPredicate {operationMetadata = nullMetadata;}
	| existsMetadata = existsPredicate {operationMetadata = existsMetadata;}
	;
	
comparisonPredicate returns[OperationMetadata operationMetadata]
@after {
operationMetadata = new RelationOperationMetadata(op.getText(), $lExpr.expressionText, $rExpr.expressionText);
}
	: lExpr = expression op = ('=' | '<' | '<=' | '>' | '>=' | '<>'| '!=') rExpr = expression
	;
	
betweenPredicate returns[OperationMetadata operationMetadata]
@after {
String rExpr = StringFormater.format("({},{})", $rExpr1.expressionText, $rExpr2.expressionText);
operationMetadata = new RelationOperationMetadata(SelectorConstants.BETWEEN, $lExpr.expressionText, rExpr);
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), operationMetadata);
}
}
	: lExpr = expression not = NOT? BETWEEN rExpr1 = expression AND rExpr2 = expression
	;
	
inPredicate returns[OperationMetadata operationMetadata]
@after {
if (selectorDefinition != null) {
	operationMetadata = new RelationOperationMetadata(SelectorConstants.IN, $lExpr.expressionText, selectorDefinition);
} else {
	String inExpressionText = StringFormater.format("({})", $rExpr.expressionText);
	operationMetadata = new RelationOperationMetadata(SelectorConstants.IN, $lExpr.expressionText, inExpressionText);
}
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), operationMetadata);
}
}
	: lExpr = expression not = NOT? IN '(' (selectorDefinition = queryExpression | rExpr = expressionList) ')'
	;

likePredicate returns[OperationMetadata operationMetadata]
@after {
operationMetadata = new RelationOperationMetadata(SelectorConstants.LIKE, $lExpr.expressionText, $rExpr.expressionText);
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), operationMetadata);
}
}
	: lExpr = expression not=NOT? LIKE rExpr = expression
	;
	
nullPredicate returns[OperationMetadata operationMetadata]
@after {
operationMetadata = new RelationOperationMetadata(SelectorConstants.IS, $lExpr.expressionText, rExpr.getText());
if (not != null) {
	operationMetadata = new LogicOperationMetadata(not.getText(), operationMetadata);
}
}
	: lExpr = expression IS not = NOT? rExpr = NULL
	;

existsPredicate returns[OperationMetadata operationMetadata]
@after {
operationMetadata = new RelationOperationMetadata(SelectorConstants.EXISTS, queryMetadata);
}
	: EXISTS '(' queryMetadata = queryExpression ')'
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
    	: shiftExpression ('&' shiftExpression)*
    	;
shiftExpression
    	: additiveExpression (('<<'|'>>') additiveExpression)*
    	;
    
additiveExpression
    	: multiplicativeExpression (('+' | '-')  multiplicativeExpression)*
    	;

multiplicativeExpression
    	: notExpression (( '*' | '/' | '%' ) notExpression)*
    	;
notExpression
    	: '~'? exp = primary;
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
	| Identifier '(' expression AS expression ')'
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

IntegerLiteral
	: DecimalLiteral
	| HexLiteral
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