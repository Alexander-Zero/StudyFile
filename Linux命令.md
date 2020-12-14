# Linux命令

### 常用命名

```
解压tar.gz文件
	tar -xzvf 
修改文件名 
	使用移动文件命令 mv fileA fileB 
创建文件夹
	mkdir dirName
创建文件
	touch fileName

```



查找文件/文件夹

whereis

find / -name filename*

systemctl status

systemctl start

systemctl restart

systemctl stop

rpm -ivh 安装

rpm -q 查看

rpm -e 卸载





安装方式区别?

yum 

apt

rpm

tar -xzvf 



### 防火墙 firewall

```
1、开放指定端口
      firewall-cmd --zone=public --add-port=1935/tcp --permanent
       命令含义：
           --zone #作用域
           --add-port=1935/tcp  #添加端口，格式为：端口/通讯协议
           --permanent  #永久生效，没有此参数重启后失效
2、开启防火墙 
    systemctl start firewalld

关闭防火墙
	s


3、重启防火墙 
	firewall-cmd --reload


```



RabbitMQ

下载RabbitMQ (RPM文件) 最新的3.8.9没有rabbitmq.config.example文件

​	https://www.rabbitmq.com/download.html

下载erlang(RPM文件)

​	https://www.erlang.org/downloads

下载两个rpm文件, 

rpm -ivh erlang.file.rpm

yum install -y rabbitmq-server.rpm

开启15672的管理配置 

rabbitmq-plugins enable rabbitmq_management

systemctl start/restart/stop rabbitmq-server

启动报错,查看日志

```
#systemctl status rabbitmq-server.service
rabbitmq-server.service - RabbitMQ broker
Loaded: loaded (/usr/lib/systemd/system/rabbitmq-server.service; disabled; vendor preset: disabled)
Active: activating (auto-restart) (Result: exit-code) since Sat 2020-12-12 10:06:42 CST; 6s ago
Process: 827374 ExecStart=/usr/sbin/rabbitmq-server (code=exited, status=1/FAILURE)
Main PID: 827374 (code=exited, status=1/FAILURE)
```

```
#journalctl -xe
-- Unit rabbitmq-server.service has begun starting up.
Dec 12 10:37:38 Gzzx rabbitmq-server[850438]: Configuring logger redirection
Dec 12 10:37:39 Gzzx rabbitmq-server[850438]: 10:37:39.136 [error]
Dec 12 10:37:39 Gzzx rabbitmq-server[850438]: 10:37:39.138 [error] BOOT FAILED
Dec 12 10:37:39 Gzzx rabbitmq-server[850438]: BOOT FAILED
Dec 12 10:37:39 Gzzx rabbitmq-server[850438]: 10:37:39.138 [error] ===========
Dec 12 10:37:39 Gzzx rabbitmq-server[850438]: ===========
Dec 12 10:37:39 Gzzx rabbitmq-server[850438]: 10:37:39.138 [error] ERROR: distribution port 25672 in use by another node: rabbit@Gzzx //备注:表示端口被占用
Dec 12 10:37:39 Gzzx rabbitmq-server[850438]: ERROR: distribution port 25672 in use by another node: rabbit@Gzzx
Dec 12 10:37:39 Gzzx rabbitmq-server[850438]: 10:37:39.138 [error]
Dec 12 10:37:40 Gzzx rabbitmq-server[850438]: 10:37:40.139 [error] Supervisor rabbit_prelaunch_sup had child prelaunch started with rabbit_prelaunch:run_prelaunch_first_phase() at undefined exit with reason {dist_port_already_used,25672,"ra>
Dec 12 10:37:40 Gzzx rabbitmq-server[850438]: 10:37:40.139 [error] CRASH REPORT Process <0.157.0> with 0 neighbours exited with reason: {{shutdown,{failed_to_start_child,prelaunch,{dist_port_already_used,25672,"rabbit","Gzzx"}}},{rabbit_pre>
Dec 12 10:37:41 Gzzx rabbitmq-server[850438]: {"Kernel pid terminated",application_controller,"{application_start_failure,rabbitmq_prelaunch,{{shutdown,{failed_to_start_child,prelaunch,{dist_port_already_used,25672,\"rabbit\",\"Gzzx\"}}},{r>
Dec 12 10:37:41 Gzzx rabbitmq-server[850438]: Kernel pid terminated (application_controller) ({application_start_failure,rabbitmq_prelaunch,{{shutdown,{failed_to_start_child,prelaunch,{dist_port_already_used,25672,"rabbit","Gzzx"}}},{rabbit>
Dec 12 10:37:41 Gzzx rabbitmq-server[850438]: [1B blob data]
Dec 12 10:37:41 Gzzx rabbitmq-server[850438]: Crash dump is being written to: erl_crash.dump...done
Dec 12 10:37:41 Gzzx systemd[1]: rabbitmq-server.service: Main process exited, code=exited, status=1/FAILURE
Dec 12 10:37:41 Gzzx systemd[1]: rabbitmq-server.service: Failed with result 'exit-code'.
Dec 12 10:37:41 Gzzx systemd[1]: Failed to start RabbitMQ broker.
-- Subject: Unit rabbitmq-server.service has failed
-- Defined-By: systemd
-- Support: https://access.redhat.com/support
-- 
-- Unit rabbitmq-server.service has failed.
-- 
-- The result is failed.
Dec 12 10:37:51 Gzzx systemd[1]: rabbitmq-server.service: Service RestartSec=10s expired, scheduling restart.
Dec 12 10:37:51 Gzzx systemd[1]: rabbitmq-server.service: Scheduled restart job, restart counter is at 74.
-- Subject: Automatic restarting of a unit has been scheduled
-- Defined-By: systemd
-- Support: https://access.redhat.com/support
-- 
-- Automatic restarting of the unit rabbitmq-server.service has been scheduled, as the result for
-- the configured Restart= setting for the unit.
Dec 12 10:37:51 Gzzx systemd[1]: Stopped RabbitMQ broker.
-- Subject: Unit rabbitmq-server.service has finished shutting down
-- Defined-By: systemd
-- Support: https://access.redhat.com/support
-- 
-- Unit rabbitmq-server.service has finished shutting down.
Dec 12 10:37:51 Gzzx systemd[1]: Starting RabbitMQ broker...
-- Subject: Unit rabbitmq-server.service has begun start-up
-- Defined-By: systemd
-- Support: https://access.redhat.com/support
-- 
-- Unit rabbitmq-server.service has begun starting up.
Dec 12 10:37:52 Gzzx rabbitmq-server[850627]: Configuring logger redirection
Dec 12 10:37:52 Gzzx rabbitmq-server[850627]: 10:37:52.927 [error]
Dec 12 10:37:52 Gzzx rabbitmq-server[850627]: 10:37:52.929 [error] BOOT FAILED
Dec 12 10:37:52 Gzzx rabbitmq-server[850627]: BOOT FAILED
Dec 12 10:37:52 Gzzx rabbitmq-server[850627]: 10:37:52.929 [error] ===========
Dec 12 10:37:52 Gzzx rabbitmq-server[850627]: ===========
Dec 12 10:37:52 Gzzx rabbitmq-server[850627]: 10:37:52.929 [error] ERROR: distribution port 25672 in use by another node: rabbit@Gzzx
Dec 12 10:37:52 Gzzx rabbitmq-server[850627]: ERROR: distribution port 25672 in use by another node: rabbit@Gzzx
```

看日志说明端口被占用

执行 netstat -nlp  | grep port

kill -9 pid