## MySQL

## 基础知识

## Mysql基础知识

### 数据存储位置

Linux存储位置: /var/lib/mysqll

windows存放位置:　C:\ProgramData\MySQL\MySQL Server 8.0\Data

### 数据库设计三范式

目的: 减少数据冗余(但可能存在表关联效率低)

1st :  列不可分: 所有字段值都是不可分的原子值(例如省/市/县 应分为三列)  

2nd:  列必须直接依赖主键(不能将多种数据保存在同一张表中, 如学生和教师的信息都保存在同一张表中)

3rd:  传递依赖: 表里面的列不能包含其他表的非主键字段

### mysql架构

![image-20210124224610354](images/image-20210124224610354.png)

连接器: show processlist 可查看当前连接数, 连接可分为长连接和短链接, jdbc时短连接, 线程池时长连接, 建议用长连接

查询缓存: 不推荐使用,  mysql8版本已没有废弃查询缓存,原因: 1:频繁失效,只要有表更新就失效, 2: 命中率低.    

分析器: 语法和词法分析, 生产AST(抽象语法树).  CalCite

优化器: 有多个索引时觉得使用哪个索引, 使用Join连接时决定连接顺序, 总之mysql使用它觉得最优的方式来执行, RBO: 基于规则的优化, CBO: 基于成本的优化

存储引擎:  不同的存放位置, 不同的文件格式, 主要的存储引擎有innodb (.frm,idb,索引和数据放一起存放) ,  myisam(.frm, .myd, .myi ,索引与数据分开), memory(内存, 无法持久化)

### 数据库事务

备注：InnoDB支持事务, MyISAM不支持事务

定义: 一个最小的不可再分的工作单元；通常一个事务对应一个完整的业务

事务四大特征: ACID

atomicity: 原子性 , undoLog来保证

consistency: 一致性  A I D 共同来实现. 最重要

isolation: 隔离性 , read uncommitted (读未提交, 脏读), read committed (读提交, 不可重复读) , repeatable read(可重读读, 幻读) , serializable串行执行, 默认:repeateble read, 通过锁机制来实现 

durability: 持久性 redoLog来保证



### 索引



### 锁机制

不同存储引擎支持不同的锁机制.MyISAM 和Memory采用表锁, InnoDB采用表锁和行锁. 默认行锁

表级锁: 开销小, 加锁快, 不会出现死锁, 锁定粒度大, 发生锁冲突较高, 并发度最低 

行级锁: 开销大, 加锁慢, 会出现死锁, 锁定粒度小, 发生锁重读教低, 并发度最高 

表锁场景: 查询为主, 少量索引条件更新的应用, 如web应用.??

行锁场景: ??

#### MyISAM锁

表共享读锁:

表独占写锁: 

#### InnoDB锁

共享锁:

排他锁:







## mysql 调优



<<<<<<< HEAD
=======
undolog: 原子性保证



## 性能调优



## 疑问

动态横表转纵表

case column_name when value then value2 end 

举例: 

–1.学生表 
Student(s_id,s_name,s_birth,s_sex) –学生编号,学生姓名, 出生年月,学生性别 
–2.课程表 
Course(c_id,c_name,t_id) – –课程编号, 课程名称, 教师编号 
–3.教师表 
Teacher(t_id,t_name) –教师编号,教师姓名 
–4.成绩表 
Score(s_id,c_id,s_score) –学生编号,课程编号,分数

如何查询 学生及各科成绩, 科目名的列 根据 课程表的中的数据动态来确定

>>>>>>> 978e5bcd22e8227c58b5cef3be5e08c67d8345cc
