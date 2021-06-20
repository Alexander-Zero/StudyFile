package com.zero.cloudzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -100;
    }

    @Override
    public boolean shouldFilter() {
        boolean aBoolean = RequestContext.getCurrentContext().getBoolean(RateLimitFilter.CONTINUE_RUN);
        System.out.println("auth : " + aBoolean);
        return aBoolean;
    }

    @Override
    public Object run() throws ZuulException {
//        RequestContext currentContext = RequestContext.getCurrentContext();
//        currentContext.setSendZuulResponse(false);
//        currentContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        return null;
    }
}
