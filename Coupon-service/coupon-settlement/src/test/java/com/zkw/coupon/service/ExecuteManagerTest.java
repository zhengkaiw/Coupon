package com.zkw.coupon.service;

import com.alibaba.fastjson.JSON;
import com.zkw.coupon.constant.CouponCategory;
import com.zkw.coupon.constant.GoodsType;
import com.zkw.coupon.exception.CouponException;
import com.zkw.coupon.executor.ExecuteManager;
import com.zkw.coupon.vo.CouponTemplateSDK;
import com.zkw.coupon.vo.GoodsInfo;
import com.zkw.coupon.vo.SettlementInfo;
import com.zkw.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

/**
 * 结算规则执行管理器测试用例
 * 对 Executor 的分发与结算逻辑进行测试
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExecuteManagerTest {

    // fake 一个 UserId
    private Long fakeUserId = 20001L;

    @Autowired
    private ExecuteManager manager;

    @Test
    public void testComputeRule() throws CouponException {

//         // 满减优惠券结算测试
//        log.info("ManJian coupon executor test");
//        SettlementInfo manJianInfo = fakeManJianCouponSettlement();
//        SettlementInfo result = manager.computeRule(manJianInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

//        // 折扣优惠券结算测试
//        log.info("ZheKou coupon executor test");
//        SettlementInfo zheKouInfo = fakeZheKouCouponSettlement();
//        SettlementInfo result = manager.computeRule(zheKouInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

//        // 立减优惠券结算测试
//        log.info("LiJian coupon executor test");
//        SettlementInfo liJianInfo = fakeLiJianCouponSettlement();
//        SettlementInfo result = manager.computeRule(liJianInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        // 满减折扣优惠券结算测试
        log.info("ManJian ZheKou coupon executor test");
        SettlementInfo manJianZheKouInfo = fakeManJianAndZheKouCouponSettlement();
        SettlementInfo result = manager.computeRule(manJianZheKouInfo);

        log.info("{}", result.getCost());
        log.info("{}", result.getCouponAndTemplateInfos().size());
        log.info("{}", result.getCouponAndTemplateInfos());
    }

    /**
     * fake*(mock) 满减优惠券的结算信息
     */
    private SettlementInfo fakeManJianCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        // fake 商品信息
        GoodsInfo goodsInfo1 = new GoodsInfo();
        goodsInfo1.setCount(2);
        goodsInfo1.setPrice(10.88);
        goodsInfo1.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo2 = new GoodsInfo();
        // 达到满减标准
        goodsInfo2.setCount(10);
        // 没有达到满减标准
//        goodsInfo2.setCount(5);
        goodsInfo2.setPrice(20.88);
        goodsInfo1.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo1, goodsInfo2));

        SettlementInfo.CouponAndTemplateInfo ctInfo = new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.MANJIAN.getCode());
        templateSDK.setKey("100120190801");

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(20, 199));
        rule.setUsage(new TemplateRule.Usage("北京市", "海淀区",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        templateSDK.setRule(rule);

        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    /**
     * <h2>fake 折扣优惠券的结算信息</h2>
     * */
    private SettlementInfo fakeZheKouCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.ZHEKOU.getCode());
        templateSDK.setKey("100220190712");

        // 设置 TemplateRule
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(85, 1));
        rule.setUsage(new TemplateRule.Usage("北京市", "海淀区",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    /**
     * <h2>fake 立减优惠券的结算信息</h2>
     * */
    private SettlementInfo fakeLiJianCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(3);
        templateSDK.setCategory(CouponCategory.LIJIAN.getCode());
        templateSDK.setKey("200320190712");

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setUsage(new TemplateRule.Usage("北京市", "海淀区",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    /**
     * <h2>fake 满减 + 折扣优惠券结算信息</h2>
     * */
    private SettlementInfo fakeManJianAndZheKouCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        // 满减优惠券
        SettlementInfo.CouponAndTemplateInfo manjianInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        manjianInfo.setId(1);

        CouponTemplateSDK manjianTemplate = new CouponTemplateSDK();
        manjianTemplate.setId(1);
        manjianTemplate.setCategory(CouponCategory.MANJIAN.getCode());
        manjianTemplate.setKey("100120190712");

        TemplateRule manjianRule = new TemplateRule();
        manjianRule.setDiscount(new TemplateRule.Discount(20, 199));
        manjianRule.setUsage(new TemplateRule.Usage("北京市", "海淀区",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        manjianRule.setWeight(JSON.toJSONString(Collections.emptyList()));
        manjianTemplate.setRule(manjianRule);
        manjianInfo.setTemplate(manjianTemplate);

        // 折扣优惠券
        SettlementInfo.CouponAndTemplateInfo zhekouInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        zhekouInfo.setId(1);

        CouponTemplateSDK zhekouTemplate = new CouponTemplateSDK();
        zhekouTemplate.setId(2);
        zhekouTemplate.setCategory(CouponCategory.ZHEKOU.getCode());
        zhekouTemplate.setKey("100220190712");

        TemplateRule zhekouRule = new TemplateRule();
        zhekouRule.setDiscount(new TemplateRule.Discount(85, 1));
        zhekouRule.setUsage(new TemplateRule.Usage("北京市", "海淀区",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        // setWeight将满减的优惠券的优惠券码加进来，标识允许该优惠券和此优惠券一起使用
        zhekouRule.setWeight(JSON.toJSONString(
                Collections.singletonList("1001201907120001")
        ));
        zhekouTemplate.setRule(zhekouRule);
        zhekouInfo.setTemplate(zhekouTemplate);

        info.setCouponAndTemplateInfos(Arrays.asList(
                manjianInfo, zhekouInfo
        ));

        return info;
    }
}
