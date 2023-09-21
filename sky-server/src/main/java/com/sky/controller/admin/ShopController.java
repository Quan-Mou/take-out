package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import static com.sky.result.Result.success;

@RestController
@Api(tags = "店铺相关接口")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {


    private final String SHOP_STATUS = "shop_status";

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;


    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getShopStatus() {
        Integer shopStatus = redisTemplate.opsForValue().get(SHOP_STATUS);
        log.info("店铺状态：{}",shopStatus == 1? "营业中" : "打烊了");
        return success(shopStatus);
    }


    @PutMapping("/{status}")
    public Result setShopStatus(@PathVariable Integer status) {
      log.info("店铺状态：{}",status == 1? "营业中" : "打烊了");
      redisTemplate.opsForValue().set(SHOP_STATUS,status);
      return success();
    }

}
