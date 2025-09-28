package work.chncyl.service.permission.controller;

import com.alibaba.fastjson2.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftArticles;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import work.chncyl.base.global.aspect.annotation.AllowAnonymous;
import work.chncyl.base.global.config.WxMpProperties;
import work.chncyl.base.global.config.WxUtils;
import work.chncyl.base.global.result.ApiResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/WeiXin")
@Slf4j
@Api(tags = "微信接口")
public class WeiXinController {
    @Resource
    private WxMpService wxService;

    @ApiOperation("获取JSSDK配置")
    @GetMapping("GetJsSdkConfig")
    public ApiResult<WxJsapiSignature> GetJsSdkConfig(String url, @RequestParam(defaultValue = "default") String appName) throws WxErrorException {
        String appId = WxUtils.getAppId(appName);
        WxJsapiSignature jsapiSignature = this.wxService.switchoverTo(appId).createJsapiSignature(url);
        return ApiResult.success(jsapiSignature);

    }

    @ApiOperation("GetOpenId")
    @GetMapping("GetOpenId")
    //public ModelAndView GetOpenId(HttpServletRequest request, String code, String redirectUrl, @RequestParam(defaultValue = "default") String appName){
    public ModelAndView GetOpenId(HttpServletRequest request, String code, String redirectUrl, @RequestParam(defaultValue = "default") String appName) throws UnsupportedEncodingException {
        if (redirectUrl.indexOf("$appName=") > 0) {
            int beginPosition = (redirectUrl.indexOf("$appName=") + 9);
            int endPosition = redirectUrl.lastIndexOf("$");
            appName = redirectUrl.substring(beginPosition, endPosition);
            redirectUrl = redirectUrl.substring(0, beginPosition - 9);
        }
        //return redirectUrl;
        WxMpProperties.MpConfig config = WxUtils.getWxConfigByAppName(appName);
        if (StringUtils.isEmpty(code)) {
//            String currentUrl = request.getRequestURL().toString().replace(":7157", "");
            String currentUrl = request.getRequestURL().toString();
            currentUrl += "?redirectUrl=" + redirectUrl + "&appName=" + appName;
            currentUrl = URLEncoder.encode(currentUrl, "UTF-8");
            return new ModelAndView("redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + config.getAppId() + "&redirect_uri=" + currentUrl + "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
        }
        redirectUrl = redirectUrl.replace("$", "#");
        try {
            WxOAuth2AccessToken accessToken = wxService.switchoverTo(WxUtils.getAppId(appName)).getOAuth2Service().getAccessToken(code);

            String reUrl = redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "openId=" + accessToken.getOpenId();
            return new ModelAndView("redirect:" + reUrl);
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new RuntimeException("授权失败");
        }
        //return new ModelAndView("redirect:https://www.baidu.com");
    }

    /***
     * @title sendMessage
     * @description 测试微信消息模板发送
     * @author wyq
     * @updateTime 2022/6/16 16:10
     * @throws
     */
    @ApiOperation("sendMessage")
    @GetMapping("sendMessage")
    public void sendMessage() throws WxErrorException {
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser("oYC6M6SAes5nQMaGtMv4h8FzvdoE").templateId(WxUtils.getMessageTemplate("ceshi", "meeting_video_alert")).url("").build();
        //参数添加
        templateMessage.addData(new WxMpTemplateData("userName", "小东东"));
        //templateMessage.addData(new WxMpTemplateData(name2, value2, color2));
//        wxService.switchoverTo(WxUtils.getAppId("ceshi")).getTemplateMsgService().sendTemplateMsg(templateMessage);
    }

    @ApiOperation("GetWxMpConfiguration")
    @GetMapping("GetWxMpConfiguration")
    public String GetWxMpConfiguration() {
        return WxUtils.getMessageTemplate("default", "meeting_alert");
    }
}
