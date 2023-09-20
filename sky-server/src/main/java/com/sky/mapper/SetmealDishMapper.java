package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface SetmealDishMapper {

    /**
     * 查询菜品是否关联了套餐
     */
    @Select("select count(*) from setmeal_dish where dish_id = #{dishId}")
    int queryDishInclude(Long dishId);




}
