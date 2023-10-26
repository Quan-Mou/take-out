package com.sky.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.socket.SocketServer;
import com.sky.utils.WeChatPayUtil;
import com.sky.entity.*;
import com.sky.exception.BaseException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private SocketServer socketServer;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

//       判断异常清空，id地址是否存在，购物车是否有商品
        List<AddressBook> list = addressBookMapper.list(AddressBook.builder().id(ordersSubmitDTO.getAddressBookId()).build());
        if(list.size() < 1) {
            throw new BaseException("地址错误");
        }
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.queryDishOrSetmeal(shoppingCart);
        if(shoppingCarts.size() < 1) {
            throw new BaseException("购物车没有商品");
        }


//      添加订单信息到订单表
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        log.info("Orders: {}",orders);
//      使用当前时间戳作为订单号
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderTime(LocalDateTime.now());
        orders.setUserId(BaseContext.getCurrentId());
        orders.setAddress(list.get(0).getCityName());
        orders.setConsignee(list.get(0).getConsignee());
        orders.setPhone(list.get(0).getPhone());
//      TODO:订单用户名暂时没设置
        log.info("Orders: {}",orders);

        orderMapper.add(orders);
//      把购物车数据添加到订单详情表
        log.info("购物车数据： {}",shoppingCarts);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(ShoppingCart item : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(item,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        log.info("详情数据：{}",orderDetailList);
        orderDetailMapper.addBatch(orderDetailList);

//       来单提醒一般是在用户付款后发通知的，因为我们不是企业实现不了真正的微信支付，所以，来单提醒在用户下单后就提醒。
        HashMap<String, Object> map = new HashMap<>();
        map.put("type",1);
//        map.put("orderId",orders.getNumber());
        map.put("orderId",orders.getId());
        map.put("content","您有一条新的订单，请及时处理");
        System.out.println("Orders:" + orders);
        socketServer.sendToAllClient(JSON.toJSONString(map));
        log.info("来单提醒：{}",JSON.toJSONString(map));
//      添加成功后清空购物车
        shoppingCartMapper.clear(userId);
//      返回VO信息
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO(orders.getId(), orders.getNumber(), orders.getAmount(), orders.getOrderTime());
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        log.info("微信支付");
        String orderNumber = ordersPaymentDTO.getOrderNumber();
        Orders order = orderMapper.getByNumber(orderNumber);
        order.setStatus(Orders.REFUND);
        orderMapper.update(order);



        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }


    /**
     * 历史订单
     * @param
     * @return
     */

    @Override
    public PageResult historyOrders(int page, int pageSize, Integer status) {
        Page<Orders> pages = PageHelper.startPage(page, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setStatus(status);
        List<Orders> orders = orderMapper.listByCondition(ordersPageQueryDTO);

        List<OrderVO> vo = new ArrayList<>();
        orders.forEach(item -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(item,orderVO);
            Long id = item.getId();
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
            log.info("历史订单详情：{}",orderDetailList);
            orderVO.setOrderDetailList(orderDetailList);
            vo.add(orderVO);
        });

        log.info("分页数据：{}",pages);
        return new PageResult(pages.getTotal(),vo);
    }

    @Override
    public PageResult getAllorConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("条件参数：{}",ordersPageQueryDTO);
        Page<Object> page = PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersPageQueryDTO,orders);
        log.info("拷贝后的orders：{}",orders);
        List<Orders> result = orderMapper.listByCondition(ordersPageQueryDTO);
        log.info("返回的订单 {}",result);
        return new PageResult(page.getTotal(),result);
    }

    @Override
    public OrderStatisticsVO countStatus() {
//       待接单、已接单、派送中的订单count
        int toBeconfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        int confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        int deliveryInprogress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setToBeConfirmed(toBeconfirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInprogress);
        return orderStatisticsVO;
    }

    @Override
    public OrderVO getOrderDetailById(Long id) {
        Orders orders = orderMapper.getById(id);
        if(Objects.isNull(orders)) {
            throw new BaseException("未知错误!");
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderDetailList(orderDetailList);
        BeanUtils.copyProperties(orders,orderVO);
        return orderVO;
    }


    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @Override
    public Result receiving(OrdersConfirmDTO ordersConfirmDTO) {
        Long id = ordersConfirmDTO.getId();
        Orders order = orderMapper.getById(id);
        log.info("接单的订单：{}",order);
        order.setStatus(Orders.CONFIRMED);
        orderMapper.update(order);
        return Result.success();
    }


    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @Override
    public Result rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Long id = ordersRejectionDTO.getId();
        Orders order = orderMapper.getById(id);
        order.setStatus(7); // 退款
        order.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        order.setCancelTime(LocalDateTime.now());
        log.info("拒绝的订单：{}",order);
        orderMapper.update(order);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @Override
    public Result cancel(OrdersCancelDTO ordersCancelDTO) {
        Long id = ordersCancelDTO.getId();
        Orders order = orderMapper.getById(id);
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason(ordersCancelDTO.getCancelReason());
        order.setCancelTime(LocalDateTime.now());
        log.info("取消的订单：{}",order);
        orderMapper.update(order);
        return Result.success();
    }

    @Override
    public Result delivery(Long id) {
        log.info("派送订单 ：{}",id);
        Orders order = orderMapper.getById(id);
        order.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(order);
        return Result.success();
    }
}
