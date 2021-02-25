package com.zzn.estest.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectTest {

    @Pointcut("execution(public * com.zzn.estest.controller.DemoController.index(..))")
    public void pointcut(){}
}
