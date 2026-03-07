package com.wangwei.service.impl;

import com.wangwei.dto.UserLoginDTO;
import com.wangwei.entity.User;
import com.wangwei.mapper.UserMapper;
import com.wangwei.properties.JwtProperties;
import com.wangwei.service.UserService;
import com.wangwei.utils.JwtUtils;
import com.wangwei.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;

    @Override
    public UserVO login(UserLoginDTO userLoginDTO) {
        if (userLoginDTO.getUsername() == null || userLoginDTO.getPassword() == null) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        UserVO userVO = userMapper.login(userLoginDTO);
        // 校验用户是否存在
        if (userVO == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        String key = "login:user:" + userVO.getId();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userVO.getUsername());
        claims.put("role", userVO.getRole());
        String token = jwtUtils.generateToken(claims);
        userVO.setToken(token);
        // 存入Redis , 这里 直接用 key 作为键, 覆盖掉旧的值 实现 单点登录
        redisTemplate.opsForValue().set(key, token, jwtProperties.getTtl(), TimeUnit.SECONDS);

        return userVO;
    }
}
