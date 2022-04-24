package com.zkw.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取优惠券请求对象定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquireTemplateRequest {

    // 用户 id
    private Long userId;

    // 优惠券模板信息
    private CouponTemplateSDK templateSDK;
}
