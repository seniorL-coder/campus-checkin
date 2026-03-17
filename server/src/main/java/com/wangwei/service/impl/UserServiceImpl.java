package com.wangwei.service.impl;

import com.wangwei.context.BaseContext;
import com.wangwei.dto.LoginDTO;
import com.wangwei.entity.User;
import com.wangwei.exception.LoginFailedException;
import com.wangwei.exception.PasswordErrorException;
import com.wangwei.mapper.UserMapper;
import com.wangwei.properties.JwtProperties;
import com.wangwei.service.UserService;
import com.wangwei.utils.JwtUtils;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param loginDTO
     * @return
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            throw new LoginFailedException("用户名或密码不能为空");
        }
        LoginVO loginVO = userMapper.login(loginDTO);
        // 校验用户是否存在
        if (loginVO == null) {
            throw new PasswordErrorException("用户名或密码错误");
        }
        String key = "login:user:" + loginVO.getId();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginVO.getUsername());
        claims.put("role", loginVO.getRole());
        claims.put("userId", loginVO.getId());
        String token = jwtUtils.generateToken(claims);
        loginVO.setToken(token);
        // 存入Redis , 这里 直接用 key 作为键, 覆盖掉旧的值 实现 单点登录
        redisTemplate.opsForValue().set(key, token, jwtProperties.getTtl(), TimeUnit.SECONDS);
        return loginVO;
    }

    /**
     * 登出
     *
     * @param userId
     */
    @Override
    public void logout(Integer userId) {
        // 清理Redis中对应的token
        String key = "login:user:" + userId;
        redisTemplate.delete(key);
    }

    @Override
    public UserVO info() {
        Long userId = BaseContext.getCurrentId();
        return userMapper.getUserInfoById(userId);
    }

    /**
     * 根据班级ID获取学生列表
     *
     * @param classIds 班级ID 列表，用逗号分隔
     * @return 学生列表
     */
    @Override
    public List<UserVO> getStudentsByClassIds(List<Integer> classIds) {
        return userMapper.getStudentsByClassIds(classIds);
    }
}
