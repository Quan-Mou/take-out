package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);


    /**
     * 历史订单
     * @param
     * @return
     */

    PageResult historyOrders(int page,int pageSize,Integer status);


    PageResult getAllorConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO countStatus();

    OrderVO getOrderDetailById(Long id);

    /**
     * 管理员接单
     */
    Result receiving(OrdersConfirmDTO ordersConfirmDTO);


    /**
     * 拒单
     * @return
     */
    Result rejection(OrdersRejectionDTO ordersRejectionDTO);


    /**
     * 取消订单
     * @return
     */
    Result cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     * @return
     */
    Result delivery(Long id);
}
