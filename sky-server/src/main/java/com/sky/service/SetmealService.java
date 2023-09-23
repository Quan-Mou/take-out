package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillType;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {


    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);


    void save(SetmealDTO setmealDTO);

    void deleteBatch(List<Long> ids);

    SetmealVO getSetmealById(Long id);

    void changeStatus(Integer status, Long id);

    void update(SetmealDTO setmealDTO);

    List<SetmealVO> listWithSetmealDish(Long categoryId);

    /**
     * 根据套餐id获取该套餐具体的菜品信息
     * @param id 套餐id
     * @return
     */
    List<DishItemVO> getSetmealOfDishList(Long id);
}
