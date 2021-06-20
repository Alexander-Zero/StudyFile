## Spring Alibaba

### Spring Cloud Gateway

可在网关实现缓存，日志，

~~~yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: api-staff
          uri: lb://api-staff
          predicates:
            - Path=/api-staff/**
#            - Query=id
            - Method=GET
            - RemoteAddr=192.168.2.1/24
          filters:
            - StripPrefix=1

        - id: api-auth-test1
          uri: http://localhost:8866
          predicates:
            - Weight=api-auth, 1
          filters:
            - StripPrefix=1
        - id: api-auth-test2
          uri: http://localhost:8877
          predicates:
            - Weight=api-auth, 5
          filters:
            - StripPrefix=1
#负载均衡
api-auth:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
~~~



#### predicates 

>Path                                =>  请求的路径
>
>Method                          =>  GET/POST/PUT/DELETE
>
>Query                             => 参数
>
>Cookie                            => cookie
>
>Header                           => header
>
>Host                                =>  Header中的host参数
>
>RemoteAddr                  =>  192.168.1.1/24    ,  输入的网址 192.168.1.104/api-auth/test ， 输入地址与掩码与 若=192.168.1.1 为true
>
>Weight                            => 权重
>
>Before,Between,After =>  时间
>
>



#### uri 

> 要转发的地址或服务
>
> 地址： http://localhost:8080
>
> 服务： lb://api-auth



#### filters

StripPrefix=1



#### 自定义路由？？？



#### 自定义过滤器： 

~~~java
@Component
public class AuthFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get("token");

        if (!validateToken(tokens)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            response.setComplete();//结束
            DataBuffer wrap = exchange.getResponse().bufferFactory().wrap("not auth".getBytes());
            return exchange.getResponse().writeWith(Mono.just(wrap));
        }

        return chain.filter(exchange);
    }

    //验证token是否合法
    private boolean validateToken(List<String> tokens) {
        if (null != tokens && tokens.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
~~~



#### 负载均衡：

>1, RandomRule                                     =>  随机
>
>2, RoundRobinRule                              => 轮询
>
>3, RetryRule                                          =>  重试??
>
>4, BestAvailableRule                           => 最低并发策略
>
>5, AvailabilityFilteringRule                 =>  可用过滤策略， 过滤不可用服务（调用失败，超时）
>
>6, WeightedResponseTimeRule       => 相应时间加权
>
>7, ZoneAvoidanceRule                       =>  区域权衡策略

~~~properties
serviceName.ribbon.NFLoadBalancerRuleClassName=com.netflix.loadbalancer.RandomRule
自定义负载均衡器需实现AbstractLoadBalancerRule
~~~





限流

令牌桶算法：

有两个参数， 桶大小， 每秒速率，

可根据不同的角色 ，设置每次可获取的令牌数， 如admin可获取5个， user只获取一个，来控制 资源、网速等

sa

redis实现





















### nacos

服务发现与注册 / 分布式配置中心

**Service registration and discovery**

**Distributed Configuration**









### sentinel

https://github.com/alibaba/Sentinel/wiki/

#### sentinel dashboard 

启动

>java  -jar sentinel-dashboard.jar  -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard 
>
>jdk11需将 -jar sentinel-dashboard.jar放到前面， 不然报错 

#### sentinel

POM

~~~
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
备注：需将SpringBoot置为 2.2.10.RELEASE
~~~



##### 1， 示例

~~~
    public static void main(String[] args) {
        initFlowRules();

        for (int i = 0; i < 20; i++) {

            Thread.sleep(250);

            Entry entry = null;
            try {
                entry = SphU.entry("test");

                //具体业务执行
                System.out.println("得到令牌， 执行成功");

            } catch (BlockException e) {
                //降级
                System.out.println("失败！！！");
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }

        }
    }


    //设置限流规则
    public static void initFlowRules() {
        List<FlowRule> flowRules = new ArrayList<>();

        FlowRule rule = new FlowRule();
        rule.setResource("test");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(2);

        flowRules.add(rule);
        FlowRuleManager.loadRules(flowRules);

    }
~~~

##### 2， 整合web对方法限流

~~~
    @GetMapping("/test")
    @SentinelResource(value = "test", blockHandler = "testBack")
    public String test() {
        return "test";
    }

    public String testBack(BlockException e) {
        return "xxoo";
    }
~~~

##### 3，限流规则由 sentinel dashboard配置

**3.1 代码中的限流规则需要去除，不然无法发送到sentinel dashboard**

3.2 ， properities中加入配置

```
spring.cloud.sentinel.transport.dashboard=localhost:8081
#服务启动之后 直接去注册到dashboard
spring.cloud.sentinel.eager=true
spring.application.name=sentinel
```

##### 4， nacos配置限流规则

4.1，nacos windows 启动 

>.\startup.cmd -m standalone

4.2, 服务拉取nacos的配置

pom

~~~
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
</dependency>
~~~

properties

~~~
spring.cloud.sentinel.datasource.ds.nacos.server-addr=http://localhost:8848
spring.cloud.sentinel.datasource.ds.nacos.dataId=sentinel
spring.cloud.sentinel.datasource.ds.nacos.groupId=DEFAULT_GROUP
spring.cloud.sentinel.datasource.ds.nacos.ruleType=flow
~~~

nacos配置

~~~json
[
     {
        "resource": "test", 
        "limitApp": "default",
        "grade": 2,
        "count": 2,
        "strategy": 0,
        "controlBehavior": 0,
        "clusterMode": false
    }
]
~~~









流量控制，服务熔断， 服务降级

**Flow control and service degradation**

flow control, circuit breaking and system adaptive protection 

### seata

分布式事务

### dubbo

远程调用

### rocketMQ







DependentManagement

~~~
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.alibaba.cloud</groupId>
				<artifactId>
					spring-cloud-alibaba-dependencies
				</artifactId>
				<version>2.2.1.RELEASE</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
~~~

