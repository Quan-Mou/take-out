package com.sky.controller.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {


    @Autowired
    private ReportService reportService;



    /**
     * 营业额统计
     * @return
     */

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result turnoverStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("来单营业额统计");
        log.info("传递的日期数据：{},{}",begin,end);
        TurnoverReportVO turnover = reportService.getTurnover(begin, end);
        return Result.success(turnover);
    }


    /**
     * 用户统计
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result userStatistics(
           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
            ) {
        return Result.success(reportService.userStatistics(begin, end));
    }


//    }


    /**
     * 订单统计
     * @return
     */
    @GetMapping("/ordersStatistics")
    public Result orderStatistics(
           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        return Result.success(reportService.orderStatistics(begin,end));
    }


    /**
     * 销量Top10统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("top10")
    public Result salesStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {

        return Result.success(reportService.salesStatistics(begin,end));
    }
}
