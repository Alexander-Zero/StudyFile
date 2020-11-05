# 设计模式 Design Pattern

### 面向对象的六大原则

SRP单一职责原则(Single Responsibility Principle): 高内聚,低耦合

OCP开闭原则 (Open-Closed Principle): 对扩展开发,对修改关闭

DIP依赖倒置原则(Dependency Inversion Principle) : 面向接口编程(依赖抽象,而不是具体的实现)

LSP里氏替换原则(Liscov Substitution Principle):所有使用父类的地方,必须可以透明的使用子类的对象.

ISP接口隔离原则(Interface Segregation Principle): 接口单一职责

LoD迪米特法则(Law of Demeter): 高内聚,低耦合(尽量不要和陌生人说话)

### 指导思想

Maintainability 可维护性

Extensibility Scalability 可扩展性

Reusability 可复用性

Flexibility Mobility Adapatability灵活性

### 设计模式描述方式

典型用法

UML类图

代码

优缺点

## 1 创建型

### 1.1 Factory Method (工厂方法)

典型应用

产品纵向扩展



### 1.2 Abstract Factory (抽象工厂)

典型应用

产品族的扩展,若需更换产品族,只需更换相应的工厂即可

代码:

```java
//抽象工厂
public abstract class GameFactory {

    abstract AbstractTank createTank();

    abstract AbstractBullet createBullet();

    abstract AbstractExplode createExplode();
}

//抽象产品族
public abstract class AbstractTank {
    abstract void paint(Graphics g);
}

public abstract class AbstractBullet {
    abstract void paint(Graphics g);
}

public abstract class AbstractExplode {
    abstract void paint(Graphics g);
}

//古代工厂
public class AncientGameFactory extends GameFactory {
    @Override
    AbstractTank createTank() {
        return new AncientTank();
    }

    @Override
    AbstractBullet createBullet() {
        return new AncientBullet();
    }

    @Override
    AbstractExplode createExplode() {
        return new AncientExplode();
    }
}

//古代产品族
public class AncientTank extends AbstractTank {
    @Override
    void paint(Graphics g) {
        g.drawString("战车", 100, 100);
    }
}

//public class AncientBullet extends AbstractBullet {
    @Override
    void paint(Graphics g) {
        g.drawString("弓箭", 10, 10);
    }
}

public class AncientExplode extends AbstractExplode {
    @Override
    void paint(Graphics g) {
        g.drawString("火药爆炸", 10, 10);
    }
}


//现代工厂
public class ModernGameFactory extends GameFactory {
    @Override
    AbstractTank createTank() {
        return new ModernTank();
    }

    @Override
    AbstractBullet createBullet() {
        return new ModernBullet();
    }

    @Override
    AbstractExplode createExplode() {
        return new ModernExplode();
    }
}

//现代产品族
public class ModernTank extends AbstractTank {
    @Override
    void paint(Graphics g) {
        g.drawString("现代坦克", 5, 5);
    }
}

public class ModernBullet extends AbstractBullet {
    @Override
    void paint(Graphics g) {
        g.drawString("穿甲弹", 5, 5);
    }
}

public class ModernExplode extends AbstractExplode {
    @Override
    void paint(Graphics g) {
        g.drawString("东风快递爆炸", 10, 10);
    }
}


//测试用例
public class Main extends Frame {
    @Override
    public void paint(Graphics g) {
        //自需修改一行代码,可达到一键换风格
        GameFactory factory = new AncientGameFactory();

        AbstractTank tank = factory.createTank();
        AbstractBullet bullet = factory.createBullet();
        AbstractExplode explode = factory.createExplode();

        //画出坦克,子弹和爆炸效果
        tank.paint(g);
        bullet.paint(g);
        explode.paint(g);
    }
}
```



### 1.3 Builder (构造者)

1.3.1 典型用法

对大型复杂对象(含多属性)的构建

1.3.2 UML类图

1.3.3 代码

```java
@Getter
@Setter
@ToString
//需要构建的类
public class Person {
    private String name;
    private String gender;
    private Integer age;
}
//构建器
public class PersonBuilder {
    private Person person;

    public PersonBuilder() {
        person = new Person();
    }

    public PersonBuilder name(String name) {
        person.setName(name);
        return this;
    }

    public PersonBuilder age(Integer age) {
        person.setAge(age);
        return this;
    }

    public PersonBuilder gender(String gender) {
        person.setGender(gender);
        return this;
    }

    public Person build() {
        return person;
    }
}

//测试类
class Main{
    public static void main(String[] args) {
        Person zhangshan = new PersonBuilder()
                .name("张山")
                .age(15)
                .gender("男")
                .build();
        System.out.println(zhangshan);
    }
}
```

