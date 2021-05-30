(注：moql-translator,moql-querier以转移到moql-transx工程)

# MOQL简介

​	MOQL(MemoryObject Query Language)是一款基于Java的面向内存对象过滤、查询及统计分析的开源工具。它能够对内存中存储的集合对象，集合中的对象可以是Bean对象，数组对象、Map对象等各种对象，完成类似于数据库提供的查询及统计分析功能。它的语法结构类似于SQL，支持distinct、where、group、having、order、limit、聚集运算(count、sum、avg、min、max)、多表查询、连接查询(left、right、inner、full)、嵌套查询、以及集合运算(union、intersect、except)等。如：

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

更多内容详见[选择器(Selector)](#选择器(Selector))

​	MOQL与内存数据库(关系型)的关注点不同。内存数据库关注OLTP(Online TransactionProcessing)，保证数据的ACID特性，并以此为基础为使用者提供查询及统计分析功能。当我们使用内存数据库的查询或统计分析功能时，首先需要将数据插入到数据库的相关表中，这种操作往往需要一个O-R转换过程，即将对象转换为表记录。而后，根据表结构使用SQL语句对数据进行统计分析。而MOQL不关注数据的存储形式，也不对数据的存储形式作任何约定。它的目标是为使用者装载入内存中的任何对象提供直接的查询及统计分析的功能，从而简化开发者的编程复杂度，为开发者提供更多的开发选择。它语法设计时的目标就是“对象”，有别于内存数据库中的面向“记录”。这点对于习惯了按表和记录方式思维并编写SQL的使用者来说，需要稍微适应一下。而相较SQL而言，MOQL在以Java为基础的面向对象的开发过程中更“面向对象”。

​	性能方面，从实现角度看MOQL与内存数据库相比会存在一定的差距。MOQL是面向对象查询的，很多数据类型是在运行时动态绑定的，这会影响数据的计算和比较效率；而内存数据库中的数据表结构是预先定义的，数据的数据类型明确，查询时可以更快的进行计算和比较。但这并不意味着MOQL在性能要求较高的场合完全派不上用场。当一个应用有固定的实时查询统计分析要求，且其数据是不断的累积增长的，那么完全可以用MOQL来解决。不断增长的数据可以向流水一样不断地流过MOQL提供的Selector(详见《MOQL-选择器(Selector)》)，Selector里定义了查询统计分析的要求，所有流过Selector的数据都会被实时的统计分析，统计分析结果可以累计，且可随时读取。所有数据在未进行持久化之前就可以完成统计分析，在应用效果上比内存数据库来的简单、容易。

​	MOQL与.NET中提供的LINQ(语言集成查询)功能相似，都可以对内存对象进行统计分析，但LINQ提供了太多的接口，语法规则也变化比较多。不同的语言如C#和VB在使用LINQ时限制也不同，掌握起来难度较大。而MOQL在语法上更接近SQL，对于有SQL基础的编程者来说，学习曲线低，更容易掌握。

​	MOQL工具提供了Selector(选择器)、Filter(过滤器)以及Operand(操作数)三种主要的应用。Selector提供了对内存数据对象的查询及统计分析功能，更多内容详见[选择器(Selector)](#选择器(Selector))；Filter提供了对内存数据对象的过滤功能，其表达式支持“=”、“>”、“<”、“>=”、“<=”、“<>”、“between”、“like”、“in”及“is”等关系运算符以及“and”、“or”、“not”等逻辑运算符，如：

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

​	更多内容详见[过滤器(Filter)](过滤器(Filter))；Operand提供了表达式解析及计算功能，其支持表达式类型有：常量(整数、浮点数、字符串)、变量(普通变量、成员变量)、函数(普通函数、成员函数)、数组(系统数组、链表、Map等)、运算表达式(“+”、“-”、“*”、“/”、“%”、“&”、“|”、“^”)等。如：

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

更多内容详见[操作数(Operand)]()

# 操作数(Operand)

​	Operand是MOQL语法结构的重要组成部分，语法结构中那些需要被分析处理的数据列或数据值都被称之为Operand。如select后跟的数据列，where条件中描述的条件字段以及需要匹配的常数值等都被称之为Operand。如下面语句中的红色字体部分，均表示是一个Operand。通过对其计算，我们可以获得数据结果，并形成最终我们求解的数据结果集。

```
select count(a.id) cnt, sum(a.num) sum, a.num%500 mod from BeanA a group by 3 having mod > 10 order by 1
```

​	在MOQL中，Operand不仅能在完整的语法结构中使用，还可以单独使用。MOQL提供了方法可以直接创建一个操作数，并利用此操作数完成数据的求解。如下：

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

​	该例中为表达式"(num * num1) / num2 * 2.2 + 2 - 1"创建了一个操作数，该操作数利用传入的实体Map对表达式进行了求解，并输出了执行结果，结果为20.8。MoqlUtils的createOperand方法可以创建一个操作数，传给该方法的参数为一个操作数表达式字符串，返回的对象为一个Operand接口，该接口位于org.moql的包路径下，定义如下：

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

​	getOperandType()方法用于返回Operand的类型OperandType。OperandType为枚举类型，也位于org.moql的包路径下，定义格式如下：

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

​	其包括的Operand类型有常量(CONSTANT)、变量(VARIABLE)、函数(FUNCTION)、表达式(EXPRESSION)及列筛选器(COLUMNSELECTOR)五类(注:Operand的类型将在后面详细介绍)，当Operand不属于以上任何一类时用UNKNOWN未知表示。

​	getName()方法用于获得Operand的名字。除了函数类型的Operand的名字为函数名外，其它类型的Operand的名字就是生成Operand的字符串本身。如：“sum(a.num)”整体被解析为一个函数Operand，其名字为“sum”，其内嵌了一个由“a.num”解析而成的表达式Operand作为参数，该Operand名字为“a.num”；再如：“123”被解析为常量Operand，其名字为“123”等。

​	getSource()方法用于返回操作数在文本串中的位置。如：“sum(a.num)”会被解析为多个操作数，对每个操作数调用该方法就可以定位操作数在文本串中的位置了。

​	operate(EntityMapentityMap)方法是Operand提供的主要方法，利用该方法操作数可以对给定的参数进行求值。EntityMap是一个装有实体对象的Map，其实体名若与变量Operand的名字一致，那么实体对象便会被变量Operand绑定用来求解。

​	booleanOperate(EntityMapentityMap)方法是operate(EntityMap entityMap)方法的扩展。用于将Operand的计算结果以布尔值的形式返回。若operate方法返回值的类型是java.lang.Boolean，则该方法返回调用Boolean.booleanValue()所得的值；若operate方法返回值的类型不是java.lang.Boolean，返回值为null时，返回false，否则返回true；

​	isConstantReturn()方法用于告诉调用者在调用Operand的operate方法后是否总能返回一个不变得常量值。若该Operand是一个常量类型的Operand，则该调用总是返回true，表示调用常量类型Operand的operate方法总是返回同一个常量值。而对于该方法更有意义的用途是在函数类型Operand以及表达式类型Operand中的调用。这两种类型的Operand在初始化时会判断所有其相关的Operand的该方法调用，返回是否都是true。若都是true，则表示该操作数的执行结果与operate调用时传入的EntityMap参数无关，那么就可以预先对该操作数进行求解，而不用等到每次调用operate方法时现去求解了。这样可以在一定程度上提升Operand的执行效率。若对该方法的调用感兴趣，可以查看源代码中org.datayoo.moql.operand.function.AbstractFunction类以及org.datayoo.moql.operand.expression.arithmetic.AbstractArithmeticExpression类的相关初始化实现。

​	reset()方法用于重置Operand，将Operand的状态恢复为初始状态。Operand在计算时分为有状态和无状态两种情况。有状态是指当对Operand的operate方法进行多次调用时，其计算结果受以前调用结果的影响；而无状态是指多次调用不会受以前调用结果的影响。对于有状态的Operand需要通过调用reset方法将其状态恢复为初始状态。如：“sum(a.num)”对应的Operand，会累计每次调用operate方法的结果，对每次调用的值求和。若需要重新开始计数，需要调用reset方法将该Operand恢复为初始值。类似的Operand还有count、avg、max、min等聚合函数。

## 常量Operand

​	Operand支持的常量类型包括字符串型(String)、双浮点型(Double)、长整型(Long)以及NULL类型。

​	字符串Operand的格式与SQL中字符串的格式一致。字符串的两端需要用单引号(‘)包围，如：’String’、’字符串’、’123’、’123.1’、’null’等都表示字符串。当字符串中也需要包含单引号(‘)时，也完全遵从SQL字符串的约束，用两个相连的单引号(‘’)来表示一个字符串中的单引号如：’Str’’ing’其代表的字符串为”Str’ing”。

​	双浮点Operand可以表示的数据范围与Java语言中的java.lang.Double对象可以表示的数据范围一致。它可以覆盖浮点型(float)数据的数据范围。MOQL中所有浮点数都以双浮点Operand表示，如：123.4、-12.78、23.3e15等。

​	长整型Operand表示的数据范围与Java语言中的java.lang.Long对象所表示的数据范围一致。在MOQL中SQL语法支持的byte、short、integer以及long类型都以长整型Operand表示。另外长整型Operand还支持八进制、十进制以及十六进制的数据格式，如：19、3435L、0xAFCD、-1235、072等。

​	NULL类型Operand用来表示null。其只有null这一种表现形式，所有字母必须是纯小写。

​	常量Operand的代码示例如下：

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

​	其执行结果输出如下：

```
1234 CONSTANT
192.16 CONSTANT
'中国''china' CONSTANT
NULL CONSTANT
Null VARIABLE
```

​	字符串Null未被解析为NULL类型的常量Operand，而是被解析为了变量类型的Operand。

## 变量Operand

​	变量类型Operand的命名规范与Java语法中变量的标示符的命名规范一致。其标示符通常是以大小写英文字母、数字、下划线“_”和美元符号“$”组成，其中数字不能成为标示符的首字母。另外，标示符的命名也支持汉语的文字或其它语言的语言的文字，如： _data、a1、$a、长度、num等。

​	变量Operand的示例代码如下：

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

​	其执行结果输出如下：

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

## 函数Operand

​	函数Operand的格式如下：函数名(参数1,参数2…)。函数名与Java语法中标示符的命名规范一致，该命名规范已在变量Operand中进行了描述。其每一个参数都是一个Operand，Operand可以是常量、变量、函数或者表达式中的任意一种。如：sum(a)、_formatTime(‘yyyy-mm-dd’，getCurrentTime())、random(23+15)等。

​	函数Operand的示例代码如下：

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

​	其执行结果输出如下：

```
max(num) FUNCTION
123
345
12
test(1,num,'a') FUNCTION
```

#### 自定义函数

​	MOQL目前已支持了部分的函数Operand，如聚集函数Operand：count、sum、avg、min、以及max等(详见**附录:函数**部分)。这些Operand在生成后调用operate方法就可以直接对数据进行计算。但如果要创建一个MOQL未支持的函数，MOQL会为其创建一个缺省的Operand，该Operand只是对函数字符串进行了解析，如上面的代码片段中的函数“test(1, num, 'a')”。该函数字符串被解析为一个缺省的函数Operand，但是当我们调用它的operate方法时，它会抛出一个java.lang.UnsupportedOperationException，表示该Operand不支持该方法。为了能够让MOQL也支持test函数，我们需要为test做一个对应的Operand的实现。相关的实现代码如下：

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

​	其执行结果输出如下：

```
test(1,num,'a') FUNCTION
java.lang.UnsupportedOperationException
	at org.moql.operand.function.MemberFunction.innerOperate(MemberFunction.java:49)
	at org.moql.operand.function.AbstractFunction.operate(AbstractFunction.java:120)
	at org.moql.core.test.TestRegistFunction.main(TestRegistFunction.java:44)
test(1,num,'a') FUNCTION
1||123||a
```

​	Test类继承了AbstractFunction抽象类，它是test函数的Operand实现。test函数可以接收3个参数，当参数的个数与3不一致时，其构造函数将抛出异常，表示test函数的调用格式不正确，如：test(1,num)将会抛出异常。当我们定义一个函数其参数为变长时，我们可以将3替换为-1，表示变长。或者我们用org.datayoo.moql.operand.function.Function中的*VARIANT_PARAMETERS*常量来表示，该值为-1。Test类的主体逻辑在innerOperate中实现，Test类覆写了该方法，将三个参数简单的连接成一个字符串并返回。

​	从上例代码的执行情况我们看到，在第一次创建test函数的Operand后，调用operate方法抛出了异常；于是在第二次创建test函数的Operand前，我们调用了MOQL的函数注册方法，为test函数注册了Operand的实现类Test。而后我们创建了test函数的Operand并调用了它的operate方法。此次调用获得了预期的输出效果。其根本原因是，第一次创建的函数Operand，因未找到注册的Operand实现，生成了一个缺省的函数Operand，该Operand不支持operate方法；而第二次我们创建函数Operand时，发现了注册的实现类Test，于是绑定了该实现，生成了test函数对应的Operand。

## 表达式Operand

​	表达式Operand是MOQL语法中格式最丰富且复杂的一类、。它包括数组表达式、数学运算表达式以及成员表达式三种类型。另外，在MOQL源代码中我们还可以看到，表达式Operand至少还包括关系运算表达式以及逻辑运算表达式。但这两种表达式无法通过MoqlUtils.createOperand()直接创建，它们必须应用在过滤器(Filter)中。我们将在过滤器(Filter)中介绍以上两种表达式的应用。

### 数组表达式Operand

​	数组表达式Operand除了能对传统意义上的数组进行描述定义外，还可以对如：Map、List等集合进行定义描述。其基本的格式为：“数组对象[索引]”。数组对象可以是一个变量、数组表达式或成员表达式Operand；索引可以是一个常量、变量、函数或表达式Operand。

​           数组表达式Operand中的数组对象不仅仅包括Java语言中所有调用isArray()方法返回true的数组对象，还包括所有实现了java.lang.Iterable接口和java.util.Map接口的对象，以及java.sql.ResultSet对象和org.moql.RecordSet对象。ResultSet与RecordSet分别是JDBC查询和MOQL查询的结果对象。数组对象中除了实现了java.util.Map接口的对象外，其他所有对象都是以数值索引方式进行访问的，且都是以‘0’作为数组中第一个数据的索引。若索引不是以数值形式传入，而是以字符串形式传入的，系统会尝试将该字符串转换为一个数值索引后，再进行数组访问，如array[1]、array[‘1’]等。而实现了java.util.Map接口的对象，其索引可以为任意对象，该对象即这个Map对象的Key，如：map[‘key’]。若数组表达式的索引值没有填写，如：array[]，则除Map对象外表示要访问数组中的全部数据，而Map对象表示要访问其全部值数据，不包括Key数据。

​	数组表达式Operand的相关示例代码如下：

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

​	其执行结果如下：

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

### 算数运算表达式Operand

​	算数运算表达式Operand的格式如下：“Operand1 运算符 Operand2”。算数运算表达式支持的算数运算符都是二元运算符，他们按执行的优先顺序划分为：乘法(*)、除法(/)、模(%)优于加法(+)、减法(-)运算。运算符两端的两个操作数可以是除列筛选Operand以外的任意类型的Operand。若其中一个Operand是另一个数学运算表达式Operand，则整个表达式就是一个包含连续多个运算符操作的表达式，如：a + 1 * 10 / 5 等。

​	数学运算表达式Operand的相关示例代码如下:

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

​	其执行结果如下：

```
(num * num1) / num2 * 2.2 + 2 - 1 EXPRESSION
20.8
```

### 位运算表达式Operand

​	位运算表达式Operand的格式与算数运算表达式相同，如下：“Operand1 运算符 Operand2”。其运算优先级为：按位与(&)优于异或(^)优于按位或(|)运算，且所有的位运算优先级均低于算数运算符。

​	位运算表达式Operand的相关示例代码如下:

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

​	其执行结果如下：

```
num << num1 + 1 = 32
num2 | num1 & num = 6
~ (num2) ^ num2 = -1
```

### 成员表达式Operand

​	成员表达式Operand用于访问对象的成员属性或者成员函数。其格式如下：“对象.成员”。“.”是成员运算符，其左侧的对象可以是变量、函数、数组表达式或成员表达式Operand；其右侧的成员可以是变量或函数Operand。当成员是变量Operand时，“对象”必须拥有能够获得该变量的对应的Getter函数。当变量是布尔型时，函数名为变量加上”is”前缀；当变量是非布尔型时，函数名为变量加上”get”前缀。当成员是函数Operand时，“对象”必须拥有对应的函数，且函数的参数类型需要与函数Operand中描述的参数类型一致。

​	成员表达式Operand的相关示例代码如下：

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

其执行结果如下：

```
bean.name EXPRESSION
Abean
bean.getNum() EXPRESSION
5
```

### 列筛选Operand

​	列筛选Operand是一类特殊的Operand，该类Operand无法通过MoqlUtils的createOperand方法直接创建。它的格式为一个只返回一个字段的MOQL查询语句，如：select field1 from table where field2 < 10，该语句只返回一个字段field1。另外，该Operand会出现在MOQL语法的输出列位置，或是where条件的”in”操作符后。如：select <u>select field1from table where field2 < 10 as field1, field2 from table2</u>、select field2, field1 from table2 where field2 in (<u>select field1 from table</u>)等。

# 过滤器(Filter)

​	Filter是MOQL提供的主要功能之一，它支持SQL语法中Where部分的语法描述，能够通过编写条件语句对内存中的数据进行条件匹配。它可以用于对从数据库中查询回的数据集进行二次查询的功能；还能够用于数据处理时对数据流的实时过滤等。

​	Filter功能建立在Operand的基础上，Operand决定了能对哪些数据进行过滤。简单来说包括：常量、变量、数组、集合、对象等(详见文章MOQL—Operand(操作数))。这些数据必须封装在一个名为EntityMap的数据对象中，Filter就可以按照预定义的语法对数据进行匹配了。

​	Filter支持的运算符包括：关系运算符与逻辑运算符两大类。关系运算符包括：大于(>)、小于(<)、等于(=)、大于等于(>=)、小于等于(<=)、不等于(<>)、between..and、like、in、is等。逻辑运算符包括：逻辑与(and)、逻辑或(or)以及逻辑非(not)。关系运算符的运算优先级高于逻辑运算符，但所有关系运算符的优先级一致，没有差别。逻辑运算符的优先级中逻辑非(not)>逻辑与(and)>逻辑或 (or)。Filter也支持小括号”()”，可以通过小括号改变算符的执行顺序。

​	关系运算符的代码示例如下：

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

​	逻辑运算符的代码示例如下：

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

​	以上两段代码的执行输出结果非常简单，在此不作赘述。示例中的Bean对象的定义可以通过下载MOQL的jar包获得，也可以随意自定义一个。

# 筛选器(Selector)

​	Selector是MOQL提供的最核心的功能，它相当于SQL(结构化查询语言)中DQL(数据查询语言)的功能，即我们通常所说的Select关键字所描述的查询功能。它能够对Java内存中的对象数据进行查询、统计以及集合(如：UNION)操作。它可用于对持续不断产生的数据进行实时统计。即预先设定好统计条件，然后每当有数据产生就将其交给Selector进行统计分析。Selector会累计统计这些不断产生的数据，直到使用者取出统计结果将其持久化，并重置Selector让它进行后续的实时数据统计分析。这种模式可应用于分析模式固定的应用，可有效减少大数据统计分析的数据量，使其统计分析建立在初步统计分析结果的基础上。如：

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

​	Selector支持的语法结构与SQL相似，但与SQL不同的是，Selector是大小写敏感的，其语法中所有的关键字都是小写的。语法结构如下:

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

​	语法结构中的蓝色字体为Selector的关键字；用’[]’括起来的部分为可选的语法部分；大写斜体部分为可变填充部分，其中有些可变填充部分如***SEARCH_CONDITION***中也包括了Selector的关键字，将在下面Selector语法的子句中进行详细阐述。

## SELECT子句

​	定义了查询结果需要返回的字段、常量以及表达式。语法如下:

```
select [cache(N[,fifo|filo|lru|lfu])] [all|distinct]] COLUMN [[as] ALIAS] [,...] from . . .
```

**cache(*N*[,fifo|filo|lru|lfu])**

​	查询运算可用的内存缓存区的大小。N为正整数，表示可以缓存的记录的条数。缓存的大小以可以缓存的记录条数来设定。该语法与传统SQL语法不同，因为MOQL是一种基于内存运算与统计分析的语法，所有的数据都缓存在内存中。若需要统计分析的数据是一个实时的，不断产生的数据，若不对缓存进行设定，则该统计很有可能会耗尽所有的内存。因此必须根据实际应用对查询可用的内存加以限定。但限定也会带来一个问题，就是它会带来数据的统计误差，但该误差相对其能带来的实时统计效果以及数据持久化空间的减少是可以接受的。fifo(先入先出)、filo(先入后出)、lru(最近最少使用)以及lfu(最不经常使用)是当缓存区满时的缓存区数据淘的汰算法，淘汰后产生的数据空间以放入新的数据。fifo是先入先出算法，即先存入缓存区的数据，在缓存区满时被率先淘汰并释放缓存空间；filo是先入后出算法，即先入缓存区的数据最后被淘汰；lru是最近最少使用算法，即淘汰最近最少使用的数据，即最不活跃的数据。在做top排名查询时，最不活跃的数据往往也不能排列到数据的最前面；lfu是最不经常使用算法，即被访问次数最少的数据。统计查询时，访问次数少往往也意味着统计值结果比较小。做数据排名时，也往往意味着该值不会被排列到前面。淘汰这些数据对最终结果不会有太大影响。如：

```
select cache(10000) a.id id, sum(a.num) sum from DataStream a group by a.id order by sum limit 100
```

​	DataStream是一个不断产生数据的数据流，定期总有数据产生。我们需要统计它所产生的数据的相同id的数据和最大的前100名。由于DataStream总不断产生数据，我们没有办法缓存其产生的所有的数据，而持久化这些数据也意义不大。那么我们利用Selector定期对DataStream求统计值。每当计算完一次，就可以丢弃DataStream产生的数据，释放空间。由于DataStream产生的数据的id值理论上有无限大的可能，即使释放掉DataStream中的原始数据所占的空间，Selector的结果空间若不加限制，也仍可能导致内存溢出。因此必须通过设定cache值，来约束Selector的结果可以占用的最大空间限制。又由于该例中我们是选前 100，为使其统计值相对误差更少一些，我们设其值为10000，即平均在100个值中获取一个有效结果，因缓存满，数据被淘汰导致的数据误差就会显的微不足道。

​	缺省情况下，cache的大小为100，缓冲区淘汰算法为fifo。当cache的值设为-1时，表示不对查询可用的缓存区的大小做限制。但使用该值时需要慎重，一个不合适的查询可能会导致整个虚拟机的内存溢出，从而影响系统的整体应用。

**[all|distinct]**

​	表示返回所有的查询结果数据还是将查询结果中的数据去重后再返回。数据去重时会判断是否有多条数据存在完全一致的情况，若多条数据完全一致，则返回且只返回一条数据。缺省为all，返回所有数据。而distinct表示只返回没有重复数据的结果。

***COLUMN* [[as] *ALIAS*][,...]**

​	表示查询要返回的结果数据列。*COLUMN*实际为一个Operand(详见：[操作数(Operand)](#操作数(Operand)))，可以是常数、字段名、函数、表达式或者一个列筛选操作数(即SQL中的子查询)。*ALIAS*为*COLUMN*的别名，方便对*COLUMN*的记忆，起别名时可以选择使用或不使用”as”关键字。group by或order by子句可以通过*COLUMN*、*ALIAS*或者数据列所处位置的索引对数据列进行引用，索引值从1开始计数，详见group by子句和order by子句。

## FROM子句

​           描述了查询从哪些数据表中获取数据。语法如下：**

```
from TABLE [[as] ALIAS] [,...]
[[inner|left|right|full] join] TABLE [[as] ALIAS]
[on SEARCH_CONDITION] [...]]
```

***TABLE* [[as] *ALIAS*][,...]****

​	TABLE*在MOQL中并不是指一个真正的持久化数据表，它所指代的是一个置于内存中的集合对象。这个集合对象可以是一个java.util.Map对象、系统数组对象、所有继承了java.util.Iterator接口的对象、数据库查询结果java.sql.ResultSet对象、MOQL自己查询返回的org.moql.RecordSet对象以及一个子查询。这些集合对象中的每一个对象元素被MOQL看做为一条记录。MOQL查询中定义的查询结果输出列、条件、分组以及排序等都是针对这些对象元素来设定的。另外，该子句与SQL的From子句一致，支持为*TABLE*设定一个易记的别名，并可在后续的MOQL编写中以该别名访问*TABLE*的信息。如：

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

​	链表BeanA在上例中的别名为a，a.id、a.name等都是通过使用别名完成的对链表BeanA元素对象的访问。

​	当MOQL需要同时从多个*TABLE*中查询数据时，其语法表现形式与SQL一致，都是将多个表放在from关键字后，并用“，”隔开，且*TABLE*的数目没有限制。如：

```
select a.id, a.name, a.num%500, b.id, b.name, b.num%500 from BeanA a, BeanB b where a.num%500 > 10 or b.num%500 < 400 order by 3 desc, 6 desc
```

**[[inner|left|right|full] join] *TABLE* [[as] *ALIAS*][,...]**

​	描述了进行多个表查询时，表之间的数据连接方式。MOQL缺省支持inner join(内连接)、left join(左[外]连接)、right join(右[外]连接)以及full join(全[外]连接)四种表连接方式。当join没有添加任何前缀时，表示inner join(内连接)。同一MOQL语句中，连接的个数没有限制。至于内连接、左连接、右连接、全连接的数据连接效果，MOQL与SQL完全一致，在此就不做赘述，可以参考SQL相关的资料进行了解。

**[on *SEARCH_CONDITION*]**

​	描述了数据连接时的连接条件。*SEARCH_CONDITION*为具体的连接条件，其详细描述参见[过滤器(Filter)](#过滤器(Filter))。

​	MOQL表连接查询的语句示例如下：

```
select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num from BeanA a left join BeanB b on a.id = b.id right join BeanC c on c.id = b.id order by 3 desc
```



## WHERE子句

 	描述了数据查询的查询条件，该条件与FROM子句中的join操作的on条件描述一致。

```
[where SEARCH_CONDITION]
```

​	***SEARCH_CONDITION***为具体的查询条件，其详细描述参见[过滤器(Filter)](#过滤器(Filter))。

## GROUP BY子句

​	描述查询的用哪些字段进行分组。使用该子句时，与SQL一致，在SELECT子句中描述的所有没有进行聚合计算的字段都必须包含在该子句中。

```
[group by COLUMN [, ...]]
```

***COLUMN***

​	***COLUMN***可以是一个出现在SELECT子句中的列、列的别名或列所处的索引位置，索引位置从1开始计数。一个GROUP BY子句可以包括一个或多个***COLUMN***。如：

```
select count(a.id) cnt, sum(a.num) sum, a.num%500 mod, a.id from BeanA a group by 3, a.id
```

## HAVING子句

​	与SQL语法一致，描述了对group by子句中产生的组数据进行过滤的过滤条件。Having子句中的所有字段都需要是select子句中的列，与where不同的是，这里是对分组结果数据的过滤，而where是对原始表数据进行的。

```
[having SEARCH_CONDITION]
```

***SEARCH_CONDITION***为具体的查询条件，其详细描述参见[过滤器(Filter)](#过滤器(Filter))。

## 集合运算子句

​	集合运算描述了两个或多个查询结果间的运算，运算包括：union(并集)、except(差集)、symexcept(对称差集)、complementation(补集)以及intersect(交集)等。其语法结构如下：

```
[union [all]|except|symexcept|complementation|intersect [all] QUERY_EXPRESSION]
```

​	进行集合运算的两个结果集A、B，其集合运算式为：“A*集合运算符* B”。A、B两个结果集，其字段数目和字段顺序可以不一致，但必须保证集合A的字段定义包含于集合B的字段定义中，否则两个结果集无法进行正常的集合运算。运算时，集合运算以左操作数即A结果集的字段和字段序为基础进行集合运算。

**union [all]**

​	表示两个查询结果合并在一起。 [all]表示合并集合时，不去除两个集合中相同的记录。而缺省情况下，若两个集合存在完全一样的多条记录，则只保留一条。并集的数学式为：A ∪ B。

**except**

​	表示两个查询结果进行差集运算，即用第一个结果集减去第二个结果集，去除第一个结果集中与第二个结果集完全相同的记录后剩下的记录即为差集结果。如：两个集合A、B，其差集数学式为：A – B。

**symexcept**

​	对称差集的结果为两个查询结果互相求差集后，又对两个差集求了并集。其数学式为：A – B ∪ B – A。

**complementation**

​	表示先对两个结果集求合集，然后再求左操作数结果集的补集。如：A complementation B，其对应的补集数学式为 (A ∪B) – A。

**intersect [all]**

​	表示对两个结果集求交集。[all] 表示集合相交时，不去除交集中两个结果集多次相交命中的相同的记录。而缺省情况下，若交集中存在完全一样的多条记录，则只保留一条。交集的数学式为：A ∩ B。

## ORDER BY子句

​	与SQL语法一致，描述了如何对查询结果进行排序，排序是按哪个或是哪几个字段进行排序；按升序、还是按降序排序等。一个Selector定义中只能有一个order by子句，但可以有多个select子句。

```
[order by ORDER_ITEM [asc | desc] [, ...]]
```

***ORDER_ITEM [asc | desc]***

​	表示要进行排序的字段的名字。这个字段可以是select子句中的列或者是其别名；也可以是列的索引值，索引从1开始计数；还可以是from子句中任意一个表(集合对象)的字段名，如：table1.name，table1为集合对象的别名，而name为该集合对象的一个字段。用于排序的字段不必要一定是select子句中包含的列。[asc | desc]表示排序方式，asc表示对字段中的数据按升序排序；desc表示对字段中的数据按降序排序。排序时可以同时指定多个字段及排序方式。执行时，排序工作从左到右依次对每个字段进行排序。其含义为，首先对结果集按字段1进行排序；然后在此基础上对数据字段1相同的记录再按字段2进行排序……。

## LIMIT子句

```
[limit [OFFSET,] N[%]]
```

​	表示从指定的偏移开始，返回查询结果的前N条记录或前N%的记录。OFFSET表示从结果集的哪个位置开始取数据，而N表示取的数据的个数。Limit可用于分页查询。OFFSET和N为正整数，两者的的取值与cache设定的大小相关，OFFSET和N应该小于等于cache的大小。当OFFSET为0，N大于cache的大小时，表示返回全部结果。另外，当其表示百分数时，若N大于100也表示返回所有的结果数据。

## DECORATE BY子句

```
[decorate by DECORATE_FUNCTION[, ...]]
```

​	表示对其它子句处理后的查询结果集按指定的方法进行装饰修改。DECORATE_FUNCTION表示一个装饰函数，该函数可以对结果集对象进行修改处理。该子句可以支持多个装饰函数，装饰函数间用“，”号隔开。装饰函数按从左到右的顺序依次执行，对结果集进行装饰。如：groupRowNumber(groupFields,valueField)函数，其含义同oracle的ROW_NUMBER_OVER函数，表示对有相同值的groupFields字段集合的纪录进行编号。每组groupFields值相同的记录，其编号值都是从1开始递增，不同组的记录其编号彼此独立增长。编号值最后将被放入到valueField所描述的字段中(见源码包中的TestSelector类的testGroupRowNumberDecorate方法)。DECORATE_FUNCTION可以通过编写函数进行扩展(扩展方法详见《MOQL—函数(Function)》)，用户可以根据需要扩展对结果集的修饰逻辑。原则上讲，装饰函数不能增加或删除结果集的列，只能修改结果集的列。

​	Selector各子句都有相关的代码样例，在jar包的example/TestSelector.java中，这里就不一一列举，有兴趣的朋友可以在如下的项目路径中下到最新的jar包.

# 转换器(Translator)

​	MOQL是一个面向内存对象设计的查询统计分析工具，其语法兼容SQL标准。其语法结构除支持如下： 

```
select count(a.id) cnt, sum(a.num) sum, a.num%500 mod from BeanA a group by 3 having mod > 10 order by 1
```

的标准SQL书写格式外，还拥有xml结构的书写格式，如下：

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

​	以上两种语法格式可以互换。当MOQL使用xml格式时，可以根据需要将xml格式的语法格式转换为不同的SQL方言。除MOQL语法外，还支持Oracle、SQL Server、DB2、MySQL、PostgreSQL等方言。

​	MOQL的xml语法格式可以看作是sql语句的语法树结构形式，他结构清晰，比较利于通过程序进行编写和修改。尤其方便前后台的交互。当一个应用系统需要提供比较完善且开放的查询功能时，可以通过编辑xml格式的查询语句，然后将其传送给后台；由后台根据数据库的类型转换为对应的sql方言，然后对数据库进行查询并最终返回结果集。这种模式可以在前端为用户提供灵活开放的查询功能，在后端屏蔽数据库的差异，方便迁移。

​	以下是一段演示代码，演示了从标准sql格式到xml格式的转换，以及xml格式到各方言的sql格式转换。

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

输出如下：

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

​	从上面演示代码输出的xml格式我们可以发现其格式比较复杂，可以包含不止一个selector标签（注：每个selector标签代表了一个sql语句）。这与MOQL最初的设计初衷有关，不影响使用，可以不必关心。另外，需要注意的就是，当sql语句中存在聚集运算时，如：union、intersect等时，其xml结构的标签就不是selector了，而是setlector了，setlector下会包含两个selector标签，表示两个集合进行聚集运算。

## SQL to ElasticSearch DSL

​	众所周知ElasticSearch目前是一个应用最为广泛的分布式搜索与分析引擎，它的功能强大，能够已很高的性能访问大规模数据。它拥有强大的查询分析语法，能够完成模糊查询、精准查询及聚集计算等诸多功能的表达。但对于那些用惯了SQL语言的数据分析人员来说，掌握ElasticSearch的DSL语言来做以前熟悉的事情，还是有比较陡的学习曲线的。

​	MOQL转换器用于完成从MOQL语法(SQL语法的一个子集，涵盖了绝大多数常用语法)到各类不同数据库SQL语法的转换，如它提供到Oracle、SqlServer以及DB2等不同SQL方言的转换，也提供了到ElasticSearch DSL的语法转换。有了这种转换，可以大大降低数据分析人员学习不同分析语言的学习曲线，能够快速的享受新的分析引擎技术带来的便利。

​	MOQL本质上是一个开发程序包，应用它进行SQL到ElasticSearch DSL的转换非常方便，如下代码示例：

```
String sql = "select ip.src, max(ip.sport), min(ip.sport) from ip3 ip group by ip.src order by ip.src desc limit 10 ";
 try {							
    String es = MoqlTranslator
        .translateMoql2Dialect(sql, SqlDialectType.ELASTICSEARCH);
    es = es.trim();
    System.out.println(es);
  } catch (MoqlException e) {
    e.printStackTrace();
  }
}
```

该代码执行完转换后，输出的ElasticSearch DSL语法为：

```
{	
  "size": 10,
  "sort": [
    {	
      "src": {
        "order": "desc"
      }
    }
  ],
  "query": {	
    "match_all": {}
  },	
  "aggs": {
    "src": {
      "terms": {
        "field": "src"
      },
      "aggs": {
        "max$ip_sport$": {// 程序自动起的别名
          "max": {
            "field": "sport"
          }
        },
        "min$ip_sport$": {// 程序自动起的别名
          "min": {
            "field": "sport"
          }
        }
      }
    }
  }
}
```

​	将该结果提交给ElasticSearch引擎可以达到与程序中sql语句匹配的执行效果。就是这样简单，可以立刻去<https://github.com/colorknight/moql>下载个源码试试了。

​	当然由于ElasticSearchDSL语法的能力过于强大，MOQL目前还无法提供完整的转换能力，并且为适应两种不同语法间的差异还做了些技巧性的设计，需要使用者在使用时加以注意。

​	ElsaticSearch DSL的query子句中有query上下文与filter上下文两种上下文(本文不解释两种上下文的差异，请参见相关资料)，这两种上下文分别对应了SQL语法的两个子句。query上下文对应了where子句，filter上下文对应了having子句。这样的对应关系主要是技巧性的对应，并不体现各部分在ElasticSerach上的执行顺序。所以在应用ElasticSearch DSL的此类特性时需要留意，不要写错SQL子句。

​	另外，对于MOQL现在不能支持的ElasticSearch DSL的特殊语法，MOQL建议通过编写函数(UDF,User Define Function)的方式予以扩展。扩展时通过继承org.moql.sql.es.ESFunctionTranslator接口来实现扩展函数，然后再去org.moql.sql.es. ElasticSearchTranslator中注册函数即可。

​	下面将给出SQL与ElasticSearch DSL的语法转换对照表，方便使用者全面了解SQL转换成ElasticSearch DSL后能达到的语法能力。

| MOQL                                  | ElasticSearch DSL                        |
| ------------------------------------- | ---------------------------------------- |
| UNION,INTERSECT,EXCEPT等集合操作子句         | 未转换映射                                    |
| SELECT子句                              | 当MOQL语句中不含DISTINCT和GROUP子句时，映射为ElasticSearch  DSL的_source子句。若SQL语法的select子句非”.*”模式时，即有具体的投映列时，这些列字段将被放入_source子句的includes属性中，否则则忽略_source子句;而当MOQL含有DISTINCT和GROUP子句时，映射为ElasticSearch的Aggs。此时SELECT子句中的投影列需遵循SQL语法的约定，这样才能正确转换。 |
| DISTINCT子句                            | 转换为ElasticSearch的Aggs子句。                 |
| FROM子句                                | 不进行转换                                    |
| WHERE子句,HAVING子句                      | WHERE子句映射为query子句的query上下文，HAVING子句映射为query子句的filter上下文 |
| and                                   | 在WHERE子句中时被转换为must;在HAVING子句中时被转换为filter |
| or                                    | 转换为should                                |
| not                                   | 转换为must_not                              |
| <>(不等于)                               | 转换为must_not+term                         |
| =(等于)                                 | 转换为term                                  |
| >(大于)、<(小于)、>=(大于等于)、<=(小于等于)、between | 转换为range                                 |
| like                                  | 转换为regexp                                |
| in                                    | 转换为terms                                 |
| is                                    | 转换为must_not+exists                       |
| 用于改变优先级的括号                            | 转换为层级关系                                  |
| GROUP BY                              | 转换为ElasticSearch的Aggs子句                  |
| LIMIT子句                               | 当MOQL语句中不含有DISTINCT和GROUP子句时，该子句被转换为ElasticSearch  DSL的from和size属性；而当含有这两个子句时，该值会被映射到terms aggregation子句的size 属性中。 |
| ORDER子句                               | 当MOQL语句中不含有DISTINCT和GROUP子句时，该子句被转换为ElasticSearch  DSL的sort子句；而当含有这两个子句时，该值会被映射到terms aggregation子句的order 子句中。 |

​	MOQL用扩展函数的方式来支持ElasticSearch DSL语法中SQL无法描述的部分，如下：

| ElasticSearch DSL   | MOQL                                     |
| ------------------- | ---------------------------------------- |
| match               | match(fields,queryString)  fields：字符串数组，表示多个字段时，字段间用“,”隔开。如：‘field1,field2’。当只有一个field值时，该函数被映射为match子句，当fields字段有多个值时，该函数被映射为malti_match子句  queryString：字符串，表示检索条件，等同与match与multi_match中的query属性 |
| multi_match         | 见match                                   |
| match_phrase        | matchPhrase(field,  queryString)或matchPhrase(field, queryString, analyzer)  field：字符串，match_phrase子句的名字  queryString：字符串，表示match_phrase子句的query属性  analyzer：字符串，与match_phrase的同名属性一致 |
| match_phrase_prefix | matchPhrasePrefix(field,  queryString)或matchPhrasePrefix(field, queryString, analyzer)  field：字符串，match_phrase_prefix子句的名字  queryString：字符串，表示match_phrase_prefix子句的query属性  analyzer：字符串，与match_phrase的同名属性一致 |
| terms_set           | termsSet(field,  valueSet, minMatchField)  field：字符串，terms_set子句的名字  valueSet：字符串数组，表示terms数组，当需要输入多个值时，值与值之间用”,”隔开。  minMatchField：字符串，表示minimum_should_match_field属性 |
| regex               | regex(field,pattern)  field：字符串，字段名  pattern：字符串，表示正则表达式的模式。 |
| fuzzy               | fuzzy(field,  value)  或fuzzy(field,value,fuzziness,prefix_length,max_expansions)  field：字符串，字段名  value：字符串，字段值  fuzziness：整数，与fuzzy子句同名属性一致  prefix_length：整数，与fuzzy子句同名属性一致  prefix_length：整数，与fuzzy子句同名属性一致 |
| type                | type(value)  value：字符串，字段值               |
| ids                 | ids(type,  values)  type：字符串，与ids子句同名属性一致。  values：字符串数组，多个值之间用”,”隔开。与ids子句同名属性一致。 |
| more_like_this      | moreLike(fields,likeText,minTermFreq,maxQueryTerms)  fields：字符串数组，表示多个字段时，字段间用“,”隔开。表示more_like_this的fields属性  likeText：字符串，表示表示more_like_this的like_text属性  minTermFreq：整数，表示more_like_this的min_term_freq属性  maxQueryTerms：整数，表示more_like_this的max_query_terms属性 |

## SQL to ElasticSearch DSL改进

​	最近团队在使用MOQL的SQL到ElasticSearch DSL转换时提出，该转换器不能完成深度分页场景的应用。而ElasticSearch为该类应用提供了“search_after”的参数解决方案。ElasticSearch的这个解决方案使得前后两个QUERY DSL有了上下文依赖，后续的查询要依赖上一个查询结果中返回的内容作为条件拼装检索语句。为满足这个需求，MOQL升级了moql-translator和moql-querier两个模块。

 	在moql-translator中整体升级了SqlTranslator接口，允许传入一个Map类型的translationContext参数，该参数可以带入语法转换时所需的参数，这样所有的语法转换器都可以支持有上下文依赖的语法转换了。示例代码如下：

```
// 带有limit的SQL语句，按照search_after的说明，limit语法中的数字20表示from，将被忽略

String sql = "select w.* from web w where w.port=443 limit 20,10";

Map<String, Object> translationContext = new HashMap<String, Object>();

Object[] features = new Object[] { 133, "test" };

// RESULT_SORT_FEATURES常量为传递给search_after语法的参数的名字

translationContext

    .put(EsTranslationContextConstants.RESULT_SORT_FEATURES, features);

testESDialect(sql, translationContext);
```

该代码执行完转换后，输出的ElasticSearch DSL语法为：

```
{
  "search_after": [
    133,
    "test"
  ],
  "size": 10,
  "query": {
    "term": {
      "port": "443"
    }
  }
}
```



​	即然语法转换有了上下文的需求，后续的查询依赖于前序查询的结果，就需要能够从结果中取出后续查询所依赖的上下文信息。之前的moql-querier只能读取结果中的部分数据，并以RecordSet的形式返回。而search_after所依赖的结果中的sort字段无法获得。故此次也对DataQuerier接口进行了升级，为接口中加入了一个SupplementReader参数。用户可以通过实现SupplementReader接口读取返回结果集中的其它信息。MOQL提供了一个CommonSupplementReader的缺省实现，可以读取检索结果中的total，max_score及sort等字段信息。

​	示例代码如下：

```
String sql = "select t.DVC_ADDRESS, t.MESSAGE from ins_test t order by t.SEVERITY LIMIT 5";

try {

  CommonSupplementReader supplementReader = new CommonSupplementReader();

  RecordSet recordSet = dataQuerier.query(sql, supplementReader);

  outputRecordSet(recordSet);

  System.out.println(supplementReader.getTotalHits());

} catch (IOException e) {

  e.printStackTrace();

}
```

​	使用者可以通过访问supplementReader获取结果集中返回的其它结果信息。

​	注：开发时需在pom文件中将moql-translator和moql-querier升级到1.1.1版本。

# 附录

## 函数

### 聚集函数

| 函数名                                                       | 函数说明                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| avg(String column)                                           | 求给定列的所有值中的平均值<br />参数：<br />column: 求平均值的列的名字，列的数据类型必须为数值型 |
| count(String column[, boolean  distinct])                    | 求给定列的所有值的数量<br />参数：<br />column: 求计数的列的名字<br />distinct: 布尔型可选参数，表示是否只对不同的值进行计数，true：表示只对不同值的记录计数，false:表示所有记录计数，缺省为false |
| joint(String column[, String separator])                     | 字串连接运算，将所给列的所有值用给定的分隔符连接成一个字符串。<br />参数：<br />column: 要做字串连接的列的名字<br />separator: 连接串所用的分隔符 |
| kurtosis(String column)                                      | 求给定列的峰度值<br />参数：<br />column: 求峰度的列的名字，列的数据类型必须为数值型 |
| median(String column)                                        | 求给定列的中位数，若数值为奇数个，返回最中间的值；若为偶数个，返回中间的两个数的平均值<br />参数：<br />column: 求中位数的列的名字，列的数据类型必须为数值型 |
| max(String column)                                           | 求给定列的所有值中的最大值<br />参数：<br />column: 求最大值的列的名字 |
| min(String column)                                           | 求给定列的所有值中的最小值<br />参数：<br />column: 求最小值的列的名字 |
| mode(String column)                                          | 求给定列的众数，返回一个众数数组<br />参数：<br />column: 求众数的列的名字 |
| notNull(String column[, String defaultVale[,boolean first]]) | 求给定列的非空值，若列无非空值允许为其设置缺省值。缺省情况下，该函数会取得列的第一个非空值，若想取得最后一个非空值，可通过将可选参数设为false<br />参数：<br />column: 要取非空值的列的名字<br />defaultValue: 当列无非null值时的缺省值<br />first: 是否取第一个非null值 |
| percentile(String column[, double percentile])               | 求给定列的指定百分位的数值。<br />参数：<br />column: 要做百分比计算的列的名字，列的数据类型必须为数值型<br />percentile: 百分比数值，取值在(0,1]之间，缺省为0.5 |
| range(String column)                                         | 求给定列的极差<br />参数：<br />column: 求极差的列的名字，列的数据类型必须为数值型 |
| semiVariance(String column)                                  | 求给定列的半方差<br />参数：<br />column: 求半方差的列的名字，列的数据类型必须为数值型 |
| skewness(String column)                                      | 求给定列的偏度值<br />参数：<br />column: 求偏度的列的名字，列的数据类型必须为数值型 |
| standardDeviation(String column)                             | 求给定列的标准差<br />参数：<br />column: 求标准差的列的名字，列的数据类型必须为数值型 |
| sum(String column)                                           | 求给定列的所有值的和<br />参数：<br />column: 求和的列的名字，列的数据类型必须为数值型 |
| variance(String column)                                      | 求给定列的方差<br />参数：<br />column: 求方差的列的名字，列的数据类型必须为数值型 |
|                                                              |                                                              |

### 数学计算函数

| 函数名                                | 函数说明                                                     |
| ------------------------------------- | ------------------------------------------------------------ |
| abs(String field)                     | 取绝对值<br />参数：<br />field: 取绝对值的字段的名字，列的数据类型必须为数值型 |
| cbrt(String field)                    | 求立方根<br />参数：<br />field: 求立方的字段的名字，列的数据类型必须为数值型 |
| ceil(String field)                    | 向上取整<br />参数：<br />field: 取整的字段的名字，列的数据类型必须为数值型 |
| cos(String field)                     | 对指定字段求余弦<br />参数：<br />field: 求cos的字段的名字，列的数据类型必须为数值型 |
| exp(String field)                     | 以自然数e为底的指数幂<br />参数：<br />field: 字段的名字，列的数据类型必须为数值型 |
| floor(String field)                   | 向下取整<br />参数：<br />field: 取整的字段的名字，列的数据类型必须为数值型 |
| log(String field)                     | 以自然数e为底，求对数<br />参数：<br />field: 字段的名字，列的数据类型必须为数值型 |
| log10(String field)                   | 以10为底，求对数<br />参数：<br />field: 字段的名字，列的数据类型必须为数值型 |
| pow(String field, double power)       | 求指数<br />参数：<br />field: 字段的名字，列的数据类型必须为数值型<br />power:指数 |
| precent(String field[,int precision]) | 将数值转换为百分数形式，如：0.23转换为23%<br />参数：<br />field: 待转换字段的名字，列的数据类型必须为数值型<br />precision：精度，转换为百分数时保留的小数的精度，缺省为0表示百分数不留小数位 |
| round(String field)                   | 四舍五入取整<br />参数：<br />field: 取整的字段的名字，列的数据类型必须为数值型 |
| sin(String field)                     | 求正弦<br />参数：<br />field: 字段的名字，列的数据类型必须为数值型 |
| sqrt(String field)                    | 求平方根<br />参数：<br />field: 字段的名字，列的数据类型必须为数值型 |
| tan(String field)                     | 求正切<br />参数：<br />field: 字段的名字，列的数据类型必须为数值型 |
|                                       |                                                              |



### 其它函数

| 函数名                            | 函数说明                                                     |
| --------------------------------- | ------------------------------------------------------------ |
| regex(String field, String regex) | 对指定字段进行正则匹配，匹配成功返回true，失败返回false。<br />参数：<br />field: 待正则匹配的列的名字<br />regex: 正则表达式 |
| trunc(String field,int precision) | 浮点数格式化操作<br />参数：<br />field: 待格式化字段的名字，列的数据类型必须为数值型<br />precision：精度，浮点数要格式化的精度，当数值精度不够时补0。如：浮点数3.23，精度为3时，输出为3.230 |
|                                   |                                                              |

