package com.kob.backend.service.impl.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.LoginService;
import com.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    // Map<String, String>是返回json格式的标准类型
    public Map<String, String> getToken(String username, String password) {
        // 封装username和password成密文
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        // 获取认证后的用户详情对象
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        // 从用户详情对象中获取User实体 这里User是pojo的
        User user = loginUser.getUser();

        //取完之后要把用户的userid封装成jwt token
        String jwt = JwtUtil.createJWT(user.getId().toString());

        Map<String, String> map = new HashMap<>();
        // 如果能执行到这里，一定成功
        map.put("error_message", "success");
        map.put("token", jwt);

        return map;
    }
}

