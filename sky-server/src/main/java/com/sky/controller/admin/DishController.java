package com.sky.controller.admin;


import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sky.result.Result.success;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {


    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @PostMapping("")
    @ApiOperation("新增菜品")
    public Result<?> save(@RequestBody DishDTO dishDTO) {
        dishService.save(dishDTO);
        return success();
    }


    @GetMapping("/page")
    @ApiOperation("条件分页查询菜品")
    public Result<PageResult> list(DishPageQueryDTO dishPageQueryDTO) {
        Page<DishVO> page = dishService.pageQuery(dishPageQueryDTO);
        return success(new PageResult(page.getTotal(),page.getResult()));
    }

    @PostMapping("/status/{status}")
    @ApiOperation("禁用、开启菜品状态")
    public Result<?> changeStatus(@PathVariable("status") Integer status,Long id) {
        dishService.changeStatus(status,id);
        return success();
    }

    @DeleteMapping()
    @ApiOperation("根据id批量删除")
    public Result<?> deleteBatch(@RequestParam("ids") List<Long> ids) {
        dishService.deleteBatch(ids);
        return success();
    }


    @PutMapping("")
    @ApiOperation("修改菜品信息")
    public Result<?> editDish(@RequestBody DishDTO dishDto) {
        dishService.update(dishDto);
        return success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取菜品信息")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dish = dishService.getById(id);
        return success(dish);
    }


    @GetMapping("list")
    @ApiOperation("根据菜品分类id获取菜品信息")
    public Result<List<DishVO>> getListById(Long categoryId) {
        List<DishVO> dishs = dishService.getByCategory(categoryId);
        return success(dishs);
    }



}
