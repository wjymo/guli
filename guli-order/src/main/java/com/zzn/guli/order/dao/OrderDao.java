package com.zzn.guli.order.dao;

import com.zzn.guli.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-03 14:17:04
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
