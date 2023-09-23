package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;


    @Override
    public Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
//        根据套餐分类id，去分类表中查询分类名，返回SetmealVO
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> setmealVOPage = setmealMapper.pageQuery(setmealPageQueryDTO);

//        categoryMapper.

        log.info("总页数 ： {} {}",setmealVOPage.getTotal(),setmealVOPage.getResult());
        return setmealVOPage;
    }

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
//       添加套餐，把SetmealDTO的SetmealDishes数据添加到setmeal_dish表中,
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.save(setmeal);
//      菜品对应的套餐（setmeal_dish）
        log.info("setmeal id: {}",setmeal.getId());
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.stream().peek(item -> {
            item.setSetmealId(setmealId);
        }).count();
        log.info("菜品：{}",setmealDishes);
//      添加套餐的菜品
        setmealDishMapper.saveDishSetmeal(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        List<Setmeal> listByIds = setmealMapper.getListByIds(ids);
        listByIds.stream().forEach(item -> {
            if(item.getStatus() == 1) {
                throw new BaseException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        // 删除套餐时，同时也应该删除套餐菜品的套餐数据
        setmealMapper.deleteBatch(ids); //删除套餐
        setmealDishMapper.deleteByIds(ids); //删除套餐菜品
    }

    @Override
    public SetmealVO getSetmealById(Long id) {
        List<Long> setmealId = new ArrayList<>();
        setmealId.add(id);
        List<Setmeal> listByIds = setmealMapper.getListByIds(setmealId);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(listByIds.get(0),setmealVO);
//      获取该套餐下的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.getById(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Setmeal setmealStatus = Setmeal.builder().status(status).id(id).build();
        setmealMapper.update(setmealStatus);
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
//      修改套餐，先把原来的套餐菜品删除，在重新添加新的套餐菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
//        删除原先套餐菜品
        List<Long> ids = Arrays.asList(setmealDTO.getId());
        setmealDishMapper.deleteByIds(ids);
        if (setmealDishes.size() > 0) {
            setmealDishes.stream().forEach(item -> {
                item.setSetmealId(setmealDTO.getId());
            });
//          添加新的套餐菜品
             setmealDishMapper.saveDishSetmeal(setmealDishes);
        }
        setmealMapper.update(setmeal);
    }

    @Override
    public List<SetmealVO> listWithSetmealDish(Long categoryId) {
        List<SetmealVO> list = setmealMapper.list(categoryId);
        log.info("list before：{}",list);
        for(SetmealVO item : list) {
//          查询该套餐下的所以菜品
            List<SetmealDish> setmealDishes = setmealDishMapper.getById(item.getId());
            item.setSetmealDishes(setmealDishes);
        }
        log.info("list after：{}",list);
        return list;
    }

    @Override
    public List<DishItemVO> getSetmealOfDishList(Long id) {
        List<DishItemVO> setmealDetail = setmealMapper.getSetmealDetail(id);
        return setmealDetail;
    }
}
