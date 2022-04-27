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
 * 满减优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManjianExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /**
     * 优惠券规则的计算
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimals(
                goodsCostSum(settlement.getGoodsInfos())
        );
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (null != probability) {
            log.debug("Manjian template does not match to GoodsType");
            return probability;
        }

        // 判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double base = (double) templateSDK.getRule().getDiscount().getBase();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // 如果不符合标准, 则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current goods cost sum < Manjian coupon base");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        // 计算使用优惠券之后的价格
        settlement.setCost(retain2Decimals(
                Math.max((goodsSum - quota), minCost())
        ));
        log.debug("Use Manjian coupon Make goods cost from {} to {}",
                goodsSum, settlement.getCost());

        return settlement;
    }
}
