package com.zzn.guli.client.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_TOKEN_TYPE = "Bearer";


    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String token = request.getHeader("Authorization");
            if (StringUtils.isEmpty(token)) {
                token = request.getParameter("access_token");
                if(StringUtils.isNotEmpty(token)){
                    processTokenOnRequest(requestTemplate,token);
                }
            }else {
                processTokenOnRequest(requestTemplate,token);
            }
        }
    }

    private void processTokenOnRequest(RequestTemplate requestTemplate,String token){
        if(StringUtils.startsWithIgnoreCase(token,BEARER_TOKEN_TYPE)){
            requestTemplate.header(AUTHORIZATION_HEADER,  token);
        }else {
            requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, token));
        }
    }
}
