Kafka

Kafka, 

.log文件是普通IO读写

.index && .timeindex 是mmap, 需预分配

问：维萨.log不用mmap? 为啥.index要用mmap

lsof -Pnp pid,

mem是mmap

reg是普通文件



kafka-dump-log.sh  --files 00000.index

高水位,低水位                    生产是生产, 消费是消费



sendfile零拷贝???

