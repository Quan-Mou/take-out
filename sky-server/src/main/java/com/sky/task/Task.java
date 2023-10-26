package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.socket.SocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Task {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SocketServer socketServer;


    /**
     * 通过WebSocket每隔5秒向客户端发送消息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendMessageToClient() {
        socketServer.sendToAllClient("这是来自服务端的消息：" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    }


    /**
     *  清理派送中的订单-凌晨1dian
     */
    @Scheduled(cron = "0 1 * * * ?")
    public void clearOrder() {
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        log.info("凌晨1点啦！，开始清理0点之前的派送中的订单！");
        List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);
        if(orders != null && orders.size() > 0) {
            List<Long> orderIds = new ArrayList<>();
            for(Orders order : orders) {
                Long orderId = order.getId();
            }
            //   批量更新
            orderMapper.batchUpdateByIds(orderIds);
        }
    }


    /**
     * 清理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void clearTimeOutOrders() {
        LocalDateTime localTime =  LocalDateTime.now().plusMinutes(-15);
        log.info("减去15分钟后的时间: {}",localTime);
        List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, localTime);
        if(orders != null && orders.size() > 0) {
            for (Orders order : orders) {
                order.setCancelReason("用户未支付");
                order.setCancelTime(LocalDateTime.now());
                order.setStatus(6);
                orderMapper.update(order);
            }
        }
    }

}
