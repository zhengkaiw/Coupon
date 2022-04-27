package com.zkw.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.zkw.coupon.entity.Coupon;
import com.zkw.coupon.exception.CouponException;
import com.zkw.coupon.service.IUserService;
import com.zkw.coupon.vo.AcquireTemplateRequest;
import com.zkw.coupon.vo.CouponTemplateSDK;
import com.zkw.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务 Controller
 */
@Slf4j
@RestController
public class UserServiceController {

    private final IUserService userService;

    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 根据用户 id 和优惠券状态查找用户优惠券记录
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(
            @RequestParam("userId") Long userId,
            @RequestParam("status") Integer status) throws CouponException {
        log.info("Find coupons by status: {}, {}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(@RequestParam("userId") Long userId) throws CouponException {
        log.info("Find available template: {}", userId);
        return userService.findAvailableTemplate(userId);
    }

    /**
     * 用户领取优惠券
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request) throws CouponException {
        log.info("Acquire template: {}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /**
     * 结算(核销)优惠券
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(info));
        return userService.settlement(info);
    }
}
