<template>
    <ContentField>
        <!-- 注册界面 -->
        <div class="row justify-content-md-center">
            <div class="col-3">
                <!-- `@submit.prevent="register"` 
                    1. `@submit` 监听 `<form>` 提交事件
                    2. `.prevent` 阻止表单默认提交（避免页面刷新）
                    3. 触发 `register` 方法，执行注册逻辑 -->
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <!-- `v-model` 绑定输入框，实现数据双向绑定 -->
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <!-- `v-model` 绑定密码输入框，实现数据双向绑定 -->
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <!-- `v-model` 绑定确认密码输入框，与 `confirmedPassword` 变量同步 -->
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="请再次输入密码">
                    </div>
                    <!-- 显示错误信息，如果 `error_message` 变量有值，就会显示 -->
                    <div class="error-message">{{ error_message }}</div>
                    <!-- 提交按钮，点击后触发 `register` 方法 -->
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue' // 引入 `ContentField` 组件
import { ref } from 'vue' // Vue 3 响应式 API，用于创建响应式变量
import router from '../../../router/index' // Vue Router，用于页面跳转
import $ from 'jquery' // 引入 jQuery 进行 AJAX 请求

export default {
    components: {
        ContentField // 注册 `ContentField` 组件，使其可以在 `template` 中使用
    },
    setup() {
        let username = ref(''); // 创建 `username` 响应式变量，绑定到输入框
        let password = ref(''); // 创建 `password` 响应式变量，绑定到输入框
        let confirmedPassword = ref(''); // 创建 `confirmedPassword` 响应式变量，绑定到输入框
        let error_message = ref(''); // 创建 `error_message` 响应式变量，存储错误信息

        // `register` 方法：当用户点击提交按钮时触发
        const register = () => {
            $.ajax({
                url: "http://localhost:3000/user/account/register/", // 发送请求到后端注册接口
                type: "post", // 使用 POST 请求
                data: {
                    username: username.value, // 用户名
                    password: password.value, // 密码
                    confirmedPassword: confirmedPassword.value, // 确认密码
                },
                success(resp) { // 请求成功时的回调函数
                    if (resp.error_message === "success") {
                        // 注册成功后跳转到登录页面
                        router.push({ name: "user_account_login" });
                    } else {
                        // 失败则显示错误信息
                        error_message.value = resp.error_message;
                    }
                },
                error(resp) { // 请求失败时的回调函数
                    console.log(resp); // 输出错误信息到控制台
                },
            })
        }

        return {
            username, // 让 `template` 访问 `username` 变量
            password, // 让 `template` 访问 `password` 变量
            confirmedPassword, // 让 `template` 访问 `confirmedPassword` 变量
            error_message, // 让 `template` 访问 `error_message` 变量
            register, // 让 `template` 访问 `register` 方法
        }
    }
}
</script>

<style scoped>
/* 让按钮占满父容器的宽度 */
button {
    width: 100%;
}
/* 设置错误信息的颜色 */
div.error-message {
    color: red;
}
</style>