```java
//直接将builder作为需要build对象的静态内部类
@Getter
@Setter
@ToString
public class Person {
    private String name;
    private String gender;
    private Integer age;
	
	//静态内部类
    public static class PersonBuilder {
        private Person person;

        public PersonBuilder() {
            person = new Person();
        }

        public PersonBuilder name(String name) {
            person.setName(name);
            return this;
        }

        public PersonBuilder age(Integer age) {
            person.setAge(age);
            return this;
        }

        public PersonBuilder gender(String gender) {
            person.setGender(gender);
            return this;
        }

        public Person build() {
            return person;
        }
    }

}

//测试类 
public class Main {
    public static void main(String[] args) {
        Person zhangshan = new Person.PersonBuilder()
                .name("张山")
                .age(15)
                .gender("男")
                .build();
        System.out.println(zhangshan);
    }
}

```



### 1.4 Prototype (原型模式)

####  1.4.1 典型用法

对已实例化对象(属性已赋值)作为原型进行克隆,JAVA已默认实现,Clonable接口. 注意深克隆和浅克隆,深克隆需将引用类型也克隆

### 1.5 Singleton (单例模式)

#### 典型应用:

实例不需要创建多个,全局唯一,可节约内存空间,Spring Bean工厂默认实现,分饿汉式和懒汉式单例

代码:

```java
//静态实例,饿汉式,类加载就创建了单例
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();

    private Singleton() {
    }

    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

```java
//双重检查, 懒汉式
public class Singleton1 {
    private static Singleton1 instance;

    private Singleton1() {
    }

    public static Singleton1 getInstance() {
        if (instance == null) {
            synchronized (Singleton1.class) {
                if (instance == null) {
                    instance = new Singleton1();
                }
            }
        }
        return instance;
    }
}
```

```java
//静态内部类 懒汉式
public class Singleton2 {

    private Singleton2() {
    }

    private static class SingletonHolder {
        private static final Singleton2 INSTANCE = new Singleton2();
    }

    public static Singleton2 getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```

```java
//枚举单例
public enum Singleton3 {
    INSTANCE;
    public static Singleton3 getInstance() {
        return INSTANCE;
    }
}
```



## 2  结构型

### 	2.1 Adapter (适配器) 

#### 		2.1.1 应用场景

​		别名Wrapper,作用类似于转接头,是将一个不能直接使用的类包装为可以使用的类.

#### 		2.1.2 UML图

#### 		2.1.3 

### 	2.2 Bridge (桥接模式)

### 	2.3 Composite (组合模式)

### 	2.4 Decorator (装饰器)

### 	2.5 Facade (门面模式)

### 	2.6 Flyweight (享元模式)

### 	2.7 Proxy (代理模式)



## 3 行为型

### 	3.1 Interceptor (解释器)

### 	3.2 Template Method (模板方法)

### 	3.3 Chain of Responsibility (责任链)

#### 		3.3.2 代码

```java
//测试类 模拟Servlet对Request和Response进行过滤处理,双向处理
public class Main {
    public static void main(String[] args) {
        FilterChain chain = new FilterChain();
        chain.add(new Filter1());
        chain.add(new Filter2());
        Request request = new Request();
        Response response = new Response();
        chain.doFilter(request, response, chain);
    }
}

//责任链
public class FilterChain implements Filter {
    private List<Filter> filters = new ArrayList<Filter>();
    private int cursor = 0;

    public void doFilter(Request request, Response response, FilterChain chain) {
        if (filters.size() == cursor) {
            return;
        }
        this.filters.get(cursor++).doFilter(request, response, chain);
    }

    public void add(Filter filter) {
        filters.add(filter);
    }

}

//单节点抽象  可通过设置返回布尔值来控制是否往下传递
public interface Filter {
    void doFilter(Request request, Response response, FilterChain chain);
}

//节点1
public class Filter1 implements Filter {
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("First In Request");
        chain.doFilter(request, response, chain);
        System.out.println("First In response");
    }
}

//节点2
public class Filter2 implements Filter {
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("Second In Request");
        chain.doFilter(request, response, chain);
        System.out.println("Second In Response");
    }
}

public class Request {
    private String msg;
}

public class Response {
    private String msg;
}
```

```java
//测试类  单向拦截,通过Filter.doFilter的返回值确定是否往下执行, 
public class Main {
    public static void main(String[] args) {
        FilterChain chain = new FilterChain();
        chain.add(new Filter1());
        chain.add(new Filter2());
        FilterObject filterObject = new FilterObject();
        chain.doFilter(filterObject);
    }
}

public class FilterChain implements Filter {
    private List<Filter> filters = new ArrayList<Filter>();

    public boolean doFilter(FilterObject filterObject) {
        for (int i = 0; i < filters.size(); i++) {
            //若filter.doFilter返回false,表示不在往下执行
            if (!filters.get(i).doFilter(filterObject)) {
                return false;
            }
        }
        return true;
    }

    public void add(Filter filter) {
        filters.add(filter);
    }

}

//节点抽象
public interface Filter {
    //返回false表示后续链条节点不在执行
    boolean doFilter(FilterObject filterObject);
}

//节点1
public class Filter1 implements Filter {
    public boolean doFilter(FilterObject filterObject) {
        System.out.println("First In Filter");
        return true;
    }
}

