package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    void add(ShoppingCart shoppingCart);

    List<ShoppingCart> queryDishOrSetmeal(ShoppingCart shoppingCart);


    void update(ShoppingCart item);

//    @Select("select * from shopping_cart ")
//    List<ShoppingCart> list();


    @Delete("delete from shopping_cart where dish_id = #{dishId}")
    void delete(ShoppingCartDTO shoppingCartDTO);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void clear(Long userId);
}
