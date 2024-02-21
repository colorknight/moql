(Note: moql-translator, moql-querier can be transferred to moql-transx project)

English | [中文](https://github.com/colorknight/moql/blob/master/readme.md)

# MOQL Introduction

​	MOQL (MemoryObject Query Language) is a Java-based open source tool for memory object filtering, query and statistical analysis. It can complete query and statistical analysis functions similar to those provided by the database for collection objects stored in memory. The objects in the collection can be Bean objects, array objects, Map objects and other objects. Its syntax structure is similar to SQL, supporting distinct, where, group, having, order, limit, aggregation operations (count, sum, avg, min, max), multi-table query, join query (left, right, inner, full) , nested queries, and set operations (union, intersect, except), etc. like:

```
List<BeanA> beanAList = BeanFactory.createBeanAList(0,100);
DataSetMap dataSetMap = new DataSetMapImpl();
dataSetMap.putDataSet("BeanA", beanAList);
String sql = "select count(a.id) cnt, sum(a.num) sum, a.num%500 mod from BeanA a group by 3 having mod > 10 order by 1";
try {
		Selector selector = MoqlEngine.createSelector(sql);
		selector.select(dataSetMap);
		RecordSet recordSet = selector.getRecordSet();
		outputRecordSet(recordSet);
} catch (MoqlException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
}
```

For more details, please see the [Selector](#Selector)

​	MOQL has different concerns than in-memory databases (relational). The in-memory database focuses on OLTP (Online Transaction Processing) to ensure the ACID characteristics of the data, and based on this, it provides users with query and statistical analysis functions. When we use the query or statistical analysis function of an in-memory database, we first need to insert data into the relevant tables of the database. This operation often requires an O-R conversion process, that is, converting objects into table records. Then, use SQL statements to perform statistical analysis on the data according to the table structure. MOQL does not pay attention to the storage form of data, nor does it make any agreement on the storage form of data. Its goal is to provide direct query and statistical analysis functions for any object loaded into memory by users, thereby simplifying developers' programming complexity and providing developers with more development options. The goal of its syntax design is "object", which is different from the "record" orientation in in-memory database. This requires some getting used to for users who are used to thinking and writing SQL in terms of tables and records. Compared with SQL, MOQL is more "object-oriented" in the Java-based object-oriented development process.

​	In terms of performance, from an implementation perspective, there will be a certain gap between MOQL and in-memory databases. MOQL is an object-oriented query, and many data types are dynamically bound at runtime, which will affect the efficiency of data calculation and comparison; while the data table structure in the memory database is predefined, the data type of the data is clear, and when querying Calculations and comparisons can be made faster. But this does not mean that MOQL is completely useless in situations with high performance requirements. When an application has fixed real-time query and statistical analysis requirements, and its data continues to accumulate and grow, it can be solved with MOQL. The growing data can continuously flow through the Selector provided by MOQL like running water (see "MOQL-Selector (Selector)" for details). The Selector defines the requirements for query statistical analysis, and all data flowing through the Selector will be processed in real time. Statistical analysis, statistical analysis results can be accumulated and can be read at any time. All data can be statistically analyzed before being persisted, making the application effect simpler and easier than in-memory databases.

​	MOQL has similar functions to LINQ (Language Integrated Query) provided in .NET. Both can perform statistical analysis on memory objects. However, LINQ provides too many interfaces and the syntax rules also change a lot. Different languages such as C# and VB have different restrictions when using LINQ, making it difficult to master. MOQL is closer to SQL in syntax. For programmers with SQL foundation, the learning curve is low and easier to master.

​	 MOQL tools provide three main applications: Selector (selector), Filter (filter) and Operand (operand). Selector provides query and statistical analysis functions for memory data objects. For more details, see Selector (Selector); Filter provides filtering functions for memory data objects, and its expressions support "=", ">" and "< ", ">=", "<=", "<>", "between", "like", "in" and "is" and other relational operators as well as logical operators such as "and", "or" and "not" Operators such as:

```
try {
	Filter filter1 = MoqlEngine.createFilter("bean.num < 100 and num > 100 or bean.num = 100 and bean.name in ('Abean', 'Bbean')");
	assertTrue(filter1.isMatch(entityMap));
	filter1 = MoqlEngine.createFilter("bean.num < 100 and (num > 100 or bean.num = 100)");
	assertFalse(filter1.isMatch(entityMap));
} catch (MoqlException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
```

​	 For more details, see Filter (Filter); Operand provides expression parsing and calculation functions, and its supported expression types are: constants (integers, floating point numbers, strings), variables (ordinary variables, member variables), functions (Ordinary functions, member functions), arrays (system arrays, linked lists, Maps, etc.), operation expressions ("+", "-", "*", "/", "%", "&", "|" , "^"), etc. like:

```
EntityMap entityMap = new EntityMapImpl();
entityMap.putEntity("bean", new BeanA("bean", 5));
entityMap.putEntity("num1", 3);
entityMap.putEntity("num2", 4);
try {
	Operand arithmetic = MoqlEngine.createOperand("(bean.getNum() * num1) / num2 * 2.2 + 2 - 1");
	System.out.println(arithmetic.toString() + " " + arithmetic.getOperandType());
	System.out.println(arithmetic.operate(entityMap));
} catch (MoqlException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
```

For more information, see [Operand]()

# Operand

​	Operand is an important part of the MOQL syntax structure. The data columns or data values in the syntax structure that need to be analyzed and processed are called Operand. For example, the data columns followed by select, the condition fields described in the where condition, and the constant values that need to be matched are all called Operand. For example, the red font part in the following statement indicates an Operand. By calculating it, we can obtain the data results and form the final data result set we solve.

```
select count(a.id) cnt, sum(a.num) sum, a.num%500 mod from BeanA a group by 3 having mod > 10 order by 1
```

​	In MOQL, Operand can not only be used in a complete syntax structure, but also can be used alone. MOQL provides a method to directly create an operand and use this operand to complete the data solution. as follows:

```
EntityMap entityMap = new EntityMapImpl();
entityMap.putEntity("num", 12);
entityMap.putEntity("num1", 3);
entityMap.putEntity("num2", 4);
try {
	Operand arithmetic = MoqlEngine.createOperand("(num * num1) / num2 * 2.2 + 2 - 1");
	System.out.println(arithmetic.operate(entityMap));
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	 In this example, an operand is created for the expression "(num * num1) / num2 * 2.2 + 2 - 1". The operand uses the incoming entity Map to solve the expression and outputs the execution result. The result is 20.8. The createOperand method of MoqlUtils can create an operand. The parameter passed to the method is an operand expression string, and the returned object is an Operand interface. The interface is located in the package path of org.moql and is defined as follows:

```
public interface Operand {
	/*获得操作数的类型*/
	OperandType getOperandType();
	/*返回操作数的名字*/
	String getName();
	/*返回操作数在文本串中的位置，返回对象类型为org.antlr.runtime.Token */
	Object getSource();
	/*根据给定的实体Map传入的值，计算操作数的值*/
	Object operate(EntityMap entityMap);
	/*根据给定的实体Map传入的值，计算操作数的布尔值。*/
	boolean booleanOperate(EntityMap entityMap);
	/*操作数的返回值是否为一固定常量。*/
	boolean isConstantReturn();
	/*重置操作数，将操作数状态置为初始状态。*/
	void reset();
}
```

​	The getOperandType() method is used to return the type of Operand, OperaandType. OperandType is an enumeration type and is also located in the package path of org.moql. The definition format is as follows:

```
public enum OperandType {
	UNKNOWN,	
	CONSTANT,	
	VARIABLE,
	FUNCTION,
	EXPRESSION,
	COLUMNSELECTOR
}
```

​	 It includes five types of Operand: constant (CONSTANT), variable (VARIABLE), function (FUNCTION), expression (EXPRESSION) and column filter (COLUMNSELECTOR) (Note: Operand types will be introduced in detail later). When the Operand does not belong to any of the above categories, it is expressed as UNKNOWN.

​	The getName() method is used to get the name of Operand. Except for the function type Operand whose name is the function name, the name of other types of Operand is the string itself that generates the Operand. For example: "sum(a.num)" is parsed as a whole function Operand, whose name is "sum", which embeds an expression Operand parsed by "a.num" as a parameter, and the Operand's name is "a.num"; another example: "123" is parsed into the constant Operand, whose name is "123", etc.

​	The getSource() method is used to return the position of the operand in the text string. For example: "sum(a.num)" will be parsed into multiple operands. Calling this method on each operand can locate the position of the operand in the text string.

​	The operate(EntityMapentityMap) method is the main method provided by Operand. The operands of this method can be used to evaluate the given parameters. EntityMap is a Map containing entity objects. If its entity name is consistent with the name of the variable Operand, then the entity object will be bound to the variable Operand for solution.

​	 The booleanOperate(EntityMapentityMap) method is an extension of the operate(EntityMap entityMap) method. Used to return Operand's calculation results in the form of Boolean values. If the type of the return value of the operate method is java.lang.Boolean, the method returns the value obtained by calling Boolean.booleanValue(); if the type of the return value of the operate method is not java.lang.Boolean and the return value is null, false is returned. , otherwise return true;

​	The isConstantReturn() method is used to tell the caller whether a constant value can always be returned after calling Operand's operate method. If the Operand is a constant type Operand, the call always returns true, which means that calling the operate method of the constant type Operand always returns the same constant value. A more meaningful use of this method is the call in the function type Operand and the expression type Operand. During initialization, these two types of Operands will determine whether the method calls of all related Operands return true. If both are true, it means that the execution result of the operand has nothing to do with the EntityMap parameter passed in when the operate method is called. Then the operand can be solved in advance instead of waiting to be solved every time the operate method is called. This can improve the execution efficiency of Operand to a certain extent. If you are interested in calling this method, you can view the related initialization implementation of the org.datayoo.moql.operand.function.AbstractFunction class and the org.datayoo.moql.operand.expression.arithmetic.AbstractArithmeticExpression class in the source code.

​	The reset() method is used to reset Operand and restore the state of Operand to its initial state. Operand is divided into two situations: stateful and stateless during calculation. Stateful means that when Operand's operate method is called multiple times, its calculation results are affected by the results of previous calls; stateless means that multiple calls will not be affected by the results of previous calls. For stateful Operand, its state needs to be restored to the initial state by calling the reset method. For example: Operand corresponding to "sum(a.num)" will accumulate the results of each call to the operate method and sum the values of each call. If you need to restart counting, you need to call the reset method to restore the Operand to its initial value. Similar Operand also has aggregate functions such as count, avg, max, and min.

## Constant Operand

​	The constant types supported by Operand include string, double floating point (Double), long integer (Long) and NULL types.

​	The format of the string Operand is consistent with the format of the string in SQL. Both ends of the string need to be surrounded by single quotes (‘), such as: ‘String’, ‘String’, ‘123’, ‘123.1’, ‘null’, etc. all represent strings. When a string also needs to contain a single quote ('), it fully complies with the constraints of SQL strings. Use two connected single quotes ('') to represent a single quote in a string, such as: 'Str''ing 'The string it represents is "Str'ing".

​	The data range that can be represented by double floating point Operand is consistent with the data range that can be represented by the java.lang.Double object in the Java language. It can cover the data range of floating point (float) data. All floating point numbers in MOQL are represented by double floating point Operands, such as: 123.4, -12.78, 23.3e15, etc.

​	The data range represented by the long integer Operand is consistent with the data range represented by the java.lang.Long object in the Java language. In MOQL, the byte, short, integer and long types supported by SQL syntax are all represented by long integer Operand. In addition, long integer Operand also supports octal, decimal and hexadecimal data formats, such as: 19, 3435L, 0xAFCD, -1235, 072, etc.

​	The NULL type Operand is used to represent null. It has only one form of expression: null, and all letters must be pure lowercase.

​	The code example of constant Operand is as follows:

```
try {	
	Operand constant = MoqlEngine.createOperand("1234");
	System.out.println(constant.toString() + " " + constant.getOperandType());
	constant = MoqlEngine.createOperand("192.16");
	System.out.println(constant.toString() + " " + constant.getOperandType());
	constant = MoqlEngine.createOperand("'中国''china'");
	System.out.println(constant.toString() + " " + constant.getOperandType());
	constant = MoqlEngine.createOperand("null");
	System.out.println(constant.toString() + " " + constant.getOperandType());
	constant = MoqlEngine.createOperand("Null");
	System.out.println(constant.toString() + " " + constant.getOperandType());
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	The execution result output is as follows:

```
1234 CONSTANT
192.16 CONSTANT
'中国''china' CONSTANT
NULL CONSTANT
Null VARIABLE
```

​	The string Null is not parsed into a NULL type constant Operand, but is parsed into a variable type Operand.

## Variable Operand

​	The naming convention of variable type Operand is consistent with the naming convention of variable identifiers in Java syntax. The identifier usually consists of uppercase and lowercase English letters, numbers, underline "_" and dollar sign "$". Numbers cannot be the first letter of the identifier. In addition, the naming of identifiers also supports Chinese characters or characters in other languages, such as: _data, a1, $a, length, num, etc.

 The sample code for the variable Operand is as follows:

```
EntityMap entityMap = new EntityMapImpl();
entityMap.putEntity("num", 123);
entityMap.putEntity("长度", 184);
entityMap.putEntity("$a", 38);
entityMap.putEntity("_data", 32);
try {
	Operand variable = MoqlEngine.createOperand("num");
	System.out.println(variable.toString() + " " + variable.getOperandType());
	System.out.println(variable.operate(entityMap));
	variable = MoqlEngine.createOperand("长度");
	System.out.println(variable.toString() + " " + variable.getOperandType());
	System.out.println(variable.operate(entityMap));
	variable = MoqlEngine.createOperand("$a");
	System.out.println(variable.toString() + " " + variable.getOperandType());
	System.out.println(variable.operate(entityMap));
	variable = MoqlEngine.createOperand("_data");
	System.out.println(variable.toString() + " " + variable.getOperandType());
	System.out.println(variable.operate(entityMap));
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	The execution result output is as follows:

```
num VARIABLE
123
长度 VARIABLE
184
$a VARIABLE
38
_data VARIABLE
32
```

## Function Operand

​	The format of the function Operand is as follows: function name (parameter 1, parameter 2...). The function name is consistent with the naming convention for identifiers in Java syntax, which is described in the variable Operand. Each parameter is an Operand, and Operand can be any one of constants, variables, functions, or expressions. Such as: sum(a), _formatTime(‘yyyy-mm-dd’, getCurrentTime()), random(23+15), etc.

 The sample code of function Operand is as follows:

```
EntityMap entityMap = new EntityMapImpl();
entityMap.putEntity("num", 123);
try {
	Operand function = MoqlEngine.createOperand("max(num)");
	System.out.println(function.toString() + " " + function.getOperandType());
	System.out.println(function.operate(entityMap));
	entityMap.putEntity("num", 345);
	System.out.println(function.operate(entityMap));
    //重置函数状态
	function.reset();
	entityMap.putEntity("num", 12);
	System.out.println(function.operate(entityMap));
	function = MoqlEngine.createOperand("test(1, num, 'a')");
	System.out.println(function.toString() + " " + function.getOperandType());
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	The execution result output is as follows:

```
max(num) FUNCTION
123
345
12
test(1,num,'a') FUNCTION
```

####  Custom function

​	MOQL currently supports some functions Operand, such as aggregate functions Operand: count, sum, avg, min, and max, etc. (see Appendix: Function section for details). These Operands can directly calculate the data by calling the operate method after they are generated. But if you want to create a function that is not supported by MOQL, MOQL will create a default Operand for it, which just parses the function string, such as the function "test(1, num, 'a" in the above code snippet ')". The function string is parsed into a default function Operand, but when we call its operate method, it will throw a java.lang.UnsupportedOperationException, indicating that the Operand does not support this method. In order for MOQL to also support the test function, we need to make a corresponding Operand implementation for test. The relevant implementation code is as follows:

```
public class TestRegistFunction {
	public static class Test extends AbstractFunction {
		public Test(List<Operand> parameters) {
/* “test”是该对象对应的函数Operand的名字，3是test函数可以接受的参数数量。若parameters的size与该值不一样时，会抛出IllegalArgumentException，表示输入的函数字符串不合法*/
		super("test",3,parameters);
			functionType = FunctionType.COMMON;
		}
		@Override
		protected Object innerOperate(EntityMap entityMap) {
			Object obj1 = parameters.get(0).operate(entityMap);
			Object obj2 = parameters.get(1).operate(entityMap);
			Object obj3 = parameters.get(2).operate(entityMap);
			StringBuffer sbuf = new StringBuffer();
			sbuf.append(obj1.toString());
			sbuf.append("||");
			sbuf.append(obj2.toString());
			sbuf.append("||");
			sbuf.append(obj3.toString());
			return sbuf.toString();
		}	
	}
	public static void main(String[] args) {
		EntityMap entityMap = new EntityMapImpl();
		entityMap.putEntity("num", 123);
		try {
			Operand function = MoqlUtils.createOperand("test(1, num, 'a')");
			System.out.println(function.toString() + " " + function.getOperandType());
			System.out.println(function.operate(entityMap));
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
             //注册test函数的实现
			MoqlUtils.registFunction("test", Test.class.getName());
			Operand function = MoqlUtils.createOperand("test(1, num, 'a')");
			System.out.println(function.toString() + " " + function.getOperandType());
			System.out.println(function.operate(entityMap));
		} catch (MoqlException e) {
			e.printStackTrace();
		}
	}
}
```

​	 The execution result output is as follows:

```
test(1,num,'a') FUNCTION
java.lang.UnsupportedOperationException
	at org.moql.operand.function.MemberFunction.innerOperate(MemberFunction.java:49)
	at org.moql.operand.function.AbstractFunction.operate(AbstractFunction.java:120)
	at org.moql.core.test.TestRegistFunction.main(TestRegistFunction.java:44)
test(1,num,'a') FUNCTION
1||123||a
```

​	The Test class inherits the AbstractFunction abstract class, which is the Operand implementation of the test function. The test function can receive 3 parameters. When the number of parameters is inconsistent with 3, its constructor will throw an exception, indicating that the calling format of the test function is incorrect. For example: test(1,num) will throw an exception. When we define a function whose parameters are variable length, we can replace 3 with -1 to indicate variable length. Or we use the VARIANT_PARAMETERS constant in org.datayoo.moql.operand.function.Function to represent it, the value is -1. The main logic of the Test class is implemented in innerOperate. The Test class overrides this method, simply concatenates the three parameters into a string and returns it.

From the execution of the above example code, we can see that after creating the Operand of the test function for the first time, calling the operate method threw an exception; so before creating the Operand of the test function for the second time, we called the function registration of MOQL Method, the implementation class Test of Operand is registered for the test function. Then we created the Operand of the test function and called its operate method. This call produced the expected output. The fundamental reason is that the function Operand created for the first time generated a default function Operand because no registered Operand implementation was found. This Operand did not support the operate method; and when we created the function Operand the second time, the registered Operand implementation was found. The implementation class Test is then bound to the implementation and the Operand corresponding to the test function is generated.

## Expression Operand

​	Expression Operand is the richest and most complex type of format in MOQL syntax. It includes three types: array expression, mathematical operation expression and member expression. In addition, we can also see in the MOQL source code that the expression Operand also includes at least relational operation expressions and logical operation expressions. But these two expressions cannot be created directly through MoqlUtils.createOperand(), they must be applied in filters. We will introduce the application of the above two expressions in Filter.

###  Array expression Operand

​	In addition to describing arrays in the traditional sense, array expression Operand can also define and describe collections such as Map and List. Its basic format is: "array object [index]". The array object can be a variable, array expression, or member expression Operand; the index can be a constant, variable, function, or expression Operand.

 	The array objects in the array expression Operand not only include all array objects in the Java language that call the isArray() method and return true, but also include all objects that implement the java.lang.Iterable interface and the java.util.Map interface, as well as java .sql.ResultSet object and org.moql.RecordSet object. ResultSet and RecordSet are the result objects of JDBC query and MOQL query respectively. Except for objects that implement the java.util.Map interface, all other objects in the array object are accessed in numerical index mode, and '0' is used as the index of the first data in the array. If the index is not passed in as a numerical value but as a string, the system will try to convert the string into a numerical index before performing array access, such as array[1], array['1'] wait. The index of an object that implements the java.util.Map interface can be any object, which is the Key of the Map object, such as: map['key']. If the index value of the array expression is not filled in, such as: array[], it means that all the data in the array is to be accessed except the Map object, and the Map object means that all the value data is to be accessed, excluding the Key data.

​	The relevant sample code for array expression Operand is as follows:

```
EntityMap entityMap = new EntityMapImpl();
//生成一个由5个Map对象构建的链表对象	
entityMap.putEntity("bean", BeanFactory.createMapList(5));
try {
	//访问链表中第3个Map对象的以‘value’做索引的数据的值
	Operand member = MoqlEngine.createOperand("bean[2]['value']");
	System.out.println(member.toString() + " " + member.getOperandType());
	System.out.println(member.operate(entityMap));
	//访问链表中的所有Map对象
	member = MoqlEngine.createOperand("bean[]");
	System.out.println(member.toString() + " " + member.getOperandType());
	System.out.println(member.operate(entityMap));
	/*访问第5个Map元素中以’bean’为索引的Bean对象的num属性，该例子结合了成员表达式Operand的格式*/
	member = MoqlEngine.createOperand("bean[4]['bean'].num");
	System.out.println(member.toString() + " " + member.getOperandType());
	System.out.println(member.operate(entityMap));
	/*访问第5个Map元素中以’bean’为索引的Bean对象的getArray()方法返回数组的第6个元素*/
	member = MoqlEngine.createOperand("bean[4]['bean'].getArray()[5]");
	System.out.println(member.toString() + " " + member.getOperandType());
	System.out.println(member.operate(entityMap));
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	 The execution results are as follows:

```
bean[2]['value'] EXPRESSION
0
bean[] EXPRESSION
[{bean=org.moql.core.simulation.BeanA@1309e87, name=0, value=0}, {bean=org.moql.core.simulation.BeanA@f7c31d, name=1, value=0}, {bean=org.moql.core.simulation.BeanA@2acc65, name=2, value=0}, {bean=org.moql.core.simulation.BeanA@1d10a5c, name=3, value=0}, {bean=org.moql.core.simulation.BeanA@ff2413, name=4, value=0}, {bean=org.moql.core.simulation.BeanA@9980d5, name=5, value=0}, {bean=org.moql.core.simulation.BeanA@1d95492, name=6, value=0}, {bean=org.moql.core.simulation.BeanA@13f7281, name=7, value=0}, {bean=org.moql.core.simulation.BeanA@76ab2f, name=8, value=0}, {bean=org.moql.core.simulation.BeanA@e0cc23, name=9, value=0}, 
……	
 {bean=org.moql.core.simulation.BeanA@2f48d2, name=8, value=0}, {bean=org.moql.core.simulation.BeanA@55d93d, name=9, value=0}, 
……	
{bean=org.moql.core.simulation.BeanA@147c1db, name=18, value=0}, {bean=org.moql.core.simulation.BeanA@82d37, name=19, value=0}]
bean[4]['bean'].num EXPRESSION
10
bean[4]['bean'].getArray()[5] EXPRESSION
5
```

###  Arithmetic expression Operand

​	The format of the arithmetic expression Operand is as follows: "Operand1 operator Operand2". The arithmetic operators supported by arithmetic expressions are all binary operators. They are divided according to the order of execution priority: multiplication (*), division (/), modulus (%), which is better than addition (+) and subtraction (-). Operation. The two operands on both sides of the operator can be any type of Operand except column filter Operand. If one of the Operands is another mathematical operation expression Operand, then the entire expression is an expression containing multiple consecutive operator operations, such as: a + 1 * 10 / 5, etc.

 	The relevant sample code for the mathematical operation expression Operand is as follows:

```
EntityMap entityMap = new EntityMapImpl();
entityMap.putEntity("num", 12);
entityMap.putEntity("num1", 3);
entityMap.putEntity("num2", 4);
try {
	Operand arithmetic = MoqlUtils.createOperand("(num * num1) / num2 * 2.2 + 2 - 1");
	System.out.println(arithmetic.toString() + " " + arithmetic.getOperandType());
	System.out.println(arithmetic.operate(entityMap));
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	 The execution results are as follows:

```
(num * num1) / num2 * 2.2 + 2 - 1 EXPRESSION
20.8
```

### Bit operation expression Operand

​	The format of the bitwise operation expression Operand is the same as the arithmetic operation expression, as follows: "Operand1 operator Operand2". Its operation priority is: bitwise AND (&) is better than XOR (^) than bitwise OR (|) operation, and the priority of all bit operations is lower than arithmetic operators.

 	The relevant sample code for the bit operation expression Operand is as follows:

```
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("num", 2);
    entityMap.putEntity("num1", 3);
    entityMap.putEntity("num2", 4);
    try {
      Operand arithmetic = MoqlEngine
          .createOperand(" num << num1 + 1");
      System.out.println(arithmetic.toString() + " = " + arithmetic.operate(entityMap));
      arithmetic = MoqlEngine.createOperand("num2 | num1 & num");
      System.out.println(arithmetic.toString() + " = " + arithmetic.operate(entityMap));
      arithmetic = MoqlEngine.createOperand("~num2 ^ num2");
      System.out.println(arithmetic.toString() + " = " + arithmetic.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
```

 	The execution results are as follows:

```
num << num1 + 1 = 32
num2 | num1 & num = 6
~ (num2) ^ num2 = -1
```

###  Member expression Operand

 	Member expression Operand is used to access member properties or member functions of an object. Its format is as follows: "Object.Member". "." is a member operator, and the object on its left side can be a variable, function, array expression, or member expression Operand; the member on its right side can be a variable or function Operand. When the member is a variable Operand, the "object" must have a corresponding Getter function that can obtain the variable. When the variable is of Boolean type, the function name is prefixed with "is"; when the variable is of non-Boolean type, the function name is prefixed with "get". When the member is a function Operand, the "object" must have a corresponding function, and the parameter type of the function needs to be consistent with the parameter type described in the function Operand.

​	The relevant sample code of member expression Operand is as follows:

```
EntityMap entityMap = new EntityMapImpl();
entityMap.putEntity("bean", new BeanA("bean", 5));
try {
	Operand member = MoqlEngine.createOperand("bean.name");
	System.out.println(member.toString() + " " + member.getOperandType());
	System.out.println(member.operate(entityMap));
	member = MoqlEngine.createOperand("bean.getNum()");
	System.out.println(member.toString() + " " + member.getOperandType());
	System.out.println(member.operate(entityMap));
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	The execution results are as follows:

```
bean.name EXPRESSION
Abean
bean.getNum() EXPRESSION
5
```

### Column filter Operand

 	Column filtering Operand is a special type of Operand. This type of Operand cannot be created directly through the createOperand method of MoqlUtils. Its format is a MOQL query statement that only returns one field, such as: select field1 from table where field2 < 10. This statement only returns one field field1. In addition, the Operand will appear in the output column position of the MOQL syntax, or after the "in" operator of the where condition. For example: select select field1 from table where field2 < 10 as field1, field2 from table2, select field2, field1 from table2 where field2 in (select field1 from table), etc.

# Filter

​	Filter is one of the main functions provided by MOQL. It supports the syntax description of the Where part of the SQL syntax, and can conditionally match the data in the memory by writing conditional statements. It can be used to perform secondary queries on data sets retrieved from the database; it can also be used for real-time filtering of data streams during data processing.

​	The Filter function is based on Operand, which determines which data can be filtered. Simply speaking, it includes: constants, variables, arrays, collections, objects, etc. (see the article MOQL—Operand (operand) for details). These data must be encapsulated in a data object named EntityMap, and the Filter can match the data according to the predefined syntax.

​	The operators supported by Filter include: relational operators and logical operators. Relational operators include: greater than (>), less than (<), equal to (=), greater than or equal to (>=), less than or equal to (<=), not equal to (<>), between..and, like, in, is etc. Logical operators include: logical AND (and), logical OR (or), and logical NOT (not). The operational precedence of relational operators is higher than that of logical operators, but the precedence of all relational operators is the same and there is no difference. The precedence order of logical operators is logical not (not) > logical AND (and) > logical OR (or). Filter also supports parentheses "()", which can change the execution order of operators.

 	Code examples for relational operators are as follows:

```
try {
	EntityMap entityMap = new EntityMapImpl();
	entityMap.putEntity("num", 123);
	entityMap.putEntity("bean", new BeanA("bean", 100)
	Filter filter = MoqlEngine.createFilter("num = 123");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("num < 160");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.num > 0 ");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("num <> 123");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("num >= 123");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("num <= 100");
	System.out.println(filter.isMatch(entityMap));
	// between..and
	filter = MoqlEngine.createFilter("bean.num between 0 and 200");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.num between 0 and 100");
	System.out.println(filter.isMatch(entityMap));
	// like
	filter = MoqlEngine.createFilter("bean.name like '%ean'");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.name like '%ea.'");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.name like '%e%'");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.name like '%c%'");
	System.out.println(filter.isMatch(entityMap));
	// in
	filter = MoqlEngine.createFilter("bean.name in ('Abean', 'Bbean')");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.name in ('Cbean', 'Bbean')");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("50 in (bean.getArray())");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("150 in (bean.getArray())");
	System.out.println(filter.isMatch(entityMap));
	// is
	filter = MoqlEngine.createFilter("bean.name is null");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.name is not null");
	System.out.println(filter.isMatch(entityMap));
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	Code examples of logical operators are as follows:

```
try {
	EntityMap entityMap = new EntityMapImpl();
	entityMap.putEntity("num", 123);
	entityMap.putEntity("bean", new BeanA("bean", 100));
	Filter filter = MoqlEngine.createFilter("num > 100 and bean.num < 200 and bean.name like'%ean'");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.num > 0 and bean.num < 100");
	System.out.println(filter.isMatch(entityMap));
	// 	or
	filter = MoqlEngine.createFilter("num > 150 or bean.num = 100");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("num > 150 or bean.num <> 100");
	System.out.println(filter.isMatch(entityMap));
	// 	not
	filter = MoqlEngine.createFilter("not bean.num <> 100");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("not num > 150 or bean.num = 100");
	//	paren
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("not (num > 150 or bean.num = 100)");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.num < 100 and num > 100 or bean.num = 100");
	System.out.println(filter.isMatch(entityMap));
	filter = MoqlEngine.createFilter("bean.num < 100 and (num > 100 or bean.num = 100)");
	System.out.println(filter.isMatch(entityMap));
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	The execution output of the above two pieces of code is very simple and will not be described in detail here. The definition of the Bean object in the example can be obtained by downloading the MOQL jar package, or you can customize one at will.

# Filter(Selector)

​	Selector is the core function provided by MOQL. It is equivalent to the function of DQL (Data Query Language) in SQL (Structured Query Language), which is the query function described by what we usually call the Select keyword. It can perform query, statistics and collection (such as UNION) operations on object data in Java memory. It can be used to perform real-time statistics on continuously generated data. That is, the statistical conditions are set in advance, and then whenever data is generated, it is handed over to the Selector for statistical analysis. The Selector will accumulate statistics on these continuously generated data until the user takes out the statistical results to persist them, and resets the Selector to allow it to perform subsequent real-time data statistical analysis. This mode can be applied to applications with fixed analysis modes, which can effectively reduce the data volume of big data statistical analysis and base its statistical analysis on preliminary statistical analysis results. like:

```
//产生一个BeanA对象的链表，链表长度为100，对这100个BeanA对象进行统计分析
List<BeanA> beanAList = BeanFactory.createBeanAList(0,100);
DataSetMap dataSetMap = new DataSetMapImpl();
dataSetMap.putDataSet("BeanA", beanAList);
String sql = "select count(a.id) cnt, sum(a.num) sum, a.num%500 mod from BeanA a group by 3 having mod > 10 order by 1";
try {
	Selector selector = MoqlEngine.createSelector(sql);
	//对第一组数据进行统计分析
	selector.select(dataSetMap);
	RecordSet recordSet = selector.getRecordSet();
	outputRecordSet(recordSet);
	//累计对第二组数据进行统计分析
	selector.select(dataSetMap);
	recordSet = selector.getRecordSet();
	outputRecordSet(recordSet);
	//重置selector，以前的统计结果被清空
	selector.reset();
	//对第三组数据进行统计分析
	selector.select(dataSetMap);
	recordSet = selector.getRecordSet();
	outputRecordSet(recordSet);
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	The syntax structure supported by Selector is similar to SQL, but unlike SQL, Selector is case-sensitive, and all keywords in its syntax are lowercase. The syntax structure is as follows:

```
select [cache(N[,fifo|filo|lru|lfu])] [all|distinct]] SELECT_LIST 
from TABLE_LIST
[[inner|left|right|full] join] TABLE [[as] ALIAS]
[on SEARCH_CONDITION] [...]]
[where SEARCH_CONDITION]
[group by GROUP_BY_EXPRESSION]
[having SEARCH_CONDITION]
[union [all]|except|symexcept|complementation|intersect [all] QUERY_EXPRESSION]
[order by ORDER_ITEM [asc | desc] [, ...]]
[limit [OFFSET,] N[%]]
[decorate by DECORATE_FUNCTION[, ...]]
```

​	The blue font in the syntax structure is the Selector keyword; the part enclosed by '[]' is the optional syntax part; the part in uppercase italics is the variable filling part, some of which are also included in SEARCH_CONDITION The Selector keyword will be explained in detail in the Selector syntax clauses below.

## SELECT clause

​	Defines the fields, constants and expressions that need to be returned in query results. The syntax is as follows:

```
select [cache(N[,fifo|filo|lru|lfu])] [all|distinct]] COLUMN [[as] ALIAS] [,...] from . . .
```

**cache(\*N\*[,fifo|filo|lru|lfu])**

​	The size of the memory buffer area available for query operations. N is a positive integer, indicating the number of records that can be cached. The size of the cache is set based on the number of records that can be cached. This syntax is different from traditional SQL syntax because MOQL is a syntax based on memory operations and statistical analysis, and all data is cached in memory. If the data that needs statistical analysis is real-time, continuously generated data, if the cache is not set, the statistics will most likely use up all the memory. Therefore, the memory available for the query must be limited according to the actual application. However, limitation will also bring about a problem, that is, it will bring about statistical errors in the data, but this error is acceptable compared to the real-time statistical effects it can bring and the reduction in data persistence space. fifo (first in, first out), filo (first in, last out), lru (least recently used) and lfu (least frequently used) are the elimination algorithms for cache data when the cache is full. The data space generated after elimination is Put in new data. fifo is a first-in-first-out algorithm, that is, the data stored in the cache area first will be eliminated first and the cache space is released when the cache area is full; filo is a first-in, first-out algorithm, that is, the data entered into the cache area first will be eliminated last; lru is the least recent Use an algorithm that eliminates the least recently used data, that is, the least active data. When doing top ranking queries, the least active data often cannot be ranked at the top of the data; lfu is the least frequently used algorithm, that is, the data that has been accessed the least frequently. When performing statistical queries, a low number of visits often means that the statistical value results are relatively small. When doing data ranking, it often means that the value will not be ranked first. Eliminating this data will not have much impact on the final results. like:

```
select cache(10000) a.id id, sum(a.num) sum from DataStream a group by a.id order by sum limit 100
```

​	DataStream is a data stream that continuously generates data, and data is always generated regularly. We need to count the data with the same ID and the top 100 of the data it generates. Since DataStream always generates data, we have no way to cache all the data it generates, and it makes little sense to persist this data. Then we use Selector to regularly calculate statistical values on the DataStream. Whenever a calculation is completed, the data generated by the DataStream can be discarded to free up space. Since the ID value of the data generated by DataStream is theoretically infinite, even if the space occupied by the original data in DataStream is released, if the result space of Selector is not limited, it may still cause memory overflow. Therefore, the cache value must be set to limit the maximum space that the Selector result can occupy. And since we are selecting the top 100 in this example, in order to make the statistical value less error-prone, we set its value to 10000, that is, on average, we will obtain a valid result among 100 values. This is caused by the cache being full and the data being eliminated. Data errors will appear insignificant.

​	By default, the cache size is 100 and the buffer elimination algorithm is fifo. When the cache value is set to -1, it means that there is no limit on the size of the cache area available for queries. However, you need to be careful when using this value. An inappropriate query may cause the memory of the entire virtual machine to overflow, thus affecting the overall application of the system.

**[all|distinct]**

​	Indicates whether to return all the query result data or return the data in the query result after deduplication. When deduplicating data, it will be judged whether multiple pieces of data are completely consistent. If multiple pieces of data are completely consistent, only one piece of data will be returned. The default is all, returning all data. And distinct means that only results without duplicate data are returned.

***COLUMN\* [[as] \*ALIAS\*][,...]**

​	Indicates the result data column to be returned by the query. COLUMN is actually an Operand (see: Operand (Operand) for details), which can be a constant, field name, function, expression or a column filter operand (ie, a subquery in SQL). ALIAS is the alias of COLUMN, which makes it easier to remember COLUMN. You can choose to use or not use the "as" keyword when making an alias. The group by or order by clause can reference the data column through COLUMN, ALIAS or the index of the data column's location. The index value starts counting from 1. For details, see the group by clause and order by clause.

## FROM clause

​	Describes which data tables the query obtains data from. The syntax is as follows: **

```
from TABLE [[as] ALIAS] [,...]
[[inner|left|right|full] join] TABLE [[as] ALIAS]
[on SEARCH_CONDITION] [...]]
```

***TABLE\* [[as] \*ALIAS\*][,...]****

​	TABLE in MOQL does not refer to a real persistent data table, it refers to a collection object placed in memory. This collection object can be a java.util.Map object, a system array object, all objects that inherit the java.util.Iterator interface, a database query result java.sql.ResultSet object, or an org.moql.RecordSet object returned by MOQL's own query. and a subquery. Each object element in these collection objects is viewed by MOQL as a record. The query result output columns, conditions, grouping and sorting defined in the MOQL query are all set for these object elements. In addition, this clause is consistent with the From clause of SQL. It supports setting an easy-to-remember alias for TABLE, and the information of TABLE* can be accessed using this alias in subsequent MOQL writing. like:

```
List<BeanA> beanAList = BeanFactory.createBeanAList(0,100);
DataSetMap dataSetMap = new DataSetMapImpl();
dataSetMap.putDataSet("BeanA", beanAList);
String sql = "select a.id, a.name, a.num%500 from BeanA a where a.num%500 > 10 order by 3";
try {
	Selector selector = MoqlEngine.createSelector(sql);
	selector.select(dataSetMap);
	RecordSet recordSet = selector.getRecordSet();
	outputRecordSet(recordSet);
} catch (MoqlException e) {
	e.printStackTrace();
}
```

​	The alias of the linked list BeanA in the above example is a, and a.id, a.name, etc. are all accessed to the linked list BeanA element object by using aliases.

​	When MOQL needs to query data from multiple TABLEs at the same time, its syntax is consistent with SQL. Multiple tables are placed after the from keyword and separated by ",", and there is no limit to the number of TABLEs. like:

```
select a.id, a.name, a.num%500, b.id, b.name, b.num%500 from BeanA a, BeanB b where a.num%500 > 10 or b.num%500 < 400 order by 3 desc, 6 desc
```

**[[inner|left|right|full] join] \*TABLE\* [[as] \*ALIAS\*][,...]**

​	Describes the data connection method between tables when querying multiple tables. By default, MOQL supports four table connection methods: inner join, left join, right join, and full join. When join does not add any prefix, it means inner join (inner join). There is no limit to the number of connections in the same MOQL statement. As for the data connection effects of inner joins, left joins, right joins, and full joins, MOQL is completely consistent with SQL. I will not go into details here. You can refer to SQL-related information to understand.

**[on \*SEARCH_CONDITION\*]**

​	Describes the connection conditions during data connection. SEARCH_CONDITION is a specific connection condition. For detailed description, see Filter.

​	Examples of MOQL table connection queries are as follows:

```
select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num from BeanA a left join BeanB b on a.id = b.id right join BeanC c on c.id = b.id order by 3 desc
```

## WHERE clause

```
[where SEARCH_CONDITION]
```

​	SEARCH_CONDITION is a specific query condition. For detailed description, see Filter.

## GROUP BY clause

​	Describe which fields are used to group the query. When using this clause, consistent with SQL, all fields described in the SELECT clause that are not aggregated must be included in this clause.

```
[group by COLUMN [, ...]]
```

***COLUMN***

​	COLUMN can be a column that appears in the SELECT clause, an alias of a column, or the index position of the column. The index position starts counting from 1. A GROUP BY clause can include one or more COLUMN. like:

```
select count(a.id) cnt, sum(a.num) sum, a.num%500 mod, a.id from BeanA a group by 3, a.id
```

## HAVING clause

 	Consistent with SQL syntax, it describes the filter conditions for filtering group data generated in the group by clause. All fields in the having clause need to be columns in the select clause. Unlike where, here is the filtering of the grouped result data, while where is performed on the original table data.

```
[having SEARCH_CONDITION]
```

​	SEARCH_CONDITION is a specific query condition. For its detailed description, see Filter.

## set operation clause

​	Set operations describe operations between two or more query results. Operations include: union, except, symexcept, complementation, intersection, etc. Its grammatical structure is as follows:

```
[union [all]|except|symexcept|complementation|intersect [all] QUERY_EXPRESSION]
```

​	For two result sets A and B that perform set operations, their set operation formula is: "A set operator B". The number and order of fields of two result sets A and B may be inconsistent, but it must be ensured that the field definitions of set A are included in the field definitions of set B, otherwise the two result sets cannot perform normal set operations. During operation, the set operation is performed based on the left operand, that is, the fields and field order of the A result set.

**union [all]**

​	Indicates that the two query results are merged together. [all] means that when merging sets, the same records in the two sets will not be removed. By default, if there are multiple identical records in two collections, only one will be retained. The mathematical formula of the union is: A ∪ B.

**except**

 	Indicates a difference set operation between two query results, that is, subtracting the second result set from the first result set, and removing the records that are exactly the same as the second result set in the first result set. The remaining records are the differences. Set results. For example: the mathematical formula of the difference between two sets A and B is: A – B.

**symexcept**

​	The result of the symmetric difference set is that after the difference sets of the two query results are calculated, the union of the two difference sets is calculated. Its mathematical formula is: A – B ∪ B – A.

**complementation**

 	Means first finding the union of the two result sets, and then finding the complement of the left operand result set. For example: A complementation B, its corresponding complement mathematical formula is (A ∪B) – A.

**intersect [all]**

​	Means finding the intersection of two result sets. [all] means that when the sets intersect, the same records that hit multiple intersections between the two result sets in the intersection will not be removed. By default, if there are multiple identical records in the intersection, only one will be retained. The mathematical formula of intersection is: A ∩ B.

## ORDER BY clause

 	Consistent with SQL syntax, it describes how to sort query results, by which field or fields to sort; in ascending order, or in descending order, etc. There can be only one order by clause in a Selector definition, but there can be multiple select clauses.

```
[order by ORDER_ITEM [asc | desc] [, ...]]
```

***ORDER_ITEM [asc | desc]***

​	 Indicates the name of the field to be sorted. This field can be a column in the select clause or its alias; it can also be the index value of the column, and the index starts counting from 1; it can also be the field name of any table (collection object) in the from clause, such as: table1 .name, table1 is the alias of the collection object, and name is a field of the collection object. The fields used for sorting do not necessarily have to be columns included in the select clause. [asc | desc] indicates the sorting method, asc indicates that the data in the field is sorted in ascending order; desc indicates that the data in the field is sorted in descending order. When sorting, you can specify multiple fields and sorting methods at the same time. When executed, the sort job sorts each field in sequence from left to right. The meaning is that the result set is first sorted by field 1; then based on this, the records with the same data field 1 are sorted by field 2...

## LIMIT clause

```
[limit [OFFSET,] N[%]]
```

​	Indicates that starting from the specified offset, return the first N records or the first N% of the query results. OFFSET indicates where to start fetching data from the result set, and N indicates the number of data fetched. Limit can be used for paging queries. OFFSET and N are positive integers, and their values are related to the size of the cache setting. OFFSET and N should be less than or equal to the size of the cache. When OFFSET is 0 and N is greater than the cache size, it means returning all results. In addition, when it represents a percentage, if N is greater than 100, it also means returning all result data.

## DECORATE BY子句

```
[decorate by DECORATE_FUNCTION[, ...]]
```

​	Indicates that the query result set processed by other clauses is decorated and modified according to the specified method. DECORATE_FUNCTION represents a decoration function that can modify the result set object. This clause can support multiple decorative functions, and the decorative functions are separated by ",". The decoration functions are executed in order from left to right to decorate the result set. For example: groupRowNumber(groupFields, valueField) function, its meaning is the same as Oracle's ROW_NUMBER_OVER function, which means to number the records of the groupFields field set with the same value. The numbers of records with the same groupFields value in each group increase from 1, and the numbers of records in different groups increase independently of each other. The number value will finally be put into the field described by valueField (see the testGroupRowNumberDecorate method of the TestSelector class in the source code package). DECORATE_FUNCTION can be expanded by writing functions (see "MOQL—Function" for details on the expansion method). Users can expand the modification logic of the result set as needed. In principle, decorating functions cannot add or delete columns of the result set, but can only modify the columns of the result set.

 	Each clause of the Selector has related code samples. They are in example/TestSelector.java of the jar package. I will not list them one by one here. Interested friends can download the latest jar package in the following project path.

# Translator

​	MOQL is a query statistical analysis tool designed for memory objects, and its syntax is compatible with SQL standards. Its syntax structure supports the following:

```
select count(a.id) cnt, sum(a.num) sum, a.num%500 mod from BeanA a group by 3 having mod > 10 order by 1
```

​	In addition to the standard SQL writing format, it also has an xml structure writing format, as follows:

```
<selector id="40daef74-ba85-465b-b051-9f41ad00a526">
    <cache size="100"/>
    <columns>
      <column name="cnt" value="count(a.id)"/>
      <column name="sum" value="sum(a.num)"/>
      <column name="mod" value="a.num%500"/>
    </columns>
    <tables>
      <table name="a" value="BeanA"/>
    </tables>
    <groupby>
      <group column="3"/>
    </groupby>
    <having>
      <binary loperand="mod" operator="&gt;" roperand="10"/>
    </having>
    <orderby>
      <order column="1" mode="ASC"/>
    </orderby>
  </selector>
```

​	The above two syntax formats are interchangeable. When MOQL uses the xml format, the syntax format of the xml format can be converted into different SQL dialects as needed. In addition to MOQL syntax, it also supports Oracle, SQL Server, DB2, MySQL, PostgreSQL and other dialects.

​	MOQL's xml syntax format can be regarded as the syntax tree structure of sql statements. It has a clear structure and is easier to write and modify through programs. Especially convenient for front-end and back-end interaction. When an application system needs to provide a relatively complete and open query function, it can edit the query statement in XML format and then send it to the backend; the backend converts it into the corresponding SQL dialect according to the type of the database, and then queries the database and Finally returns the result set. This model can provide users with flexible and open query functions on the front end, and shield database differences on the back end to facilitate migration.

 	The following is a demo code that demonstrates the conversion from standard sql format to xml format, and the conversion of xml format to sql format in various languages.

```
String sql = "select count(a.id) cnt, sum(a.num) sum, a.num%500 mod from BeanA a group by 3 having mod > 10 order by 1 limit 10,3";
try {	
	String xml = MoqlTranslator.translateMoql2Xml(sql);
	System.out.println(xml);
	sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.MOQL);
	System.out.println(sql);
	sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.ORACLE);
	System.out.println(sql);
	sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.SQLSERVER);
	System.out.println(sql);
	sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.MYSQL);
	System.out.println(sql);
	sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.POSTGRESQL);
	System.out.println(sql);
	sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.DB2);
	System.out.println(sql);
} catch (MoqlException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
```

​	The output is as follows:

```
<?xml version="1.0" encoding="UTF-8"?>
<selectors xmlns="http://www.moql.org/schema/moql" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.moql.org/schema/moql selector-base.xsd">
  <selector id="b6463fb5-298e-461f-8f60-e9da8f5da80f">
    <cache size="100"/>
…	
    <limit offset="10" value="3"/>
  </selector>
</selectors>
	

select cache(100,fifo) count(a.id), sum(a.num), a.num % 500 from BeanA a group by 3 having mod > 10 order by 1 asc limit 10,3 
select count(a.id), sum(a.num), a.num % 500 from BeanA a where rownum <= 3 group by 3 having mod > 10 order by 1 asc 
select top 3 count(a.id), sum(a.num), a.num % 500 from BeanA a group by 3 having mod > 10 order by 1 asc 
select count(a.id), sum(a.num), a.num % 500 from BeanA a group by 3 having mod > 10 order by 1 asc limit 10,3 
select count(a.id), sum(a.num), a.num % 500 from BeanA a group by 3 having mod > 10 order by 1 asc limit 10,3 
select count(a.id), sum(a.num), a.num % 500 from BeanA a group by 3 having mod > 10 order by 1 asc fetch first 3 rows only 
```

​	From the xml format output by the above demonstration code, we can find that its format is relatively complex and can contain more than one selector tag (note: each selector tag represents a sql statement). This is related to the original design intention of MOQL and does not affect its use, so you don't need to worry about it. In addition, it should be noted that when there are aggregation operations in the SQL statement, such as union, intersect, etc., the label of the XML structure is not the selector, but the setlector. The setlector will contain two selector labels, indicating two Perform aggregation operations on a collection.

# 附录

## 函数

### aggregate function

| Function name                                                | Function description                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| avg(String column)                                           | Find the average of all values in a given column  parameter:  column: The name of the column for averaging. The data type of the column must be numeric. |
| count(String column[, boolean distinct])                     | Find the number of all values in a given column  parameter:  column: the name of the column to be counted  distinct: Boolean optional parameter, indicating whether to count only different values, true: indicating only counting records with different values, false: indicating counting all records, the default is false |
| joint(String column[, String separator])                     | String concatenation operation, concatenates all the values of the given column into a string using the given delimiter.  parameter:  column: the name of the column to be joined by strings  separator: separator used to connect strings |
| kurtosis(String column)                                      | Find the kurtosis value of a given column  parameter:  Column: The name of the column for finding kurtosis. The data type of the column must be numeric. |
| median(String column)                                        | Find the median of a given column. If there are an odd number of values, return the middle value; if there are an even number, return the average of the two middle numbers.  parameter:  column: The name of the column to find the median. The data type of the column must be numeric. |
| max(String column)                                           | Find the maximum value among all values in a given column  parameter:  column: the name of the column with the maximum value |
| min(String column)                                           | Find the minimum value among all values in a given column  parameter:  column: the name of the column to find the minimum value |
| mode(String column)                                          | Finds the mode of a given column, returning an array of modes  parameter:  column: the name of the column to find the mode |
| notNull(String column[, String defaultVale[,boolean first]]) | Find the non-null value of a given column. If the column has no non-null value, a default value can be set for it. By default, this function will get the first non-null value of the column. If you want to get the last non-null value, you can set the optional parameter to false  parameter:  column: the name of the column to obtain non-null values  defaultValue: the default value when the column has no non-null value  first: whether to take the first non-null value |
| percentile(String column[, double percentile])               | Finds the numerical value of the specified percentile for the given column.  parameter:  column: The name of the column to be calculated as a percentage. The data type of the column must be numeric.  percentile: percentage value, the value is between (0,1], the default is 0.5 |
| range(String column)                                         | Find the range of a given column  parameter:  column: Find the name of the column with the extreme difference. The data type of the column must be numeric. |
| semiVariance(String column)                                  | Find the semivariance of a given column  parameter:  Column: The name of the column to find the semivariance. The data type of the column must be numeric. |
| skewness(String column)                                      | Find the skewness value of a given column  parameter:  column: the name of the column for which skewness is to be found. The data type of the column must be numeric. |
| standardDeviation(String column)                             | Find the standard deviation of a given column  parameter:  column: The name of the column for which the standard deviation is found. The data type of the column must be numeric. |
| sum(String column)                                           | Find the sum of all values in a given column  parameter:  column: The name of the column to be summed. The data type of the column must be numeric. |
| variance(String column)                                      | Find the variance of a given column  parameter:  column: the name of the column for which the variance is to be calculated. The data type of the column must be numeric. |
|                                                              |                                                              |

### mathematical calculation function

| Function name                         | Function description                                         |
| ------------------------------------- | ------------------------------------------------------------ |
| abs(String field)                     | Take absolute value  parameter:  field: The name of the field that takes the absolute value. The data type of the column must be numeric. |
| cbrt(String field)                    | Find the cube root  parameter:  field: The name of the field to be cubed. The data type of the column must be numeric. |
| ceil(String field)                    | Rounded up  parameter:  field: The name of the field to be rounded. The data type of the column must be numeric. |
| cos(String field)                     | Find the cosine of the specified field  parameter:  field: Find the name of the field of cos. The data type of the column must be numeric. |
| exp(String field)                     | Exponential power with natural number e as the base  parameter:  field: the name of the field, the data type of the column must be numeric |
| floor(String field)                   | Round down  parameter:  field: The name of the field to be rounded. The data type of the column must be numeric. |
| log(String field)                     | Taking the natural number e as the base, find the logarithm  parameter:  field: the name of the field, the data type of the column must be numeric |
| log10(String field)                   | Using base 10, find the logarithm  parameter:  field: the name of the field, the data type of the column must be numeric |
| pow(String field, double power)       | Find the exponent  parameter:  field: the name of the field, the data type of the column must be numeric  power:index |
| precent(String field[,int precision]) | Convert the value to a percentage, for example: 0.23 is converted to 23%  parameter:  field: The name of the field to be converted. The data type of the column must be numeric.  precision: precision, the precision of decimals retained when converting to a percentage. The default is 0, which means no decimal places are left in the percentage. |
| round(String field)                   | Rounding  parameter:  field: The name of the field to be rounded. The data type of the column must be numeric. |
| sin(String field)                     | Find sine  parameter:  field: the name of the field, the data type of the column must be numeric |
| sqrt(String field)                    | Find the square root  parameter:  field: the name of the field, the data type of the column must be numeric |
| tan(String field)                     | Find tangent  parameter:  field: the name of the field, the data type of the column must be numeric |
|                                       |                                                              |

### Other functions

| Function name                     | Function description                                         |
| --------------------------------- | ------------------------------------------------------------ |
| regex(String field, String regex) | Perform regular matching on the specified field, returning true if the match is successful and false if the match fails.  parameter:  field: the name of the column to be matched regularly  regex: regular expression |
| trunc(String field,int precision) | Floating point formatting operations  parameter:  field: The name of the field to be formatted. The data type of the column must be numeric.  Precision: Precision, the precision to be formatted for floating point numbers. When the numerical precision is not enough, 0 is added. For example: floating point number 3.23, when the precision is 3, the output is 3.230 |
|                                   |                                                              |
