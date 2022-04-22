package com.zkw.coupon.service;

import com.alibaba.fastjson.JSON;
import com.zkw.coupon.constant.CouponCategory;
import com.zkw.coupon.constant.DistributeTarget;
import com.zkw.coupon.constant.PeriodType;
import com.zkw.coupon.constant.ProductLine;
import com.zkw.coupon.vo.TemplateRequest;
import com.zkw.coupon.vo.TemplateRule;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * 构造优惠券模板服务测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTest {

    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate() throws Exception {
        System.out.println(JSON.toJSONString(
                buildTemplateService.buildTemplate(fakeTemplateRequest())
        ));
        Thread.sleep(1000);
    }

    /**
     * fake TemplateRequest
     */
    private TemplateRequest fakeTemplateRequest() {

        TemplateRequest request = new TemplateRequest();
        request.setName("coupon-template-" + new Date().getTime());
        request.setLogo("https://www.coupon.com");
        request.setDesc("This is a coupon template");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.PRODUCTION_ONE.getCode());
        request.setCount(10000);
        request.setUserId(10001L); // fake user id
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),
                1, DateUtils.addDays(new Date(), 30).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "Beijing", "Haidian",
                JSON.toJSONString(Arrays.asList("Entertainment", "Furniture"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}
