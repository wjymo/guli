package com.zzn.guli.auth.config;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("coupon-server")
                .secret(passwordEncoder().encode("123"))
                .scopes("read","write")
                .resourceIds("coupon")
                .authorizedGrantTypes("client_credentials", "refresh_token", "password", "authorization_code")
                .accessTokenValiditySeconds(20 * 60)
                .refreshTokenValiditySeconds(60*60)
                .and()
                .withClient("client-server")
                .secret(passwordEncoder().encode("123"))
                .scopes("read","write")
                .resourceIds("client")
                .authorizedGrantTypes("client_credentials", "refresh_token", "password", "authorization_code")
                .accessTokenValiditySeconds(20 * 60)
                .refreshTokenValiditySeconds(60*60)
                .and()
                .withClient("client2-server")
                .secret(passwordEncoder().encode("123"))
                .scopes("read","write")
                .resourceIds("client2")
                .authorizedGrantTypes("client_credentials", "refresh_token", "password", "authorization_code")
                .accessTokenValiditySeconds(20 * 60)
                .refreshTokenValiditySeconds(60*60)
        ;
    }
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));
        endpoints
                .authenticationManager(authenticationManager)
//                .userDetailsService(userDetailsService())
                .tokenStore(tokenStore())
                .tokenEnhancer(enhancerChain)
                .accessTokenConverter(jwtAccessTokenConverter());
    }
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }
    @Bean
    public  JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("333");
        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer(){
        TokenEnhancer tokenEnhancer = (oAuth2AccessToken, oAuth2Authentication) -> {
            Object principal = oAuth2Authentication.getPrincipal();
            User user = (User) principal;
            String username = user.getUsername();
            Collection<GrantedAuthority> authorities = user.getAuthorities();
            final Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("username",username);
            additionalInfo.put("authorities", Joiner.on(",").join(authorities));
            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
            return oAuth2AccessToken;
        };
        return tokenEnhancer;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        InMemoryUserDetailsManagerConfigurer userDetailsManagerConfigurer=new InMemoryUserDetailsManagerConfigurer();
//        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//        userDetailsManagerConfigurer
//        return inMemoryUserDetailsManager;
//    }
}
