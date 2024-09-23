/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-22 15:32
 **/

package org.jeecg.modules.system.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.Md5Util;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysLoginModel;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.util.RandImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private ISysUserService sysUserService;

    private final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";
    private final JeecgBaseConfig jeecgBaseConfig;

    public LoginController(JeecgBaseConfig jeecgBaseConfig) {
        this.jeecgBaseConfig = jeecgBaseConfig;
    }

    @ApiOperation("登录接口")
    @PostMapping("/login")
    public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel, HttpServletRequest request) {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = sysLoginModel.getUsername();
        String password = sysLoginModel.getPassword();
        if(isLoginFailOvertimes(username)) {
            return result.error500("该用户登录失败次数过多，请于10分钟后再次登录！");
        }

        // step.1 验证码check
        String captcha = sysLoginModel.getCaptcha();
        if (captcha == null) {
            return result.error500("验证码无效");
        }

        String lowerCaseCaptcha = captcha.toLowerCase();
        String origin = lowerCaseCaptcha + sysLoginModel.getCheckKey() + jeecgBaseConfig.getSignatureSecret();
        String realKey = Md5Util.md5Encode(origin, "utf-8");
        Object checkCode = redisUtil.get(realKey);
        if (checkCode == null || !checkCode.toString().equals(lowerCaseCaptcha)) {
            log.warn("验证码错误，key= {} , Ui checkCode= {}, Redis checkCode = {}", sysLoginModel.getCheckKey(), lowerCaseCaptcha, checkCode);
            result.error500("验证码错误");
            // 改成特殊的code 便于前端判断
            result.setCode(HttpStatus.PRECONDITION_FAILED.value());
            return result;
        }

        // step.2 校验用户是否存在且有效
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        result = sysUserService.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }

        // step.3 校验用户名或密码是否正确
        String userPassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
        String sysPassword = sysUser.getPassword();
        if (!sysPassword.equals(userPassword)) {
            addLoginFailOvertimes(username);
            result.error500("用户名或密码错误");
            return result;
        }

        // step.4  登录成功获取用户信息
        //        userIn

        return result;
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
     * 获取用户扫码后保存的token
     */
    @ApiOperation(value = "获取用户扫码后保存的token", notes = "获取用户扫码后保存的token")
    @GetMapping("/getQrcodeToken")
    public Result<?> getQrcodeToken(@RequestParam String qrcodeId) {
        Object token = redisUtil.get(CommonConstant.LOGIN_QRCODE_TOKEN + qrcodeId);
        Map result = new HashMap<>(5);
        Object qrcodeIdExpire = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
        if (oConvertUtils.isEmpty(qrcodeIdExpire)) {
            //二维码过期通知前台刷新
            result.put("token", "-2");
            return Result.ok(result);
        }
        if (oConvertUtils.isNotEmpty(token)) {
            result.put("token", token);
            result.put("success", true);
        } else {
            result.put("token", "-1");
        }
        return Result.ok(result);
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

    /**
     * 记录登录失败次数
     * @param username
     */
    public void addLoginFailOvertimes(String username) {
        String key = CommonConstant.LOGIN_FAIL + username;
        Object failTime = redisUtil.get(key);
        Integer val = 0;
        if (failTime != null) {
            val = Integer.parseInt(failTime.toString());
        }
        // 10分钟，一分钟为60s
        redisUtil.set(key, ++val, 600);
    }

    /**
     * 登录失败超出次数5 返回true
     * @param username
     * @return
     */
    public boolean isLoginFailOvertimes(String username) {
        Object failTime = redisUtil.get(CommonConstant.LOGIN_FAIL + username);
        return failTime != null && Integer.parseInt(failTime.toString()) >= 5;
    }
}
