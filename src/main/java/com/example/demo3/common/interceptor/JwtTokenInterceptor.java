package com.example.demo3.common.interceptor;


import com.example.demo3.common.Result;
import com.example.demo3.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置响应为JSON
        response.setContentType("application/json;charset=UTF-8");
        // 从标准Authorization头拿
        String authorization = request.getHeader("Authorization");
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            // 截掉 "Bearer " 前缀
            token = authorization.substring(7);
        }

        // ... existing code ...
        if(token == null || token.isBlank()){
            Result<?> result = Result.fail(401,"未登录，请携带token");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }


        try {

            Long userId = JwtUtil.getUserIdByToken(token);
            Integer role = JwtUtil.getRoleByToken(token);

            request.setAttribute("userId",userId);
            request.setAttribute("role",role);
            return true;
        }catch (ExpiredJwtException e){
            log.warn("token过期");
            Result<?> result = Result.fail(401,"登录已过期，请重新登录");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }catch (SignatureException e){
            log.warn("token签名错误，令牌被篡改");
            Result<?> result = Result.fail(401,"token非法");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }catch (MalformedJwtException e){
            log.warn("token格式错误");
            Result<?> result = Result.fail(401,"token格式错误");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }catch (Exception e){
            log.error("token解析异常",e);
            Result<?> result = Result.fail(401,"认证失败");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }

    }

}