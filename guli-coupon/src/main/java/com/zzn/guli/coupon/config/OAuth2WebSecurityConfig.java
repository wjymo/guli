package com.zzn.guli.coupon.config;

import com.alibaba.fastjson.JSON;
import com.zzn.guli.common.response.CommonResponse;
import com.zzn.guli.common.response.ResponseUtil;
import com.zzn.guli.common.response.ResultCode;
import com.zzn.guli.coupon.utils.SpringContextUtils;
import com.zzn.guli.coupon.security.TokenAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Configuration
@EnableWebSecurity
public class OAuth2WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Value("${zzn.auth.clientId}")
//    private String clientId;
//
//    @Value("${zzn.auth.clientSecret}")
//    private String clientSecret;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/coupon/coupon/**")
                .permitAll() //放过/haha不拦截
                .anyRequest().authenticated();//其余所有请求都拦截


        String jwtKeyByAuth = null;
        Environment environment = SpringContextUtils.getEnvironment();
        String clientId = environment.getProperty("zzn.auth.clientId");
        String clientSecret = environment.getProperty("zzn.auth.clientSecret");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64Utils.encodeToString((clientId + ":" + clientSecret).getBytes()));
        ResponseEntity<Map> exchange = null;
        try {
            exchange = restTemplate.exchange("http://localhost:20000/oauth/token_key",
                    HttpMethod.GET, new HttpEntity<>(null, headers), Map.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new RuntimeException("没拿到jwtKek，项目启动失败");
        }

        boolean needExit = false;
        if (exchange != null) {
            HttpStatus statusCode = exchange.getStatusCode();
            if (Objects.equals(HttpStatus.OK.value(), statusCode.value())) {
                Map body = exchange.getBody();
                if (body != null && !body.isEmpty()) {
                    jwtKeyByAuth = (String) body.get("value");
                } else {
                    needExit = true;
                }
            } else {
                needExit = true;
            }
        } else {
            needExit = true;
        }
        if (needExit) {
            throw new RuntimeException("没拿到jwtKek，项目启动失败");
        }

        http.addFilterBefore(new TokenAuthenticationFilter(jwtKeyByAuth),
                UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().accessDeniedHandler((req, resp, ex) -> {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/json");
            CommonResponse commonResponse = ResponseUtil.error(ResultCode.ACCESS_ERROR);
            String json = JSON.toJSONString(commonResponse);
            resp.getWriter().print(json);
        });
        http.exceptionHandling().authenticationEntryPoint((req, resp, ex) -> {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/json");
            CommonResponse commonResponse = ResponseUtil.error(ResultCode.AUTH_ERROR);
            String json = JSON.toJSONString(commonResponse);
            resp.getWriter().print(json);
        });
    }


    /**
     * 通过这个Bean，去远程调用认证服务器，验token
     * @return
     */
//    @Bean
//    public ResourceServerTokenServices tokenServices(){
//        RemoteTokenServices tokenServices = new RemoteTokenServices();
//        tokenServices.setClientId(clientId);//在认证服务器配置的，订单服务的clientId
//        tokenServices.setClientSecret(clientSecret);//在认证服务器配置的，订单服务的ClientSecret
//        tokenServices.setCheckTokenEndpointUrl("http://localhost:20000/oauth/check_token");
//        return tokenServices;
//    }


    /**
     * 要认证跟用户相关的信息，一般用 AuthenticationManager
     * 覆盖这个方法，可以将AuthenticationManager暴露为一个Bean
     *
     * @return
     * @throws Exception
     */
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        OAuth2AuthenticationManager authenticationManager = new OAuth2AuthenticationManager();
//        authenticationManager.setTokenServices(tokenServices());//设置为自定义的TokenServices，去校验令牌
//        return authenticationManager;
//    }


}
