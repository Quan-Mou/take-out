package com.sky.service.impl;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;


    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        log.info("参数：,{}",addressBook);
        AddressBook address = AddressBook.builder().userId(BaseContext.getCurrentId()).build();
        List<AddressBook> list = addressBookMapper.list(address);
        return list;
    }

    @Override
    public void modifyById(AddressBook addressBook) {
        log.info("参数：,{}",addressBook);
        addressBookMapper.modifyById(addressBook);
    }

    @Override
    public void setDefaultById(Long id) {
//       默认地址只能有一个，在设置默认地址之前，必须把原来的默认地址修改下，在设置。
        AddressBook build = AddressBook.builder().isDefault(1).build();
        List<AddressBook> list = addressBookMapper.list(build);
        AddressBook addressBook = list.get(0);
        addressBook.setIsDefault(0);
        addressBookMapper.setDefaultById(addressBook);
        log.info("设置为默认地址的，{}",list);
        addressBookMapper.setDefaultById(AddressBook.builder().isDefault(1).id(id).build());
    }

    @Override
    public AddressBook getDefaultAddress() {
        AddressBook address = AddressBook.builder().isDefault(1).userId(BaseContext.getCurrentId()).build();
        List<AddressBook> list = addressBookMapper.list(address);
        if (list.size() < 1) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void add(AddressBook addressBook) {
        log.info("参数：,{}",addressBook);
        addressBookMapper.add(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        log.info("参数：,{}",id);
        addressBookMapper.deleteById(id);
    }

    @Override
    public AddressBook getAddressById(Long id) {
        AddressBook address = AddressBook.builder().id(id).userId(BaseContext.getCurrentId()).build();
        List<AddressBook> list = addressBookMapper.list(address);
        return list.get(0);
    }
}
