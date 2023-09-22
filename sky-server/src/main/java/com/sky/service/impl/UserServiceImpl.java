package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.BaseException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserSservice;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserSservice {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    public User login(UserLoginDTO code) {

        String url = "https://api.weixin.qq.com/sns/jscode2session";

        HashMap<String, String> paramter = new HashMap<>();
        paramter.put("appid",weChatProperties.getAppid());
        paramter.put("secret",weChatProperties.getSecret());
        paramter.put("js_code",code.getCode());
        paramter.put("grant_type","authorization_code");

//        根据发送过来的code登录凭证，发送wx.login请求
        String result = HttpClientUtil.doGet(url, paramter);
        JSONObject jsonObject = JSON.parseObject(result);
        String openid = (String) jsonObject.get("openid");
        if (Objects.isNull(openid)) {
            throw new BaseException("登录失败");
        }
//       如果返回null说明没有这个用户，就自动注册，否则就返回查到的用户
        User user = userMapper.getByOpenId(openid);
        if(user != null) {
            return user;
        }

//      自动注册
        User userInfo = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
        log.info("注册之前：{}",userInfo);
//        TODO:现在暂时添加openid和create_time两个字段
        userMapper.insert(userInfo);
        log.info("注册之后：{}",userInfo);
        return userInfo;
    }


}
