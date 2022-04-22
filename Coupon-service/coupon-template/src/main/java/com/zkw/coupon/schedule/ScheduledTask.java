package com.zkw.coupon.schedule;

import com.zkw.coupon.dao.CouponTemplateDao;
import com.zkw.coupon.entity.CouponTemplate;
import com.zkw.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时清理已过期的优惠券模板
 */
@Slf4j
@Component
public class ScheduledTask {

    private final CouponTemplateDao couponTemplateDao;

    public ScheduledTask(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    /**
     * 下线已过期的优惠券模板
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate() {

        log.info("Start to expire CouponTemplate");

        List<CouponTemplate> templates = couponTemplateDao.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templates)) {
            log.info("Done expire CouponTemplate");
            return;
        }

        Date now = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());

        templates.forEach(t -> {
            // 根据优惠券模板规则中的"过期规则"校验模板是否过期
            TemplateRule rule = t.getRule();
            if (rule.getExpiration().getDeadline() < now.getTime()) {
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });

        if (CollectionUtils.isNotEmpty(expiredTemplates)) {
            log.info("Expired couponTemplates num: {}",
                    couponTemplateDao.saveAll(expiredTemplates));
        }
        log.info("Done expire CouponTemplate");
    }
}