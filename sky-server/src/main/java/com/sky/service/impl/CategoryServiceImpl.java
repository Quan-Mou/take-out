package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import org.apache.catalina.util.ToStringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        Page<Category> page = PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public Integer save(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .createTime(LocalDateTime.now())
                .createUser(BaseContext.getCurrentId())
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
//              添加的分类默认是禁用的
                .status(0)
                .build();
        BeanUtils.copyProperties(categoryDTO,category);
        Integer count = categoryMapper.save(category);
        return count;
    }

    @Override
    public void remove(Long id) {
        /**
         * 先判断该分类或者套餐下是否有菜品，如果没有就允许删除，否则不允许，返回提示信息
         */
        int dishCount = dishMapper.getDIshExistCategory(id);
        if(dishCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        int setmealCount = setmealMapper.getSetmealExistCategory(id);
        if(setmealCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.remove(id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .updateUser(BaseContext.getCurrentId())
                .updateTime(LocalDateTime.now())
                .build();
        BeanUtils.copyProperties(categoryDTO,category);
        System.out.println("修改分类："  + category);
        categoryMapper.update(category);
    }

    @Override
    public void changeStatus(Integer stats, Long id) {
        Category category = Category.builder()
                .status(stats)
                .id(id).build();
        categoryMapper.update(category);
    }
}
