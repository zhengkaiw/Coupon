package com.zkw.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.zkw.coupon.constant.CouponCategory;
import com.zkw.coupon.constant.RuleFlag;
import com.zkw.coupon.executor.AbstractExecutor;
import com.zkw.coupon.executor.RuleExecutor;
import com.zkw.coupon.vo.GoodsInfo;
import com.zkw.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 满减折扣优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManjianZhekouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * 校验商品类型与优惠券是否匹配
     * 1. 这里实现的是满减 + 折扣优惠券的校验
     * 2. 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
     * @param settlement {@link SettlementInfo} 用户传递的结算信息
     */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        log.debug("Check Manjian and Zhekou are match or not");
        List<Integer> goodsType = settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType)
                .collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();

        settlement.getCouponAndTemplateInfos().forEach(ct -> {
            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
        });

        // 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodsType, templateGoodsType));
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
        // 商品类型的校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability) {
            log.debug("Manjian and Zhekou template are not match to GoodsType");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct : settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }

        assert null != manJian;
        assert null != zheKou;

        // 当前的优惠券和满减券如果不能共用, 清空优惠券, 返回商品原价

        return null;
    }

    private boolean isTemplateCanShared() {

        return false;
    }
}
