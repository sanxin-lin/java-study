package org.jeecg.config;

import lombok.Data;
import org.jeecg.config.vo.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 加载项目配置
 * @author: Sunshine_Lin
 */
@Data
@Component("jeecgBaseConfig")
@ConfigurationProperties(prefix = "jeecg")
public class JeecgBaseConfig {
    /**
     * 签名密钥串(字典等敏感接口)
     * @TODO 降低使用成本加的默认值,实际以 yml配置 为准
     */
    private String signatureSecret = "dd05f1c54d63749eda95f9fa6d49v442a";
    /**
     * 需要加强校验的接口清单
     */
    private String signUrls;
    /**
     * 上传模式
     * 本地：local\Minio：minio\阿里云：alioss
     */
    private String uploadType;

    /**
     * 平台安全模式配置
     */
    private Firewall firewall;

    /**
     * shiro拦截排除
     */
    private Shiro shiro;
    /**
     * 上传文件配置
     */
    private Path path;

    /**
     * 前端页面访问地址
     * pc: http://localhost:3100
     * app: http://localhost:8051
     */
    private DomainUrl domainUrl;

    /**
     * 文件预览
     */
    private String fileViewDomain;
    /**
     * ES配置
     */
    private Elasticsearch elasticsearch;

    /**
     * 微信支付
     * @return
     */
    private WeiXinPay weiXinPay;

    /**
     * 百度开放API配置
     */
    private BaiduApi baiduApi;



}
