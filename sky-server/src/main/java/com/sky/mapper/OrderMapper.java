package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void add(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    List<Orders> list(Orders orders);


    @Select("select * from orders where user_id = #{currentId}")
    List<Orders> listByUserid(Long currentId);

    List<Orders> listByCondition(OrdersPageQueryDTO ordersPageQueryDTO);


    @Select("select count(*) from orders where status = #{status}")
    int countStatus(Integer status);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime time);

//    @Update("update table orders set status = 6 where id in(orders))")
    void batchUpdateByIds(List<Long> orderIds);

    @Select("select sum(amount) from orders where  order_time between #{begin} and #{end} and status = #{status}")
    Double getTurnoverByDay(Map map);


    /**
     * 根据状态统计订单数
     * @param cancelled
     * @return
     */
    Integer orderCountByStatus(Integer status);


    /**
     * 根据时间统计订单数
     * @param map
     * @return
     */
    Integer orderStatistics(Map map);

    @Select("select od.name as name ,sum(od.number) as number from orders o join order_detail od on o.id = od.order_id " +
            "where o.order_time > #{begin} and o.order_time < #{end} " +
            "GROUP BY od.name " +
            "order by number desc " +
            "limit 0,10")
    List<GoodsSalesDTO> salesStatistics(LocalDate begin, LocalDate end);

}
