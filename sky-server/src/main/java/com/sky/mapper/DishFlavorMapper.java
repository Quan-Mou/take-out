package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillType;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface DishFlavorMapper {
    void saveBatch(List<DishFlavor> dishFlavorList);

    @Delete("delete from dish_flavor where dish_id = #{id}")
    void delete(Long id);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getDishById(Long id);
}
