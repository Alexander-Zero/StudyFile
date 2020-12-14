# Redis

### centos 环境搭建

1.tar.gz文件下载

​	https://redis.io/download

2.解压文件

​	tar -zxvf filename

3.移动到解压目录执行make命令

​	cd redis

​	make

​	make install

4.启动

​	./redis-server  /myredis/redis.conf  //后面参数为配置文件(可不加)

​	后台启动:  修改redis.conf   daemonize no -> daemonize yes // 再次执行启动

​	备注: 需查看redis-server文件位置 ,以前目录: redis/bin/redis-server  ; 现在目录: redis/src/redis-server

​	日志位置:  redis.conf 修改  logfile 

5关闭

​	./redis-cli shutdown  

6.远程连接redis 服务

​	windows下: redis-cli -h hostname -p port -a 

redis命名: 

```
 redis-server        redis服务器
 redis-cli            redis命令行客户端
 redis-benchmark        redis性能测试工具
 redis-check-aof        aof文件修复工具
 redis-check-dump    rdb文件检查工具
```

​	



### 配置文件



### 原理理论

