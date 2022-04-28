package com.zkw.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.zkw.coupon.exception.CouponException;
import com.zkw.coupon.executor.ExecuteManager;
import com.zkw.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结算服务 Controller
 */
@Slf4j
@RestController
public class SettlementController {

    // 结算规则执行管理器
    private final ExecuteManager executeManager;

    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * 优惠券结算
     * 127.0.0.1:7003/coupon-settlement/settlement/compute
     * 127.0.0.1:9000/coupon/coupon-template/settlement/compute
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement) throws CouponException {

        log.info("settlement: {}", JSON.toJSONString(settlement));

        return executeManager.computeRule(settlement);
    }
}
