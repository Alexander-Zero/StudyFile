# 设计模式 Design Pattern

面向对象的六大原则

单一职责原则(Single Responsibility Principle): 高内聚,低耦合

开闭原则 (Open-Closed Principle): 对扩展开发,对修改关闭

依赖倒置原则(Dependency Inversion Principle) : 面向接口编程(依赖抽象,而不是具体的实现)

里氏替换原则(Liscov Substitution Principle):所有使用父类的地方,必须可以透明的使用子类的对象.

接口隔离原则(Interface Segregation Principle): 接口单一职责

迪米特法则(Law of Demeter): 高内聚,低耦合(尽量不要和陌生人说话)

指导思想:

Maintainability 可维护性/ Extensibility Scalability 可扩展性/ Reusability 可复用性/Flexibility Mobility Adapatability灵活性

典型用法:

类图:

b:UML

c:代码

d: 优缺点

## 1 创建型

### 1.1 Factory Method (工厂方法)

### 1.2 Abstract Factory (抽象工厂)

### 1.3 Builder (构造者)

### 1.4 Prototype (原型模式)

### 1.5 Singleton (单例模式)



## 2  结构型

### 	2.1 Adapter (适配器) 

#### 		2.1.1 应用场景

​		Adapter别名Wrapper,作用类似于转接头,是将一个不能直接使用的类包装为可以使用的类.

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

```
模拟Servlet对Request和Response进行过滤处理,双向处理
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

责任链
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

单节点抽象  可通过设置返回布尔值来控制是否往下传递
interface Filter {
    void doFilter(Request request, Response response, FilterChain chain);
}

节点1
class Filter1 implements Filter {
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("First In Request");
        chain.doFilter(request, response, chain);
        System.out.println("First In response");
    }
}

节点2
class Filter2 implements Filter {
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("Second In Request");
        chain.doFilter(request, response, chain);
        System.out.println("Second In Response");
    }
}

class Request {
    private String msg;
}

class Response {
    private String msg;
}
```

```java
//单向拦截,通过Filter.doFilter的返回值确定是否往下执行
class Main {
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


interface Filter {
    //返回false表示后续链条节点不在执行
    boolean doFilter(FilterObject filterObject);
}

class Filter1 implements Filter {
    public boolean doFilter(FilterObject filterObject) {
        System.out.println("First In Filter");
        return true;
    }
}

class Filter2 implements Filter {
    public boolean doFilter(FilterObject filterObject) {
        System.out.println("Second In Request");
        return true;
    }
}

class FilterObject {
    private String msg;
}
```

### 3.4 Command (命令模式)

### 3.5 Iterator (迭代器)

### 3.6 Mediator (调停者)

### 3.7 Memento (备忘录)

### 3.8 Observer (观察者)

### 3.9 State (状态模式)

### 3.10 Stratagy (策略模式)

### 3.11 Visitor (访问者模式)



