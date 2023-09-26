package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.annotations.Optimistic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sky.result.Result.success;

@RestController()
@Api(tags = "C端购物车相关接口")
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shopingCartService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车：{}",shoppingCartDTO);
        shopingCartService.add(shoppingCartDTO);
        return success();
    }


    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result getShoppingCartList() {
        List<ShoppingCart> list = shopingCartService.list();
        return success(list);
    }


    @PostMapping("/sub")
    @ApiOperation("根据id删除购物车")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shopingCartService.delete(shoppingCartDTO);
        return success();
    }

    @DeleteMapping("/clean")
    @ApiOperation("根据id清空购物车")
    public Result clearShoppingCart() {
        shopingCartService.clear();
        return success();
    }





}
