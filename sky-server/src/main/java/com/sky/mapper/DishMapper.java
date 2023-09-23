package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillType;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 查询该菜品是否存在某个分类之下。
     * @param id 分类id
     */
    @Select("select count(*) from dish where category_id = #{id}")
    int getDIshExistCategory(Long id);

    @AutoFill(AutoFillType.INSERT)
    void save(Dish dish);

    List<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @AutoFill(AutoFillType.UPDATE)
    void update(Dish dish);

    void deleteBatch(List<Long> ids);

    List<Dish> queryBatch(List<Long> ids);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);


    @Select("select * from dish where category_id = #{categoryId}")
    List<DishVO> getByCategory(Long categoryId);


}
