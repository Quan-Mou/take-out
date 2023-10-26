package com.sky.service;


import com.sky.vo.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 数据统计
 */
public interface ReportService {


    /**
     * 获取营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);


    /**
     * 用户统计
     * @param begin
     * @param end
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO orderStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO salesStatistics(LocalDate begin, LocalDate end);

}
