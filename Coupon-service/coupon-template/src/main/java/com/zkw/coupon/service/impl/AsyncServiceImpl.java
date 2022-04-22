package com.zkw.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.zkw.coupon.constant.Constant;
import com.zkw.coupon.dao.CouponTemplateDao;
import com.zkw.coupon.entity.CouponTemplate;
import com.zkw.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步服务接口实现
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    // CouponTemplate Dao
    private final CouponTemplateDao couponTemplateDao;

    // 注入 Redis 模板类
    private final StringRedisTemplate redisTemplate;

    public AsyncServiceImpl(CouponTemplateDao couponTemplateDao, StringRedisTemplate redisTemplate) {
        this.couponTemplateDao = couponTemplateDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据模板异步创建优惠券码
     * @param couponTemplate {@link CouponTemplate} 优惠券模板实体
     */
    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate couponTemplate) {
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(couponTemplate);

        // coupon_template_code_1
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, couponTemplate.getId().toString());
        log.info("Push coupon codes to Redis: {}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));

        couponTemplate.setAvailable(true);
        couponTemplateDao.save(couponTemplate);

        watch.stop();
        log.info("Construct coupon codes by Template costs: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        // TODO 发送短信或者邮件通知优惠券模板已经可用
        log.info("CouponTemplate({}) is available", couponTemplate.getId());
    }

    /**
     * 构造优惠券码
     * 优惠券码(对应于每一张优惠券, 18位)
     * 前四位：产品线 + 类型
     * 中间六位：日期随机(190101)
     * 后八位：0 ~ 9 随机数构成
     * @param couponTemplate {@link CouponTemplate}
     * @return Set<String> 与 couponTemplate 相同个数的优惠券码
     */
    private Set<String> buildCouponCode(CouponTemplate couponTemplate) {

        // 计时
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(couponTemplate.getCount());

        // 前四位
        String prefix4 = couponTemplate.getProductLine().getCode().toString()
                + couponTemplate.getCategory().getCode();

        String date = new SimpleDateFormat("yyMMdd")
                .format(couponTemplate.getCreateTime());

        for (int i = 0; i != couponTemplate.getCount(); ++i) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        while (result.size() < couponTemplate.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert result.size() == couponTemplate.getCount();

        watch.stop();
        log.info("Build coupon codes costs: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /**
     * 构造优惠券码的后 14 位
     * @param date 创建优惠券的日期
     * @return 14 位优惠券码
     */
    private String buildCouponCodeSuffix14(String date) {

        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // 中间六位
        List<Character> chars = date.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toList());

        Collections.shuffle(chars);
        String mid6 = chars.stream().map(Object::toString).collect(Collectors.joining());

        // 后八位
        String suffix8 = RandomStringUtils.random(1, bases)
                + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}