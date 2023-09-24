package com.sky.controller.admin;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sky.result.Result.success;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理相关接口")
@Slf4j
public class SetmealController {


    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    @ApiOperation("条件分页查询套餐")
    public Result paggQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        Page<SetmealVO> setmealVOS = setmealService.pageQuery(setmealPageQueryDTO);
        return success(new PageResult(setmealVOS.getTotal(),setmealVOS.getResult()));
    }

    @PostMapping()
    @ApiOperation("添加套餐")
    @CacheEvict(value = "setmealCache",key = "#setmealDTO.categoryId",allEntries = true)
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("参数： {}",setmealDTO);
        setmealService.save(setmealDTO);
        return success();
    }

    @DeleteMapping
    @ApiOperation("可批量删除套餐")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result deleteBatchById(@RequestParam List<Long> ids) {
        log.info("删除套餐");
        setmealService.deleteBatch(ids);
        return success();
    }


    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation("修改套餐的状态")
    public Result changeStatus(@PathVariable("status") Integer status,@RequestParam("id") Long id) {
        log.info("修改套餐的状态");
        setmealService.changeStatus(status,id);
        return success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取套餐")
//    @Cacheable(cacheNames = "setmealCache",key = "#id")
    public Result getSetmealById(@PathVariable("id") Long id) {
        SetmealVO setmeal = setmealService.getSetmealById(id);
        return success(setmeal);
    }

    @PutMapping()
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation("修改套餐信息")
    public Result editSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}",setmealDTO);
        setmealService.update(setmealDTO);
        return success();
    }


}
