package com.zkw.coupon.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Ribbon 应用 Controller
 */
@Slf4j
@RestController
public class RibbonController {

    private final RestTemplate restTemplate;

    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TemplateInfo getTemplateInfo() {

        String infoUrl = "http://eureka-client-coupon-template" +
                "/coupon-template/info";
        return restTemplate.getForEntity(infoUrl, TemplateInfo.class).getBody();
    }

    /**
     * 模板微服务的元信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TemplateInfo {

        private Integer code;
        private String message;
        private List<Map<String, Object>> data;
    }
}
