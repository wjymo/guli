package com.zzn.nettytest.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class MsgLog {
    private Long id;
    private String content;
    private Integer status;
    private Integer retries;
    private Date updateTime;
}
