package com.zkw.coupon.feign.hystrix;

import com.zkw.coupon.feign.TemplateClient;
import com.zkw.coupon.vo.CommonResponse;
import com.zkw.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板 Feign 接口的熔断降级策略
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

    /**
     * 查找所有可用的优惠券模板
     */
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {

        log.error("[eureka-client-coupon-template] findAllUsableTemplate " +
                "request error");

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error",
                Collections.emptyList()
        );
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     * @param ids 优惠券模板 id
     */
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2Template(Collection<Integer> ids) {

        log.error("[eureka-client-coupon-template] findIds2Template " +
                "request error");

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error",
                Collections.emptyMap()
        );
    }
}
