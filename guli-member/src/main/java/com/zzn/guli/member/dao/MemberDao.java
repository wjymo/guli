package com.zzn.guli.member.dao;

import com.zzn.guli.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-03 14:26:01
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
