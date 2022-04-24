package com.zkw.coupon.dao;

import com.zkw.coupon.constant.CouponStatus;
import com.zkw.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Coupon Dao 接口定义
 */
public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * 根据 userId + 状态查找优惠券记录
     * where userId = ... and status = ...
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
