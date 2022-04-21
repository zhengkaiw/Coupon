package com.zkw.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 产品线枚举
 */
@Getter
@AllArgsConstructor
public enum ProductLine {

    PRODUCTION_ONE("产品线1", 1),
    PRODUCTION_TWO("产品线2", 2);

    // 产品线描述
    private String description;

    // 产品线编码
    private Integer code;

    public static ProductLine of(Integer code) {

        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exists"));
    }
}
