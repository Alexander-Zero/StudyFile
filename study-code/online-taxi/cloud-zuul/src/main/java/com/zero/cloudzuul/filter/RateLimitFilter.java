package com.zero.cloudzuul.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RateLimitFilter extends ZuulFilter {
    public static final RateLimiter RATE_LIMITER = RateLimiter.create(1);

    public static final String CONTINUE_RUN = "continue-run";

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        boolean get = RATE_LIMITER.tryAcquire();
        if (!get) {
            RequestContext.getCurrentContext().setSendZuulResponse(false);
            RequestContext.getCurrentContext().setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
        }
        RequestContext.getCurrentContext().set(CONTINUE_RUN, get);
        return null;
    }
}
