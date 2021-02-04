package com.zzn.guli.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzn.guli.common.utils.PageUtils;
import com.zzn.guli.order.entity.OrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-03 14:17:04
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

