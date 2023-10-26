package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.sky.result.Result.success;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {


    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .userId(BaseContext.getCurrentId())
                .build();

//        判断该菜品、套餐是否已经存在购物车当中，如果已经存在，只需number+1
        List<ShoppingCart> item = shoppingCartMapper.queryDishOrSetmeal(shoppingCart);
        if(item.size() > 0) {
           log.info("已经存在");
           item.get(0).setNumber(item.get(0).getNumber() + 1);
//           TODO:已经存在，价格也应该相乘，这里还没写
           shoppingCartMapper.update(item.get(0));
           return;
       }

//       判断添加的是菜品还是套餐
        if(shoppingCartDTO.getSetmealId() != null) {
            log.info("添加套餐");
//            根据套餐id去查套餐表
            List<Long> id = Arrays.asList(shoppingCartDTO.getSetmealId());
            List<Setmeal> listByIds = setmealMapper.getListByIds(id);
            shoppingCart.setSetmealId(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(listByIds.get(0).getName());
            shoppingCart.setImage(listByIds.get(0).getImage());
            shoppingCart.setNumber(1);
            BigDecimal number = new BigDecimal(shoppingCart.getNumber());
            BigDecimal amount = number.multiply(listByIds.get(0).getPrice());
            log.info("相乘的套餐价格：{}",amount);
            shoppingCart.setAmount(amount);

        } else {
            log.info("添加菜品");
//            根据菜品id去查菜品表
            Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
            shoppingCart.setDishId(shoppingCartDTO.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
            shoppingCart.setNumber(1);
            BigDecimal number = new BigDecimal(shoppingCart.getNumber());
            BigDecimal amount = number.multiply(dish.getPrice());
            log.info("相乘菜品的价格：{}",amount);
            shoppingCart.setAmount(amount);

        }
        log.info("shoppingCart: {}",shoppingCart);
        shoppingCart.setCreateTime(LocalDateTime.now());
//        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.add(shoppingCart);
    }

    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        List<ShoppingCart> list = shoppingCartMapper.queryDishOrSetmeal(shoppingCart);
        return list;
    }

    @Override
    public void delete(ShoppingCartDTO shoppingCartDTO) {
//       删除之前先判断当前的商品数量，如果当前商品的number大于1，就只对该数据的number-1，否则就删除这条数据
//        根据用户id和
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).dishId(shoppingCartDTO.getDishId()).setmealId(shoppingCartDTO.getSetmealId()).build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.queryDishOrSetmeal(shoppingCart);
        log.info("删除购物车数据：" + shoppingCarts);

//        log.info("提取当个数据：" + shoppingCart1);

        if(shoppingCarts.size()>0) {
            ShoppingCart shoppingCart1 = shoppingCarts.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
            shoppingCartMapper.update(shoppingCart1);
            return;
        }
        shoppingCartMapper.delete(shoppingCartDTO);
    }

    @Override
    public void clear() {
        shoppingCartMapper.clear(BaseContext.getCurrentId());
    }
}
