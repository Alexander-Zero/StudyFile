package com.zero.apistaff.aspect;

import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RibbonAspect {

    @Pointcut("execution(* com.zero.apistaff.controller.*Controller*.*(..))")
    public void ribbonPointCut() {
    }

    @Before("ribbonPointCut()")
    public void addFilter(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getHeader("userId");
        System.out.println("xxxx");
        String myName = null;
        //需判断调用的微服务是否开启灰度发布
        //查库 user id =>  version/metadata
        if ("1".equals(userId)) {
            myName = "alex";
        } else if ("2".equals(userId)) {
            myName = "zero";
        }

        if (myName != null) {
            RibbonFilterContextHolder.clearCurrentContext();
            RibbonFilterContextHolder.getCurrentContext().add("myName", myName);
        }
    }
}
