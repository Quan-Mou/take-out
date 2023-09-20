package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.BaseException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.properties.QiniuOssProperties;
import com.sky.service.DishService;
import com.sky.utils.QiniuOssUtil;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private QiniuOssProperties qiniuOssProperties;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public String upload(MultipartFile file) {
        String upload = QiniuOssUtil.upload(qiniuOssProperties, file);
        return upload;
    }

    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
//        添加菜品，和菜品口味，两次sql操作，保证事务安全
//        1.添加菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.save(dish);
        System.out.println();
        if(dishDTO.getFlavors().size() > 0) {
            for(DishFlavor item: dishDTO.getFlavors()) {
                item.setDishId(dish.getId());
            }
//        2.添加菜品口味
            dishFlavorMapper.saveBatch(dishDTO.getFlavors());
        }
    }

    @Override
    public Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        Page<DishVO> pageData = PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //       根据菜品分类id，去分类表查询对应的分类名称，填充到DishVO的categoryName中(多表查询)
        dishMapper.pageQuery(dishPageQueryDTO);
        return pageData;
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
//        查询当前要删除的菜品状态是否是启用状态
        List<Dish> deleteDishs = dishMapper.queryBatch(ids);
        System.out.println("ids.size():" + ids.size());
        for(Dish item : deleteDishs) {
            if(item.getStatus() == 1) {
                throw new BaseException("要删除的菜品名" + item.getName() + "状态为启用，请停用后再删除");
            }
//        查询当前要删除的菜品是否关联套餐
            if(setmealDishMapper.queryDishInclude(item.getId()) > 0) {
                throw new BaseException("要删除的菜品名" + item.getName() + "关联了套餐");
            }
//        当前要删除的菜品对应的菜品口味数据也要被删除
            dishFlavorMapper.delete(item.getId());
        }
        dishMapper.deleteBatch(ids);
    }

    /**
     * 修改菜品，根据id
     */
    @Override
    @Transactional
    public void update(DishDTO dishDto) {
        System.out.println("修改菜品：" + dishDto);
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish);
        dishMapper.update(dish);
        List<DishFlavor> flavors = dishDto.getFlavors();

        if(flavors.size() > 0) {
//          先删除菜品原有口味，在新增现有的口味数据
            dishFlavorMapper.delete(dishDto.getId());
            for(DishFlavor item : flavors) {
                item.setDishId(dishDto.getId());
            }
            dishFlavorMapper.saveBatch(flavors);
        }





    }

    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getDishById(id);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }
}
