package com.zkw.coupon.executor.impl;

import com.zkw.coupon.constant.RuleFlag;
import com.zkw.coupon.executor.AbstractExecutor;
import com.zkw.coupon.executor.RuleExecutor;
import com.zkw.coupon.vo.CouponTemplateSDK;
import com.zkw.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 折扣优惠券结算规则执行器
 */
@Slf4j
@Component
public class ZhekouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    /**
     * 优惠券规则的计算
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimals(goodsCostSum(
                settlement.getGoodsInfos()
        ));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability) {
            log.debug("Zhekou template does not match GoodsType");
            return probability;
        }

        // 折扣优惠券可以直接使用, 没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // 计算使用优惠券之后的价格
        settlement.setCost(retain2Decimals(
                Math.max((goodsSum * (quota * 1.0 / 100)), minCost())
        ));
        log.debug("Use Zhekou coupon Make goods cost from {} to {}",
                goodsSum, settlement.getCost());

        return settlement;
    }
}
