package com.kob.backend.service.user.bot;

import com.kob.backend.pojo.Bot;

import java.util.List;

public interface GetListService {
    //每个人返回自己的bot，自己的userId其实是存在token里面的，它不需要传参数。
    List<Bot> getList();
}

