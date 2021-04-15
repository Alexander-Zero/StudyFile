Zookepper

安装

下载 tar.gz

解压， 

复制zoo_sample.conf -> zoo.conf

编辑

填写节点： 

server.1=ip:port1:port2

server.2=ip:2888:3888

server.3=ip:port1:port2

server.4=ip:port1:port2

设置数据文件夹为/var/zookeeper 需创建

在/var/zookeeper 目录下创建myid， 

vim -> 写入1 ， 即 server.1 的 1 ， 这个是优先级， 创建主的优先级

选主需过半， 刚开始启动可能是3或者4

修改配置文件

export_path 



启动

zkServer.sh start_forehcxcxx

cZxid  创建事务id  前半部分是server id, 后半部分是顺序的事务id

mZxid 更新事务id

pZxid 目录下最后创建的事务id

session 统一视图