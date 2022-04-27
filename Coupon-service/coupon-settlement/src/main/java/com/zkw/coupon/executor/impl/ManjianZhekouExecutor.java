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
import java.util.Arrays;
import java.util.Collections;
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

        // 当前的折扣优惠券和满减券如果不能共用, 清空优惠券, 返回商品原价
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current Manjian and Zhekou can not shared");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double) manJian.getTemplate().getRule().getDiscount().getBase();
        double manJianQuota = (double) manJian.getTemplate().getRule().getDiscount().getQuota();

        // 最终的价格
        double targetSum = goodsSum;

        // 先计算满减
        if (targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        // 再计算折扣
        double zheKouQuota = (double) zheKou.getTemplate().getRule().getDiscount().getQuota();
        targetSum *= zheKouQuota * 1.0 / 100;
        ctInfos.add(zheKou);

        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(Math.max(targetSum, minCost())));

        log.debug("Use Manjian and Zhekou coupon make goods cost from {} to {}",
                goodsSum, settlement.getCost());

        return settlement;
    }

    /**
     * 当前的两张优惠券是否可以共用
     * 即校验 TemplateRule 中的 weight 是否满足条件
     */
    @SuppressWarnings("all")
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manJian,
                                        SettlementInfo.CouponAndTemplateInfo zheKou) {

        String manJianKey = manJian.getTemplate().getKey()
                + String.format("%04d", manJian.getTemplate().getId());
        String zheKouKey = zheKou.getTemplate().getKey()
                + String.format("%04d", zheKou.getTemplate().getId());

        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manJianKey);
        allSharedKeysForManjian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForZhekou = new ArrayList<>();
        allSharedKeysForZhekou.add(zheKouKey);
        allSharedKeysForZhekou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(),
                List.class
        ));

        return CollectionUtils.isSubCollection(
                Arrays.asList(manJianKey, zheKouKey), allSharedKeysForManjian)
                || CollectionUtils.isSubCollection(Arrays.asList(manJianKey, zheKouKey), allSharedKeysForZhekou);
    }
}
