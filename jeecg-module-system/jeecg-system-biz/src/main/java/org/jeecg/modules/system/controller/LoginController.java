/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-22 15:32
 **/

package org.jeecg.modules.system.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.Md5Util;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.modules.system.util.RandImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sys")
@Api(tags="用户登录")
@Slf4j
public class LoginController {

    @Autowired
    private RedisUtil redisUtil;

    private final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";
    private final JeecgBaseConfig jeecgBaseConfig;

    public LoginController(JeecgBaseConfig jeecgBaseConfig) {
        this.jeecgBaseConfig = jeecgBaseConfig;
    }

    /**
     * 登录二维码
     */
    @ApiOperation(value = "登录二维码")
    @GetMapping("getLoginQrcode")
    public Result<?> getLoginQrcode() {
        String qrcodeId = CommonConstant.LOGIN_QRCODE_PRE + IdWorker.getIdStr();
        //定义二维码参数
        Map params = new HashMap<>(5);
        params.put("qrcodeId", qrcodeId);
        //存放二维码唯一标识30秒有效
        redisUtil.set(CommonConstant.LOGIN_QRCODE +qrcodeId, qrcodeId, 30);
        log.info("登录二维码，Redis key = {}，checkCode = {}", CommonConstant.LOGIN_QRCODE +qrcodeId, qrcodeId);
        return Result.ok(params);
    }

    /**
     * 扫码二维码
     */
    @ApiOperation(value = "扫码登录二维码", notes = "扫码登录二维码")
    @PostMapping("/scanLoginQrcode")
    public Result<?> scanLoginQrcode(@RequestParam String qrcodeId, @RequestParam String token) {
        Object check = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
        if (oConvertUtils.isNotEmpty(check)) {
            // 存放token给前台读取
            redisUtil.set(CommonConstant.LOGIN_QRCODE_TOKEN + qrcodeId, token, 60);
        } else {
            return Result.error("二维码已过期,请刷新后重试");
        }

        return Result.ok("扫码成功");
    }

    /**
     * 后台生成图形验证码 ：有效
     * @param response
     * @param key
     */
    @ApiOperation("获取验证码")
    @GetMapping(value = "/randomImage/{key}")
    public Result<String> randomImage(HttpServletResponse response, @PathVariable("key") String key){
        Result<String> res = new Result<String>();
        try {
            //生成验证码
            String code = RandomUtil.randomString(BASE_CHECK_CODES, 4);
            //存到redis中
            String lowerCaseCaseCode = code.toLowerCase();

            // 加入密钥作为混淆，避免简单的拼接，被外部利用，用户自定义该密钥即可
            String orgin = lowerCaseCaseCode + key + jeecgBaseConfig.getSignatureSecret();
            String realKey = Md5Util.md5Encode(orgin, "utf-8");

            redisUtil.set(realKey, lowerCaseCaseCode, 60);
            log.info("获取验证码，Redis key = {}，checkCode = {}", realKey, code);
            //返回前端
            String base64 = RandImageUtil.generate(code);
            res.setSuccess(true);
            res.setResult(base64);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.error500("获取验证码失败,请检查redis配置!");
        }
        return res;
    }
}
