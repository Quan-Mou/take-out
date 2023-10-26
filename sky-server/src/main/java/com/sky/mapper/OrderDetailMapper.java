package com.sky.mapper;


import com.sky.entity.OrderDetail;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {



    void addBatch(List<OrderDetail> orderDetails);


    /**
     * 根据订单号查询订单详情
     * @param orderDetail
     * @return
     */
    
    @Select("select * from order_detail where order_id = #{orderId}")
//    List<OrderDetail> getByNumber(OrderDetail orderDetail);
    List<OrderDetail> getByNumber(Long orderId);


    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
