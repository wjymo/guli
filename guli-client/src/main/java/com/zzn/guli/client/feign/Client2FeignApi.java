package com.zzn.guli.client.feign;

import com.zzn.guli.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient("guli-client2")
public interface Client2FeignApi {
    @RequestMapping("/client2/api/api1")//注意写全优惠券类上还有映射//注意我们这个地方不熟控制层，所以这个请求映射请求的不是我们服务器上的东西，而是nacos注册中心的
    R api1();//得到一个R对象
}