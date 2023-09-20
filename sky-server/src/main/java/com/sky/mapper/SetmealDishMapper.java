package com.sky.mapper;


import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface SetmealDishMapper {

    /**
     * 查询菜品是否关联了套餐
     */
    @Select("select count(*) from setmeal_dish where dish_id = #{dishId}")
    int queryDishInclude(Long dishId);


    void saveDishSetmeal(List<SetmealDish> setmealDishes);

    void deleteByIds(List<Long> ids);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getById(Long id);
}
