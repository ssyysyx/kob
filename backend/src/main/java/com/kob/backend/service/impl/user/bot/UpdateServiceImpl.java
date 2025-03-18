package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();

        User user = loginUser.getUser();
        // 上面这三行在django里面就只是request.user

        // 更新哪一个bot
        int bot_id = Integer.parseInt(data.get("bot_id"));

        // 要变哪些信息
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

        // 判断完之后要看bot_id是不是属于该用户的
        Bot bot = botMapper.selectById(bot_id);
        if (bot == null) {
            map.put("error_message", "Bot不存在或已被删除");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("error_message", "没有权限修改Bot");
            return map;
        }

        // 如果有权限，就更新Bot
        Bot new_bot = new Bot(
                bot.getId(),
                user.getId(),
                title,
                description,
                content,
                bot.getRating(),
                bot.getCreatetime(),
                new Date()
        );

        botMapper.updateById(new_bot);
        map.put("error_message","success");
        return map;
    }
}

