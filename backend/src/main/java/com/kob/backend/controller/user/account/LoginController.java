package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// RestController返回json
@RestController
public class LoginController {
    // 注入刚刚定义的接口
    @Autowired
    private LoginService loginService;

    // 登录是post，get请求会将用户名和密码参数放到url链接里面，是明文传输的不安全，post是看不到明文的，安全一些。
    @PostMapping("/user/account/token/") // 定义完之后一定要记得去config/SecurityConfig.java公开化放行
    // 定义函数
    // getToken需要传两个信息（userid，password），需要从post请求中把我们的参数拿出来，可以将post请求里的参数放到map（字典）里面。
    // @RequestParam 可以把post里的参数放到一个Map里面
    public Map<String, String> getToken(@RequestParam Map<String, String> map) {
        // 从map中取出用户名
        String username = map.get("username");
        String password = map.get("password");
        return loginService.getToken(username, password);
    }
}

