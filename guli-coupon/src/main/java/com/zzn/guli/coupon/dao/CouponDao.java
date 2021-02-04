package com.zzn.guli.coupon.dao;

import com.zzn.guli.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-03 14:33:57
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