//节点2
public class Filter2 implements Filter {
    public boolean doFilter(FilterObject filterObject) {
        System.out.println("Second In Request");
        return true;
    }
}

//处理对象
public class FilterObject {
    private String msg;
}
```

### 3.4 Command (命令模式)

典型引用

执行命令以及撤回操作

UML图

代码

```java
//命令(抽象父类)
public abstract class Command {
    abstract void doit();//执行命令
    abstract void undo();//撤回命令
}

//子命令(删除操作)
public class DeleteCommand extends Command {
    Content content;
    String deleteStr;

    public DeleteCommand(Content content, int size) {
        this.content = content;
        this.deleteStr = content.msg.substring(content.msg.length() - size, content.msg.length());
    }

    @Override
    void doit() {
        content.msg = content.msg.substring(0, content.msg.length() - deleteStr.length());
    }

    @Override
    void undo() {
        content.msg = content.msg + deleteStr;
    }
}


//子命令(添加操作)
public class InsertCommand extends Command {
    String insertStr;
    Content content;

    public InsertCommand(Content content, String str) {
        this.content = content;
        this.insertStr = str;
    }

    @Override
    void doit() {
        content.msg = content.msg + insertStr;
    }

    @Override
    void undo() {
        int keepLength = content.msg.length() - insertStr.length();
        String substring = content.msg.substring(0, keepLength);
        content.msg = substring;
    }
}
//操作的内容
public class Content {
    String msg;
    public Content(String msg) {
        this.msg = msg;
    }
}

//命令责任链
public class CommandChain extends Command {
    private List<Command> chain = new ArrayList<>();
    private int cursor = 0;

    @Override
    void doit() {
        if (chain.size() == cursor) {
            cursor -= 1;
            return;
        }
        chain.get(cursor++).doit();
    }

    @Override
    void undo() {
        if (cursor < 0) {
            cursor = 0;
            return;
        }
        chain.get(cursor--).undo();
    }

    public void add(Command command) {
        chain.add(command);
    }
}

//测试类, 结合责任链 连续撤回
public class MainCommand {
    public static void main(String[] args) {
        Content content = new Content("Hello World! this is China.");

        CommandChain chain = new CommandChain();
        chain.add(new InsertCommand(content, "Hi China, This is Russia."));
        chain.add(new InsertCommand(content, "Hello Russia, Nice to meet you,"));
        chain.add(new DeleteCommand(content, 2));
        chain.add(new DeleteCommand(content, 4));
        chain.add(new InsertCommand(content, "ohh,No"));

        for (int i = 0; i < 5; i++) {
            chain.doit();
        }
        for (int i = 0; i < 5; i++) {
            chain.undo();
        }
    }
}
```



### 3.5 Iterator (迭代器)

典型应用

容器与容器遍历, Collection.Iterator()方法,使用方法见Collection.Iterator()方法.

UML

### 3.6 Mediator (调停者)

内部系统各组件之间相互有关联导致关联情况太复杂,若添加组件需添加很多的关联情况,修改很多的代码(如果有if else这样的代码),所以对所有组件进行抽象,所有组件都与同一个对象进行交互,容易扩展.



### 3.7 Memento (备忘录)

典型应用

对程序的某一个瞬时状态进行留存备份,已供后续回退之需,通过JAVA Serializable接口可存储在磁盘

### 3.8 Observer (观察者)

典型应用

源对象(Source) 发布 事件(Event) 各个观察者(Observer)根据自身情况处理事件, 可结合责任链使用, 如若有观察者处理, 后续的观察者不能再处理等等

UML图

![clipboard](C:\Users\AlexanderZero\Desktop\clipboard.png)

代码

```java
//事件源 action() 是发布事件的动作
public class Source {
    private static List<Observer> observers = new ArrayList<>();

    static {
        observers.add(new FirstObserver());
        observers.add(new SecondObserver());
    }

    public static void action(Event event) {
        for (Observer observer : observers) {
            observer.action(event);
        }
    }

    //测试
    public static void main(String[] args) {
        Event<Tank> tankEvent = new Event<>(this);
        action(tankEvent);
    }
}

//发布的事件, 一个有getSource定义 获取 发布源 ,并且会抽象事件
public class Event<T> {
    private Object source;
    private Date date;

    public Event(Object source) {
        this.source = source;
    }

    Object getSource() {
        return source;
    }

    Date getDate() {
        return date;
    }
}

//观察者 抽象一个观察事件并产生行动的方法
public abstract class Observer {
    abstract void action(Event event);
}

//观察者1
public class FirstObserver extends Observer {
    @Override
    void action(Event event) {
        if (event.getSource() instanceof Tank) {
            if (event.getClass().getGenericSuperclass() instanceof Tank) {
                System.out.println("Source published a tank-related Event");
            }
        }
    }
}

//观察者2
public class SecondObserver extends Observer{
    @Override
    void action(Event event) {
        System.out.println("I will do this!");
    }
}
```



### 3.9 State (状态模式)

### 3.10 Stratagy (策略模式)

### 3.11 Visitor (访问者模式)



