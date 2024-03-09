package com.sky.task;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;

//  定时任务类 定时处理订单状态
@Slf4j
@Component
public class orderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoytOrder() {
        log.info("定时处理超时订单{}", LocalDateTime.now());
        List<Orders> orderList = orderMapper.getByStautsAndOrderTimeLT(Orders.PENDING_PAYMENT,
                LocalDateTime.now().plusMinutes(-15));
        if (orderList != null && orderList.size() > 0) {
            for (Orders orders : orderList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                // TODO update没写
                // orderMapper.update(orders)

            }
        }
    }

    // 每天凌晨一点
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("定时处理始终处于派送的订单{}", LocalDateTime.now());
        List<Orders> orderList = orderMapper.getByStautsAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,
                LocalDateTime.now().minusHours(1));
        if (orderList != null && orderList.size() > 0) {
            for (Orders orders : orderList) {
                orders.setStatus(Orders.COMPLETED);
                // orders.setCancelReason("订单超时，自动取消");
                // orders.setCancelTime(LocalDateTime.now());
                // TODO update没写
                // orderMapper.update(orders)

            }
        }
    }
}
