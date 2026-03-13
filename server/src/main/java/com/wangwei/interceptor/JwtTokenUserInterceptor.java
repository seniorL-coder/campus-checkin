package com.wangwei.interceptor;

import com.wangwei.context.BaseContext;
import com.wangwei.properties.JwtProperties;
import com.wangwei.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Order(1) // 指定拦截器的顺序，数字越小，优先级越高
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            log.info("当前拦截到的是静态资源");
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = jwtUtils.parseToken(token);
            log.info("claims:{}", claims);
            Long userId = Long.valueOf(claims.get("userId").toString());
            // 单点登录: 如果当前用户提交的令牌与redis中保存的不一致, 说明用户换了设备, 将当前用户踢下线
            String key = "login:user:" + userId;
            String redisToken = (String) redisTemplate.opsForValue().get(key);
            log.info("redisToken:{}", redisToken);
            if (!Objects.equals(token, redisToken)) {
                log.info("用户已换设备, 请重新登录");
                response.setStatus(401);
                return false;
            }
            log.info("当前用户id：{}", userId);
            BaseContext.setCurrentId(userId); // 记录当前登录的用户ID
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentId();
    }
}
