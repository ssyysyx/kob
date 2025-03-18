package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 如果想让ID自增的话要加上注解
    @TableId(type = IdType.AUTO)

    private Integer id;
    private String username;
    private String password;
    private String photo;
}

