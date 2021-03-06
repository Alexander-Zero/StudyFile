//package com.zero.apistaff.config;
//
//import com.netflix.client.config.IClientConfig;
//import com.netflix.loadbalancer.AbstractLoadBalancerRule;
//import com.netflix.loadbalancer.ILoadBalancer;
//import com.netflix.loadbalancer.Server;
//import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
//import org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList;
//
//import java.security.spec.ECField;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
///**
// * @author Alexander Zero
// * @version 1.0.0
// * @date 2021/6/18
// */
//
//public class GrayRule extends AbstractLoadBalancerRule {
//
//    @Override
//    public void initWithNiwsConfig(IClientConfig clientConfig) {
//    }
//
//    @Override
//    public Server choose(Object key) {
//        return choose(getLoadBalancer(), key);
//    }
//
//    private Server choose(ILoadBalancer loadBalancer, Object key) {
//        List<Server> servers = loadBalancer.getAllServers();
//        String userId = RequestUtils.getUserId();
//        String myName = null;
//        //需判断调用的微服务是否开启灰度发布
//        //查库 user id =>  version/metadata
//        if ("1".equals(userId)) {
//            myName = "alex";
//        } else if ("2".equals(userId)) {
//            myName = "zero";
//        }
//
//
//        if (myName != null) {
//            for (Server server : servers) {
//                if (server.isReadyToServe() && server.isAlive()) {
//                    Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
//                    String metaMyName = metadata.get("myName");
//                    if (myName.equals(metaMyName)) {
//                        return server;
//                    }
//                }
//            }
//        }
//        return servers.get(new Random().nextInt(servers.size()));
//    }
//}
