package com.zzn.nettytest.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class MessageInfo {
    private Long id;
    //源客户端命名空间
    private String namespace;

    private String content;

    private Integer flow;
    private Integer fire;
    private Integer can;

    private String createDate;
}
