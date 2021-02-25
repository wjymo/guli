package com.zzn.estest.controller;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionAdviceController {
    @ExceptionHandler(value = Exception.class)
    public Map<String,Object> handle(Exception ex){
        if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            if(CollectionUtils.isNotEmpty(allErrors)){
                ObjectError objectError = allErrors.get(0);
                String defaultMessage = objectError.getDefaultMessage();
                Map map = new HashMap();
                map.put("code", 300);
                map.put("msg", defaultMessage);
                return map;
            }
        }
        ex.printStackTrace();
        Map map = new HashMap();
        map.put("code", 100);
        map.put("msg", ex.getMessage());
        return map;

    }
}
