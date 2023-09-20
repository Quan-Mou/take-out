package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import io.swagger.models.auth.In;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.util.List;

public interface CategoryService {


    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    Integer save(CategoryDTO categoryDTO);

    void remove(Long id);

    void update(CategoryDTO categoryDTO);


    void changeStatus(Integer stats, Long id);

    List<Category> getListByType(Integer type);
}
