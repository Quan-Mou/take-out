package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

public interface CategoryService {


    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    Integer save(CategoryDTO categoryDTO);

    void remove(Long id);

    void update(CategoryDTO categoryDTO);


    void changeStatus(Integer stats, Long id);
}
