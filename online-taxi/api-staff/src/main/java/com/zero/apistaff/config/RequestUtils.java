package com.zero.apistaff.config;

import org.apache.http.protocol.RequestContent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/6/18
 */
@Component
public class RequestUtils {
    public static String getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader("userId");
    }
}
