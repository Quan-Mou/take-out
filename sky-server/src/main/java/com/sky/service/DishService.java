package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DishService {
    /**
     * 上传菜品图片
     */
    String upload(MultipartFile file);

    void save(DishDTO dishDTO);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    void changeStatus(Integer status, Long id);


    void deleteBatch(List<Long> ids);


    void update(DishDTO dish);

    DishVO getById(Long id);
}
