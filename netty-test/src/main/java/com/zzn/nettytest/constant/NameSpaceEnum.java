package com.zzn.nettytest.constant;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum NameSpaceEnum {

    MAIN("/main",0),
    COOLING("/cooling",1),
    DIVE("/dive",2),
    FUEL("/fuel",3),
    ENVIRONMENT("/environment",4),
    TRIMMING("/trimming",5),
    FIRE("/fire",6),
    CIRCUIT("/circuit",7),
    FLOW("/flow",8),
    FIRELIST("/firelist",9),
    TEST("/test",10),
    ;

    @Getter
    @Setter
    private String namespace;

    @Getter
    @Setter
    private int code;

    NameSpaceEnum(String namespace, int code) {
        this.namespace = namespace;
        this.code = code;
    }

    public static String getNamespaceByCode(int code){
        NameSpaceEnum[] values = values();
        for (NameSpaceEnum value : values) {
            int code1 = value.getCode();
            if(Objects.equals(code,code1)){
                return value.getNamespace();
            }
        }
        return null;
    }

    public static Integer getCodeByNamespace(String namespace){
        NameSpaceEnum[] values = values();
        for (NameSpaceEnum value : values) {
            String namespace1 = value.getNamespace();
            if(StringUtils.equals(namespace,namespace1)){
                return value.getCode();
            }
        }
        return null;
    }
}