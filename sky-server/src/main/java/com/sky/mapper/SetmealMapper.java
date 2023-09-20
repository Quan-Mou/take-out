package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillType;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {


    @Select("select count(*) from setmeal where id = #{id}")
    int getSetmealExistCategory(Long id);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(AutoFillType.INSERT)
    void save(Setmeal setmeal);

    void deleteBatch(List<Long> ids);

    /**
     * 根据ids获取套餐信息
     */
    List<Setmeal> getListByIds(List<Long> ids);

    @AutoFill(AutoFillType.UPDATE)
    void update(Setmeal setmealStatus);
}
