package com.sky.controller.user;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sky.result.Result.success;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端地址相关接口")
@Slf4j
public class AddressBookController {



    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    @ApiOperation("根据用户id，获取所有地址信息")
    public Result list() {
        List<AddressBook> list = addressBookService.list(null);
        return success(list);
    }


    @GetMapping("/default")
    @ApiOperation("获取默认地址")
    public Result getDefaultAddress() {
        AddressBook defaultAddress = addressBookService.getDefaultAddress();
        if(defaultAddress == null) {
            return Result.error("请添加一个收货地址");
        }
        return success(defaultAddress);
    }

    @PutMapping("/default")
    @ApiOperation("根据id设置默认地址")
    public Result setDefaultAddressBook(@RequestBody AddressBook addressBook) {
        log.info("id: {}",addressBook);
        addressBookService.setDefaultById(addressBook.getId());
        return null;
    }


    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result modifyAddressById(@RequestBody AddressBook addressBook) {
        addressBookService.modifyById(addressBook);
        return success();
    }


    @GetMapping("/{id}")
    @ApiOperation("根据id获取地址")
    public Result getAddressById(@PathVariable Long id) {
        AddressBook addressById = addressBookService.getAddressById(id);
        return success(addressById);
    }


    @PostMapping
    @ApiOperation("新增地址")
    public Result addAddressBook(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookService.add(addressBook);
        return success();
    }


    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteAddressById(Long id) {
        addressBookService.deleteById(id);
        return success();
    }





}
