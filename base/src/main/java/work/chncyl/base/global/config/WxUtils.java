package work.chncyl.base.global.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.draft.WxMpAddDraft;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftArticles;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * 微信
 */
@Component
@ConditionalOnBean(WxMpService.class)
public class WxUtils {
    private static WxMpProperties properties;
    private static WxMpService wxService;
    @Autowired
    private WxMpProperties wxMpProperties;
    @Autowired
    private WxMpService wxMpService;

    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        return gson.toJson(obj);
    }

    public static String getAppId(String appName) {
        String appId = properties.getConfigs().stream().filter(t -> t.getAppName().equals(appName)).map(t -> t.getAppId()).findFirst().orElse(null);
        if (StringUtils.isEmpty(appId))
            throw new RuntimeException("未配置【" + appName + "】的微信配置");
        return appId;
    }

    public static WxMpProperties.MpConfig getWxConfigByAppName(String appName) {
        return properties.getConfigs().stream().filter(t -> t.getAppName().equals(appName)).findFirst().orElse(null);
    }

    public static String getMessageTemplate(String appName, String templateName) {
        WxMpProperties.MpConfig appIdentity = properties.getConfigs().stream().filter(t -> t.getAppName().equals(appName)).findFirst().orElse(null);
        if (appIdentity == null)
            return "";
        return appIdentity.getMessageTemplate().stream().filter(t -> t.getName().equals(templateName)).map(t -> t.getId()).findFirst().orElse(null);
    }

    /**
     * 使用code获取用户openid
     */
    public static String getUserOpenId(String code) throws RuntimeException, WxErrorException {
        WxOAuth2AccessToken accessToken = getAccessToken(code);
        return accessToken.getOpenId();
    }

    public static WxOAuth2UserInfo getWechatInfo(String code) throws WxErrorException {
        WxOAuth2AccessToken accessToken = getAccessToken(code);
        return wxService.getOAuth2Service().getUserInfo(accessToken, "zh_CN");
    }

    /**
     * 增加永久图片素材
     *
     * @param fileName 名称
     * @param file     文件
     * @return 素材信息，包含素材的mediaId和访问url
     * @throws WxErrorException
     */
    public static WxMpMaterialUploadResult addImageMaterial(String fileName, File file) throws WxErrorException {
        WxMpMaterial material = new WxMpMaterial();
        material.setName(fileName);
        material.setFile(file);

        return wxService.getMaterialService().materialFileUpload(WxConsts.MaterialType.IMAGE, material);
    }

    /**
     * 发布草稿
     *
     * @param articles 草稿内容
     * @return 草稿的mediaId
     * @throws WxErrorException
     */
    public static String sendDraft(List<WxMpDraftArticles> articles) throws WxErrorException {
        WxMpAddDraft d = new WxMpAddDraft(articles);
        return wxService.getDraftService().addDraft(d);
    }

    /**
     * 草稿正式发布
     *
     * @param draftMediaId 草稿的mediaId
     * @return 发布后的文章
     */
    public static String publishDraft(String draftMediaId) throws WxErrorException {
        return wxService.getFreePublishService().submit(draftMediaId);
    }

    private static WxOAuth2AccessToken getAccessToken(String code) throws WxErrorException {
        return wxService.getOAuth2Service().getAccessToken(code);
    }

    private static String getAccessToken() throws WxErrorException {
        return wxService.getAccessToken();
    }


    @PostConstruct
    private void init() {
        properties = wxMpProperties;
        wxService = wxMpService;
    }
}
