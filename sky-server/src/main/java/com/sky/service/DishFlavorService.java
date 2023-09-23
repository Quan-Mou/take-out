package com.sky.service;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.constant.AutoFillType;
import com.sky.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService {

    void saveBatch(List<DishFlavor> dishFlavors);

//    List<DishFlavor> getDishFlavorByDishId(Long dishId);
}
