package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.sky.result.Result.success;

@RestController
@Api(tags = "公共接口")
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private DishService dishService;
    @PostMapping("/upload")
    @ApiOperation("图片上传")
    public Result upload(MultipartFile file) {
        String upload = dishService.upload(file);
        return Result.success(upload);
    }


}
