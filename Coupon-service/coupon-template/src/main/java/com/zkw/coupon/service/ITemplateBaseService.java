package com.zkw.coupon.service;

import com.zkw.coupon.entity.CouponTemplate;
import com.zkw.coupon.exception.CouponException;
import com.zkw.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板基础服务(CRUD)定义
 */
public interface ITemplateBaseService {

    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * 查找所有可用的优惠券模板
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     * @param ids 模板 ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK> </key:>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
