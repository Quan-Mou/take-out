package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

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
    void setDefaultById(Long id);


    AddressBook getDefaultAddress();


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

    /**
     * 根据id获取地址
     * @param id
     * @return
     */

    AddressBook getAddressById(Long id);
}
