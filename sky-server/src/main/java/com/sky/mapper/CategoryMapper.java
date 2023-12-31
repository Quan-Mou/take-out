package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillType;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {


    List<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill(AutoFillType.INSERT)
    @Insert("insert into category(type,name,sort,status,create_time,update_time,create_user,update_user)" +
            "values(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    Integer save(Category category);

    @Delete("delete from category where id = #{id}")
    int remove(Long id);


    @AutoFill(AutoFillType.UPDATE)
    void update(Category category);


//    @Select("select * from category where `type` = #{type}")
    List<Category> getListByType(Integer type);


}
