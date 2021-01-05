---

---

# JVM 虚拟机 

## GC 基础知识

1.什么是垃圾(垃圾的定义)?

  不同语言申请和释放内存的实现

语言          申请内存                    释放内存

C                malloc                           free

C++            new                              delete

Java           new                               ? 自动回收

自动回收垃圾的好处? 

	1. 编程简单, 2. 系统不易出错

手动回收常见问题:

​	1.忘记回收(内存泄漏) , 2.多次回收(会出错)

 垃圾: 没有任何 根引用(root) 指向的 一个/多个(循环引用) 的对象

垃圾判别常见的算法:

​	1.引用计数法, 循环引用问题不能解决(python使用)

​	2.根可达算法(Java使用), 有向图 dfs 或者 bfs 来实现

什么是根对象: 线程栈变量 + 静态变量 + 常量池 + JNI 指针(?)

原文: which instances are roots?

answer: jvm stack, native method stack, run-time constant pool, static references in method area, clazz



常见垃圾回收算法

	1. 标记清除(mark-sweep): 内存不连续,产生内存碎片
	2. 复制算法(copying): 占空间,性能好,无碎片
	3. 标记压缩(mark-compact): 效率低,没碎片, 不占空间



JVM内存分代模型 (部分垃圾回收器使用)

新生代(young)  +  老年代(old)  + 永久代(permanent : 1.7) /  元数据区(metadata space : 1.8)

永久代和元数据区的异同:

1.都存class类对象

2.永久代必须指定大小限制(存在内存不够), 元数据去可设可不设,,无上限(受限于系统物理内存)

3.字符串常量池: 1.7 存永久代 1.8 存堆内存(heap)

4.方法区(method area)是逻辑概念, 1.7 对应 永久代, 1.8 对应 元数据区

5.永久代放堆内存(由JVMd对该部分进行内存管理), 元数据区 内存由操作系统管理(不受JVM管理)

![image-20201218000017936](images/image-20201218000017936.png)

对象创建 & 对象存储位置 & 垃圾回收:

1. new对象, 进入eden区,若对象太大, 直接进入 tenured 区
2. 首次 YGC(minor GC): 大多数对象会被回收, 存活对象会进行S0区, 若对象太大,直接进入tenured区
3. 再次 YGC, 将 eden + S0 存活对象复制到 S1区
4. 再次 YGC, 将 eden + S1 存活对象复制到 S0区
5. 重复步骤 3,4, 每进行一次GC, 对象年龄 +1, 达到年龄阈值(默认15/CMS:6 , 可设置),进入老年代
6. 老年代(顽固分子) 满, FGC(major GC), 会回收 年轻代 + 老年代



GC tunning: GC调优(分代调优)

目标:减少FGC, 减少STW时间(Stop the World)



垃圾回收器: 

![垃圾回收器](images/垃圾回收器.png)



1. Serial 单线程串行 STW
2.  



-XX:ConcGCThreads=1 
-XX:G1ConcRefinementThreads=4 
-XX:GCDrainStackTargetSize=64 
-XX:InitialHeapSize=515777856 
-XX:MarkStackSize=4194304 
-XX:MaxHeapSize=8252445696 
-XX:MinHeapSize=6815736 
-XX:+PrintCommandLineFlags 
-XX:ReservedCodeCacheSize=251658240 
-XX:+SegmentedCodeCache 
-XX:+UseCompressedClassPointers 
-XX:+UseCompressedOops 
-XX:+UseG1GC 

能力欠缺 
|----阅读源码的能力, 快速了解API的能力.
|----测试模拟线上环境的能力,并根据情况进行调优.

软件工程职位分工:
|----后端程序员
|----前端程序员
|----测试
|----运维



## class文件结构





## 类加载过程

### loading 加载

​	将class文件加载到内存(方式可多种多样,如class文件,zip文件等)

### liking 连接

#### 	verification

​		验证class文件是否符合JVM规范

#### 	preparation

​		静态变量设置默认值

#### 	resolution

​		将类,方法,属性等符号引用解析为直接引用, 将常量池中的各种符号引用解析为为指针,偏移量等内存地址的直接引用

### initializing 初始化

​	调用类初始方法<cinit>,  将静态变量赋初始值

![ClassLoad](images/ClassLoad.png)



类加载到内存是创建了两个对象. 一个是class文件的二进制字节流, 一个是class对象, class对象指向 二进制字节流对象

![image-20210104204037513](images/image-20210104204037513.png)

双亲委派模型

​	类加载器首先将加载过程委托给父加载器去加载,父加载器不能加载再自己加载

​	作用:安全, 防止自己写的类覆盖jdk原有的类,实现数据窃取等问题.

​	不同classloader加载的class文件不相等.

​	父加载器 不是 父类加载加载器,他们不是继承关系

​	加载路径 (具体见Lanucher类)  boostrap: sun.boot.class.path;  extension: java.ext.dirs ;  app: java.class.path 

​	自定义类加载器,只需实现findClass方法(模板方法)

​	**待做: 自定义ClassLoader, 实现 Jar文件加密**

![ClassLoader](images/ClassLoader.png)



Java执行过程的三种模式

编译执行: 将class bytecode编译为本地代码执行, 启动慢,执行快,  -XComp

解释执行: 再执行过程中把 bytecode解释为本地代码执行, 启动快,执行慢, -Xint

混合模式: 刚开始解释执行,对热点代码(执行次数多的方法或代码块)编译为本地代码(Hot Spot由来) -Xmixed



new 关键字步骤

1.申请内存空间(赋默认值), 2.赋初始值



### JMM  Java Memory Model  java内存模型

![Snipaste_2020-12-19_22-47-14](images/Snipaste_2020-12-19_22-47-14.png)

数据一致性问题(L1,L2数据不共享)

1.总线锁 (Bus Lock) 效率低

2.缓存一致性协议 (MESI --> 应用于缓存行)

缓存行: 为提高效率, 按整行读取