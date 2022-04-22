package com.zkw.coupon.service.impl;

import com.zkw.coupon.dao.CouponTemplateDao;
import com.zkw.coupon.entity.CouponTemplate;
import com.zkw.coupon.exception.CouponException;
import com.zkw.coupon.service.IAsyncService;
import com.zkw.coupon.service.IBuildTemplateService;
import com.zkw.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 构建优惠券模板接口实现
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    private IAsyncService asyncService;

    private CouponTemplateDao couponTemplateDao;

    public BuildTemplateServiceImpl(IAsyncService asyncService, CouponTemplateDao couponTemplateDao) {
        this.asyncService = asyncService;
        this.couponTemplateDao = couponTemplateDao;
    }

    /**
     * 创建优惠券模板
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {

        // 参数合法性校验
        if (!request.validate()) {
            throw new CouponException("BuildTemplate param is not valid");
        }

        // 判断同名优惠券模板是否存在
        if (null != couponTemplateDao.findByName(request.getName())) {
            throw new CouponException("Exist same name template");
        }

        // 构造 CouponTemplate 并保存到数据库中
        CouponTemplate template = request2Template(request);
        template = couponTemplateDao.save(template);

        // 根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    /**
     * 将 TemplateRequest 转换为 CouponTemplate
     */
    private CouponTemplate request2Template(TemplateRequest request) {

        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
