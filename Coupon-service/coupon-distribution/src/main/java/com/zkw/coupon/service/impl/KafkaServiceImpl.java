package com.zkw.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.zkw.coupon.constant.Constant;
import com.zkw.coupon.constant.CouponStatus;
import com.zkw.coupon.dao.CouponDao;
import com.zkw.coupon.entity.Coupon;
import com.zkw.coupon.service.IKafkaService;
import com.zkw.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Kafka 相关的服务接口实现
 * 核心思想: 是将 Cache 中的 Coupon 的状态变化同步到 DB 中
 */
@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {

    private final CouponDao couponDao;

    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * 消费优惠券 Kafka 消息
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "kafka-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(message.toString(), CouponKafkaMessage.class);

            log.info("Received CouponKafkaMessage: {}", message);
            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
                default:
            }
        }
    }

    /**
     * 处理已使用的用户优惠券
     */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        // TODO 给用户发送短信
        processCouponsByStatus(kafkaMessage, status);
    }

    /**
     * 处理已过期的用户优惠券
     */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        // TODO 给用户发送推送
        processCouponsByStatus(kafkaMessage, status);
    }

    /**
     * 根据状态处理优惠券信息
     */
    private void processCouponsByStatus(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(
                kafkaMessage.getIds()
        );
        if (CollectionUtils.isEmpty(coupons)
                || coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Can not find right coupon info: {}",
                    JSON.toJSONString(kafkaMessage));
            // TODO 发送邮件
            return;
        }

        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage ops coupon count: {}",
                couponDao.saveAll(coupons).size());
    }
}
