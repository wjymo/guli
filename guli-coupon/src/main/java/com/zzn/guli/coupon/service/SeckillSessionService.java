package com.zzn.guli.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzn.guli.common.utils.PageUtils;
import com.zzn.guli.coupon.entity.SeckillSessionEntity;

import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-03 14:33:57
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

