package com.zzn.guli.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzn.guli.common.utils.PageUtils;
import com.zzn.guli.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-03 14:26:01
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

