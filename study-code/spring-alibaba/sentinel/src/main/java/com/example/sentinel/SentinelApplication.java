package com.example.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SentinelApplication {


    public static void main(String[] args) {
//        initFlowRules();
        SpringApplication.run(SentinelApplication.class, args);
    }

//    public static void main(String[] args) {
//        initFlowRules();
//
//        for (int i = 0; i < 20; i++) {
//
//            Thread.sleep(250);
//
//            Entry entry = null;
//            try {
//                entry = SphU.entry("test");
//
//                //具体业务执行
//                System.out.println("得到令牌， 执行成功");
//
//            } catch (BlockException e) {
//                //降级
//                System.out.println("失败！！！");
//            } finally {
//                if (entry != null) {
//                    entry.exit();
//                }
//            }
//
//        }
//    }


    //加载资源
    public static void initFlowRules() {
        List<FlowRule> flowRules = new ArrayList<>();

        FlowRule rule = new FlowRule();
        rule.setResource("test");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(2);

        flowRules.add(rule);
        FlowRuleManager.loadRules(flowRules);

    }
}
