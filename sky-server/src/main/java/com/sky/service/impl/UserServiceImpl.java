package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    public static String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;

    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 调用接口服务
        String openid = getOpenid(userLoginDTO.getCode());
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.getByOpenId(openid);
        if (user == null) {
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
        }
        userMapper.insert(user);

        //
        return user;
    }

    private String getOpenid(String code) {
        Map<String, String> map = new HashMap();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("grant_type", "authorization_code");
        map.put("js_code", code);
        String doGet = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(doGet);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
