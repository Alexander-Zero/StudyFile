ELK - FF

配置文件:

discovery.seed_hosts:  配置可能为master的节点, master-enable=true的节点	

elasticSearch集群安装

配置文件

```

```

测试环境内存大小不够, 修改jvm.options

启动, 

非root用户启动

path.data

path.logs

目录创建最好是在有权限的目录下

```
bootstrap check failure [1] of [3]: memory locking requested for elasticsearch process but memory is not locked
=> 

bootstrap check failure [2] of [3]: max number of threads [3028] for user [zero] is too low, increase to at least [4096]
=> vim /etc/security/limit.conf  
=> root hard nproc 4096
=> root soft nproc 4096
=> user hard nproc 4096
=> user soft nproc 4096
=> reboot

bootstrap check failure [3] of [3]: max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
=> vim /etc/sysctl.conf
=> vm.max_map_count=262144
=> sysctl -p
```

错误killed

测试环境内存不够, 修改如下

>1, conf/jvm.options 中修改-Xms -Xmx
>
>2, 启动加参数, ES_JAVA_OPTIONS="-Xms1g -Xmx1g"  ./elasticesearch
>
>3, boostrap.memory_lock= false







elasticsearch启动

ES_JAVA_OPTIONS="-Xms100m -Xmx100m" ./bin/elasticsearch -d 



filebeat启动

./filebeat -e -c ./filebeat.yml

filebeat中 ./data/register中保存了一些读取日志偏移量等

-log 下可定义 

tags: []

json.override.  覆盖时间格式不同不能覆盖, 要么时间格式改成相同的, 或者改名

json.

采集syslog

那些是系统日志? => /etc/rsyslog.conf

syslog不需要写入文件, 再监听文件改变, 直接将文件通过udp或tcp写入filebeats

*. * @@remote:514  => @@127.0.0.1:514

systemctl restart rsyslog

将日志采集封装成jar包.







logstash启动

./bin/logstash -f conf/logstash-sample.yml

可以路由:

if ["zero"] in tags: 