package com.zkw.coupon.feign.hystrix;

import com.zkw.coupon.exception.CouponException;
import com.zkw.coupon.feign.SettlementClient;
import com.zkw.coupon.vo.CommonResponse;
import com.zkw.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结算微服务熔断策略实现
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

    /**
     * 优惠券规则计算
     * @param settlement {@link SettlementInfo}
     */
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) throws CouponException {

        log.error("[eureka-client-coupon-settlement] computeRule " +
                "request error");

        settlement.setEmploy(false);
        settlement.setCost(-1.0);

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] request error",
                settlement
        );
    }
}
