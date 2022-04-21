package com.zkw.coupon.service;

import com.zkw.coupon.entity.CouponTemplate;
import com.zkw.coupon.exception.CouponException;
import com.zkw.coupon.vo.TemplateRequest;

/**
 * 构建优惠券模板接口定义
 */
public interface IBuildTemplateService {

    /**
     * 创建优惠券模板
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}