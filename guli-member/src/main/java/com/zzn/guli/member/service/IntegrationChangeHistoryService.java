package com.zzn.guli.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzn.guli.common.utils.PageUtils;
import com.zzn.guli.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-03 14:26:01
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

