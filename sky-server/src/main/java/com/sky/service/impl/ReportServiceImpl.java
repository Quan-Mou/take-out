package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {


    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;


    @Autowired
    private UserMapper userMapper;




    /**
     * 获取营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        List<String> dateListString = new ArrayList<>();
        dates.add(begin);
        dateListString.add(begin.toString());
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
            dateListString.add(begin.toString());
        }

        List<String> amountList = new ArrayList<>();
        dates.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map= new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.CANCELLED);
            Double amount = orderMapper.getTurnoverByDay(map);
            amount = amount != null ? amount:0.00;
            amountList.add(String.valueOf(amount));
        });
        String turnoverList = String.join(",", amountList);
        String dateList = String.join(",", dateListString);
        return new TurnoverReportVO(dateList,turnoverList);
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
//       处理除了开始和结束中间的时间
        Map map = builderDateList(begin, end);
        List<LocalDate>  dates = (List<LocalDate>) map.get("dates");
        List<String> dateList = (List<String>)map.get("dateListString"); //日期列表

        List<String> userCountList = new ArrayList<>();
        List<String> userByDayCountList = new ArrayList<>();
//     查询某一天内的新增用户
        dates.forEach(date -> {
            LocalDateTime beginTimeByDay = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime endTimeByDay = LocalDateTime.of(date, LocalTime.MIN);

            Map mapCondition = new HashMap();
            mapCondition.put("begin",beginTimeByDay);
            mapCondition.put("end",endTimeByDay);
//           查询某一天内的新增用户
            Integer userCount = userMapper.userStatisticsByDay(mapCondition);
            userCountList.add(String.valueOf(userCount));
//           查询截止到这一天的所有用户
            Integer userByDayCount = userMapper.userStatisticsByDayTotal(beginTimeByDay);
            userByDayCountList.add(String.valueOf(userByDayCount));
        });
        return new UserReportVO(String.join(",",dateList),String.join(",",userByDayCountList),String.join(",",userCountList));
    }

    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        Map map = builderDateList(begin, end);
        List<LocalDate> dates = (List<LocalDate>) map.get("dates");
        List<String> dateListString = (List<String>) map.get("dateListString");

        List<String> orderCountLists = new ArrayList<>();
        List<String> validOrderCountLists = new ArrayList<>();
        dates.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map mapCondition = new HashMap();
            mapCondition.put("begin",beginTime);
            mapCondition.put("end",endTime);
            Integer orderByTime = orderMapper.orderStatistics(map);// 统计订单列表
            orderCountLists.add(String.valueOf(orderByTime));
            mapCondition.put("status",Orders.COMPLETED);
            Integer validOrderByTime = orderMapper.orderStatistics(map);// 统计有效订单列表
            validOrderCountLists.add(String.valueOf(validOrderByTime));
        });

        Integer totalOrderCount = orderMapper.orderCountByStatus(null);// 所有订单数
        Integer validOrderCount = orderMapper.orderCountByStatus(Orders.COMPLETED);// 所有有效订单数
        String orderCountList = String.join(",", orderCountLists); // 订单列表
        String validOrderCountList = String.join(",", validOrderCountLists); // 有效订单列表
        String dateList = String.join(",", dateListString); // 日期列表
        Double orderCompletionRate = (double) (validOrderCount / totalOrderCount * 100); // 订单完成率


        return OrderReportVO.builder()
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCountList(orderCountList)
                .dateList(dateList)
                .validOrderCountList(validOrderCountList)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO salesStatistics(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> goodsSalesDTOS = orderMapper.salesStatistics(begin, end);
        if(goodsSalesDTOS == null) {
            return null;
        }
        log.info("goodsSalesDTOS {}",goodsSalesDTOS);
        List<String> names = new ArrayList<>();
        List<String> numbers = new ArrayList<>();
        log.info("销量列表：{}",goodsSalesDTOS);
        goodsSalesDTOS.forEach(goods -> {
            names.add(goods.getName());
            numbers.add(goods.getNumber().toString());
        });


        return SalesTop10ReportVO.
                builder()
                .nameList(String.join(",",names))
                .numberList(String.join(",",numbers))
                .build();
    }


    /**
     *
     * @param begin
     * @param end
     * @return dates是构建好的日期，localDate类型，dateListString是String类型的LocalDate
     */
    public  Map builderDateList(LocalDate begin,LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        List<String> dateListString = new ArrayList<>();
        dates.add(begin);
        dateListString.add(begin.toString());
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
            dateListString.add(begin.toString());
        }
        Map map = new HashMap();
        map.put("dates",dates);
        map.put("dateListString",dateListString);
        return map;
    }
}
