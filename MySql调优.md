## MySql

## 基础知识

mysql架构:







ACID: 原子性, 一致性, 持久性, 隔离性 

日志:

binlog:  归档文件

redolog:  持久性保证

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

