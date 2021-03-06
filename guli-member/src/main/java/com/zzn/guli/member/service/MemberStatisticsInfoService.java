package com.zzn.guli.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzn.guli.common.utils.PageUtils;
import com.zzn.guli.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-03 14:26:01
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

