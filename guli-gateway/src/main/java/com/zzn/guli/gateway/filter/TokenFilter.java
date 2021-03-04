package com.zzn.guli.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.zzn.guli.common.response.CommonResponse;
import com.zzn.guli.common.response.ResponseUtil;
import com.zzn.guli.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class TokenFilter implements GlobalFilter, Ordered {
    private final static AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final static Set<String> IGNORE_PATTERNS = new HashSet<>();

    static {
        IGNORE_PATTERNS.add("/api/client/test/**");
        IGNORE_PATTERNS.add("/client/test/**");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 获取当前请求路径
        String path = request.getURI().getPath();
        // 查询是否是忽略路径
        boolean ignore = isIgnore(path);
        if (ignore) {
            // 忽略路径直接放行
            log.info("当前路径为" + path + ",是忽略过滤路径,直接放行!");
            return chain.filter(exchange);
        } else {
            log.info("当前路径为" + path + ",不是忽略过滤路径,开始校验!");
            String token = extractToken(request);
            if(StringUtils.isNotEmpty(token)){
                request = exchange.getRequest().mutate().header("Authorization", token).build();
                return chain.filter(exchange.mutate().request(request).build());
            }
        }
        // 返回无权访问信息
        log.info("当前路径为" + path + ",校验失败!当前用户ID没有权限访问!");
        //拦截，提示未授权错误
        DataBuffer buffer = setResponseInfo(response, ResultCode.AUTH_ERROR);
        return response.writeWith(Mono.just(buffer));
    }

    private DataBuffer setResponseInfo(ServerHttpResponse response, ResultCode resultCode) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        CommonResponse commonResponse = ResponseUtil.error(resultCode);
        String json = JSON.toJSONString(commonResponse);
        byte[] responseByte = json.getBytes();
        DataBuffer buffer = response.bufferFactory().wrap(responseByte);
        return buffer;
    }


    private boolean isIgnore(String path) {
        for (String ignorePattern : IGNORE_PATTERNS) {
            boolean match = antPathMatcher.match(ignorePattern, path);
            if (match) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> authorization = request.getHeaders().get("Authorization");
        String token = null;
        if (!CollectionUtils.isEmpty(authorization)) {
            token = authorization.get(0);
        }
        if (token == null) {
            log.debug("Token not found in headers. Trying request parameters.");
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            if(queryParams!=null){
                List<String> access_token = queryParams.get("access_token");
                if(access_token!=null){
                    token = access_token.get(0);
                }
                if (token == null) {
                    log.debug("Token not found in request parameters.  Not an OAuth2 request.");
                }
            }
        }
        if (StringUtils.startsWithIgnoreCase(token, "bearer")) {
//            int length = "bearer ".length();
//            token = token.substring(length);
            token=StringUtils.substringAfter(token,"bearer ");
        }
        return token;
    }
}
