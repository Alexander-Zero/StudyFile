//package com.zero.apistaff.filter;
//
//import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
//
//@Component
//public class GrayFilter  implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        String userId = request.getHeader("userId");
//
//        String myName = null;
//        //需判断调用的微服务是否开启灰度发布
//        //查库 user id =>  version/metadata
//        if ("1".equals(userId)) {
//            myName = "alex";
//        } else if ("2".equals(userId)) {
//            myName = "zero";
//        }
//
//        if(myName != null){
//            RibbonFilterContextHolder.clearCurrentContext();
//            RibbonFilterContextHolder.getCurrentContext().add("myName",myName);
//        }
//        filterChain.doFilter(servletRequest,servletResponse);
//    }
//
//}
