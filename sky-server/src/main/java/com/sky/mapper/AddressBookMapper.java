package com.sky.mapper;


import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    List<AddressBook> list(AddressBook addressBook);

    /**
     * 根据id修改地址
     * @param addressBook
     */
    void modifyById(AddressBook addressBook);


    /**
     *  根据id设置默认地址
     * @param id
     */
    @Update("update address_book set is_default = #{isDefault} where id = #{id}")
    void setDefaultById(AddressBook addressBook);


    /**
     * 新增地址
     * @param addressBook
     */
    void add(AddressBook addressBook);


    /**
     * 根据id删除地址
     * @param id
     */
    void deleteById(Long id);

}
