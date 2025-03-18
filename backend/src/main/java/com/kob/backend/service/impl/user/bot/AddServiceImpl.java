package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service  // 让 Spring 识别此类为 Service 组件，并自动管理
public class AddServiceImpl implements AddService {

    @Autowired
    private BotMapper botMapper;  // 自动注入 `BotMapper`，用于数据库操作

    @Override
    public Map<String, String> add(Map<String, String> data) {
        // 获取当前登录用户信息
        // `SecurityContextHolder` 存储了当前登录用户的安全上下文信息
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        // 获取封装的 `UserDetailsImpl`，从中获取 `User` 对象
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();  // 获取当前用户对象

        // 获取前端传递的数据（标题、描述、代码）
        String title = data.get("title");         // Bot 标题
        String description = data.get("description");  // Bot 描述
        String content = data.get("content");     // Bot 代码

        Map<String, String> map = new HashMap<>();

        // 校验 `title`（不能为空，且长度 ≤ 100）
        if (title == null || title.length() == 0) {
            map.put("error_message", "标题不能为空");
            return map;
        }

        if (title.length() > 100) {
            map.put("error_message", "标题长度不能大于100");
            return map;
        }

        // 校验 `description`
        // 如果 `description` 为空，则赋予默认值
        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下～";
        }

        // 确保 `description` 长度不超过 300
        if (description.length() > 300) {
            map.put("error_message", "Bot描述的长度不能大于300");
            return map;
        }

        // 校验 `content`（代码）
        if (content == null || content.length() == 0) {
            map.put("error_message", "代码不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("error_message", "代码长度不能超过10000");
            return map;
        }

        // 创建 `Bot` 对象，设置默认 `rating = 1500`
        Date now = new Date();  // 获取当前时间
        Bot bot = new Bot(
                null,              // `id`（数据库自动生成）
                user.getId(),       // 绑定当前用户 ID
                title,              // 标题
                description,        // 描述（可能是默认值）
                content,            // 代码
                1500,               // 初始分数 1500
                now,                // 创建时间
                now                 // 修改时间
        );

        // 将 `bot` 插入数据库
        botMapper.insert(bot);

        // 返回成功消息
        map.put("error_message", "success");
        return map;
    }
}