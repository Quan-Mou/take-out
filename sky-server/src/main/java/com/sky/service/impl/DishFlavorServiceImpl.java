package com.sky.service.impl;

import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishFlavorServiceImpl implements DishFlavorService {


    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public void saveBatch(List<DishFlavor> dishFlavors) {
        dishFlavorMapper.saveBatch(dishFlavors);
    }

//    @Override
//    public List<DishFlavor> getDishFlavorByDishId(Long dishId) {
////        dishFlavorMapper.get
//        return null;
//    }
}
