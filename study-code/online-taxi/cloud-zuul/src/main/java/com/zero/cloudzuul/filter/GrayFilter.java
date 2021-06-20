package com.zero.cloudzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jmnarloch.spring.cloud.ribbon.api.RibbonFilterContext;
import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/6/18
 */
@Component
public class GrayFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        boolean aBoolean = RequestContext.getCurrentContext().getBoolean(RateLimitFilter.CONTINUE_RUN);
        System.out.println("gray: " + aBoolean);
        return aBoolean;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //哪些服务需要灰度发布
        if (request.getRequestURI().startsWith("/api-staff")) {
            return null;
        }


        String userId = request.getHeader("userId");
        //先清除
        RibbonFilterContextHolder.clearCurrentContext();
        String myName = null;
        if ("1".equals(userId)) {
            myName = "zero";
        } else if ("2".equals(userId)) {
            myName = "alex";
        }

        if (myName != null) {
            RibbonFilterContextHolder.getCurrentContext().add("myName", myName);
        }
        return null;
    }
}
