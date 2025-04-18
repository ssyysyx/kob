package com.kob.backend.mapper; // 指定该接口的包路径

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 导入 MyBatis-Plus 提供的基础 Mapper 接口
import com.kob.backend.pojo.User; // 导入 User 实体类
import org.apache.ibatis.annotations.Mapper; // 导入 MyBatis 的 @Mapper 注解

/**
 * UserMapper 是 MyBatis-Plus 提供的 Mapper 接口，用于操作 User 表的数据。
 * 继承 BaseMapper<User> 后，自动具备基本的 CRUD（增删改查）功能。
 */
@Mapper // 标记为 MyBatis 的 Mapper 接口，Spring Boot 会自动扫描并管理
public interface UserMapper extends BaseMapper<User> {
    // 由于 BaseMapper<T> 已经提供了基本的数据库操作方法，这里可以直接使用
    // 例如：
    // - selectById(id)        -> 根据 ID 查询用户
    // - selectList(null)      -> 查询所有用户
    // - insert(user)          -> 插入用户数据
    // - deleteById(id)        -> 根据 ID 删除用户
    // - updateById(user)      -> 根据 ID 更新用户信息

    // 如果需要自定义 SQL 语句，可以在这里添加方法，并在 XML 文件或使用 @Select 注解定义 SQL
}