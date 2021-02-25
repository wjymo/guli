package com.zzn.estest.config;

import com.zzn.estest.util.TestInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {
    @Bean
    public String myTestInterceptor(SqlSessionFactory sqlSessionFactory){
        TestInterceptor testInterceptor=new TestInterceptor();
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.addInterceptor(testInterceptor);
        return "success";
    }
}
