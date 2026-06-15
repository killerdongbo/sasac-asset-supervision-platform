package com.sasac.platform.miniapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.miniapp.dto.*;
import com.sasac.platform.auth.entity.User;
import com.sasac.platform.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WxMiniAppService {

    private final UserMapper userMapper;
    private final AssetMapper assetMapper;

    @Value("${wx.miniapp.appid:}")
    private String appId;

    @Value("${wx.miniapp.secret:}")
    private String appSecret;

    public WxLoginResponse login(WxLoginRequest request) {
        String openId = code2Session(request.getCode());

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenId, openId)
        );

        if (user == null) {
            throw new BusinessException("用户未绑定，请联系管理员");
        }

        return WxLoginResponse.builder()
                .token(generateToken(user))
                .openId(openId)
                .realName(user.getRealName())
                .userId(user.getId())
                .tenantId(user.getTenantId())
                .build();
    }

    public Asset queryByCode(String assetCode, Long tenantId) {
        Asset asset = assetMapper.selectOne(
                new LambdaQueryWrapper<Asset>()
                        .eq(Asset::getAssetCode, assetCode)
                        .eq(Asset::getTenantId, tenantId)
        );
        if (asset == null) throw new BusinessException("资产不存在");
        return asset;
    }

    public List<Asset> queryMyAssets(Long userId, Long tenantId) {
        User user = userMapper.selectById(userId);
        if (user == null) return List.of();
        return assetMapper.selectList(
                new LambdaQueryWrapper<Asset>()
                        .eq(Asset::getTenantId, tenantId)
                        .eq(Asset::getCustodian, user.getRealName())
                        .orderByDesc(Asset::getCreatedAt)
                        .last("LIMIT 50")
        );
    }

    private String code2Session(String code) {
        if (appId.isBlank() || appSecret.isBlank()) {
            return "mock_openid_" + code;
        }
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code
        );
        RestTemplate rest = new RestTemplate();
        Map<?, ?> result = rest.getForObject(url, Map.class);
        if (result == null || result.containsKey("errcode")) {
            throw new BusinessException("微信登录失败");
        }
        return (String) result.get("openid");
    }

    private String generateToken(User user) {
        return "miniapp_token_" + user.getId() + "_" + System.currentTimeMillis();
    }
}
