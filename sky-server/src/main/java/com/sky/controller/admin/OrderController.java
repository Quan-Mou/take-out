package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("admin/order")
@RestController("adminOrderController")
@Slf4j
public class OrderController {


    @Autowired
    private OrderService orderService;



    @GetMapping("/conditionSearch")
    @ApiOperation("获取所有订单或者条件查询订单")
    public Result orderAllorConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        return Result.success(orderService.getAllorConditionSearch(ordersPageQueryDTO));
    }


    @GetMapping("/statistics")
    @ApiOperation("各个状态下的订单统计")
    public Result countStatus() {
        OrderStatisticsVO orderStatisticsVO = orderService.countStatus();
        return Result.success(orderStatisticsVO);
    }


    @GetMapping("/details/{id}")
    @ApiOperation("根据id查看订单详情")
    public Result getOrderDetails(@PathVariable("id") Long id) {
        System.out.println("id : " + id);
        OrderVO orderVO = orderService.getOrderDetailById(id);
        return Result.success(orderVO);
    }


    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result receiving(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单参数：{}",ordersConfirmDTO);
        return orderService.receiving(ordersConfirmDTO);
    }


    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单参数：{}",ordersRejectionDTO);
        return orderService.rejection(ordersRejectionDTO);
    }


    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单参数参数：{}",ordersCancelDTO);
        return orderService.cancel(ordersCancelDTO);
    }


    /**
     * 派送订单
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable("id") Long id) {
        return orderService.delivery(id);
    }


}
