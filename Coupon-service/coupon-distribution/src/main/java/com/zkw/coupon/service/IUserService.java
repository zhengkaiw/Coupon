package com.zkw.coupon.service;

import com.zkw.coupon.entity.Coupon;
import com.zkw.coupon.exception.CouponException;
import com.zkw.coupon.vo.AcquireTemplateRequest;
import com.zkw.coupon.vo.CouponTemplateSDK;
import com.zkw.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * 用户服务相关的接口定义
 * 1. 用户的三类状态优惠券信息展示服务
 * 2. 查看用户当前可以领取的优惠券模板 - coupon-template 微服务配合实现
 * 3. 用户领取优惠券服务
 * 4. 用户消费优惠券服务 - coupon-settlement 微服务配合实现
 */
public interface IUserService {

    /**
     * 根据用户 id 和状态查询优惠券记录
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     * @param userId 用户 id
     * @return {@link CouponTemplateSDK}s 优惠券模板
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * 用户领取优惠券
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    /**
     * 结算(核销)优惠券
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}
