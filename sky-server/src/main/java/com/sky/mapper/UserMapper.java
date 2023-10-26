package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{opendid}")
    User getByOpenId(String opendid);


    void insert(User newUser);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 用户统计 - 某一天内新增用户
     * @param mapCondition
     * @return
     */
    @Select("select count(*) from user where create_time between #{begin} and #{end}")
    Integer userStatisticsByDay(Map mapCondition);

    /**
     * 用户统计 - 截止某一天内的所有用户
     * @param beginTimeByDay
     * @return
     */
    @Select(" select count(*) from user where create_time < #{beginTimeByDay}")
    Integer userStatisticsByDayTotal(LocalDateTime beginTimeByDay);
}
