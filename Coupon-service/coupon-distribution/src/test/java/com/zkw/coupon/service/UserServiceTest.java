package com.zkw.coupon.service;

import com.alibaba.fastjson.JSON;
import com.zkw.coupon.constant.CouponStatus;
import com.zkw.coupon.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 用户服务功能测试用例
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    // fake 一个 UserId
    private final Long fakeUserId = 20001L;

    @Autowired
    private IUserService userService;

    @Test
    public void testFindCouponsByStatus() throws CouponException {

        System.out.println(JSON.toJSONString(
                userService.findCouponsByStatus(
                        fakeUserId,
                        CouponStatus.USABLE.getCode()
                )
        ));
//        System.out.println(JSON.toJSONString(
//                userService.findCouponsByStatus(
//                        fakeUserId,
//                        CouponStatus.USED.getCode()
//                )
//        ));
//        System.out.println(JSON.toJSONString(
//                userService.findCouponsByStatus(
//                        fakeUserId,
//                        CouponStatus.EXPIRED.getCode()
//                )
//        ));
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException {

        System.out.println(JSON.toJSONString(
                userService.findAvailableTemplate(fakeUserId)
        ));
    }
}
