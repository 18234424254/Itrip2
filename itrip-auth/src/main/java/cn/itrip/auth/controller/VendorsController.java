package cn.itrip.auth.controller;


import cn.itrip.auth.AuthExcepiton;
import cn.itrip.auth.service.TokenService;
import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.beans.vo.ItripWechatTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.UrlUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/vendors")
public class VendorsController {
    String appId="wx9168f76f000a0d4c";
    @Resource
    private UserService userService;
    @Resource
    TokenService tokenService;


    @GetMapping("/wechat/login")
    public void wechatLogin(HttpServletResponse response) throws IOException {

        String redirectUri="http://localhost:8081/auth/vendors/wechat/callback";
        String url = "https://open.weixin.qq.com/connect/qrconnect?" +
              "appid="+appId+
              "&redirect_uri=" + URLEncoder.encode(redirectUri,"utf-8") +
              "&response_type=code" +
              "&scope=snsapi_login" +
              "&state=STATE#wechat_redirect";
        response.sendRedirect(url);
    }
    @RequestMapping("/wechat/callback")
    public Dto callback(String code, String state, HttpServletRequest request) throws Exception {
        String screat="";
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" +appId+
                "&secret=" +screat+
                "&code=" +code+
                "&grant_type=authorization_code";
        //用户授权返回结果
        String json = UrlUtils.loadURL(url);
        Map map = JSON.parseObject(json, Map.class);

        String access_token = (String) map.get("access_token");
        //授权失败
        if (access_token==null){
            throw new AuthExcepiton("获取授权失败");
        }
        //授权成功-》创建用户记录到数据库
        String openid= (String) map.get("openid");
        ItripUser itripUser = userService.getItripUserByUserCode(openid);

        if (itripUser==null){
            itripUser.setUserCode(openid);
            //微信给的字符串 我们需要的是Long
            // itripUser.setFlatID(openid);
            itripUser.setUserType(1);
            itripUser.setCreationDate(new Date());
            userService.itriptxCreateuserItripUser(itripUser);
        }
        //创建客户端本站的token作为登录凭证
        ItripTokenVO tokenVO = tokenService.processToken(request.getHeader("User-Agent"), itripUser);
        //返回微信的token
        //把两个token返回客户端
        ItripWechatTokenVO wechatTokenVO = new ItripWechatTokenVO(tokenVO.getToken(),tokenVO.getExpTime(),tokenVO.getGenTime());
        wechatTokenVO.setAccessToken(access_token);
        wechatTokenVO.setExpiresIn(String.valueOf(map.get("expires_in")));
        wechatTokenVO.setOpenid(openid);
        wechatTokenVO.setRefreshToken((String)map.get("refresh_token"));
        return DtoUtil.returnDataSuccess(wechatTokenVO);
    }
    @GetMapping("/wechat/user/info")
    public Dto getUserInfo(String accessToken,String openid) throws Exception {
        String url = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" +accessToken+
                "&openid="+openid;
        String json = UrlUtils.loadURL(url);
        Map map = JSON.parseObject(json, Map.class);
        String nickname = (String) map.get("nickname");
        ItripUser itripUser = userService.getItripUserByUserCode(openid);
        if (itripUser==null){
            throw new AuthExcepiton("获取用户信息失败");
        }
        itripUser.setUserName(nickname);
        //更新用户
        userService.updateItpirUser(itripUser);

        return DtoUtil.returnDataSuccess(map);
    }

}
