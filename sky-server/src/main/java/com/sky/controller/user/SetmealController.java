package com.sky.controller.user;


import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetmealController")
@Api(tags = "C端套餐相关接口")
@RequestMapping("/user/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;



    @GetMapping("/list")
    @ApiOperation("根据分类id获取对应套餐")
    public Result list(@RequestParam Long categoryId) {
        List<SetmealVO> setmealVOS = setmealService.listWithSetmealDish(categoryId);
        return Result.success(setmealVOS);
    }



    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查看套餐菜品")
    public Result getSetmealDishInfo(@PathVariable Long id) {
//        拿着套餐id去查套餐菜品表查询菜品，拿到菜品id去菜品表查菜品具体信息
        List<DishItemVO> setmealOfDishList = setmealService.getSetmealOfDishList(id);
        return Result.success(setmealOfDishList);
    }

}
