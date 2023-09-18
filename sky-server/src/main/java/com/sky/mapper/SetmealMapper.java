package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {


    @Select("select count(*) from setmeal where id = #{id}")
    int getSetmealExistCategory(Long id);
}