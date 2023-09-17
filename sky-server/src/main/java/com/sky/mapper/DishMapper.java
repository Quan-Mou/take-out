package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 查询该菜品是否存在某个分类之下。
     * @param id 分类id
     */
    @Select("select count(*) from dish where category_id = #{id}")
    int getDIshExistCategory(Long id);
}
