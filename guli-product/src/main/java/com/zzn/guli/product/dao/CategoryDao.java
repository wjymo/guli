package com.zzn.guli.product.dao;

import com.zzn.guli.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-02-02 14:09:43
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
