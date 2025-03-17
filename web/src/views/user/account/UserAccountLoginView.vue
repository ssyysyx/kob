<template>
    <ContentField v-if="!$store.state.user.pulling_info">
        <!-- 登录界面 -->
        <div class="row justify-content-md-center">
            <div class="col-3">
                <!-- `@submit.prevent="login"` 
                    1. `@submit` 监听 `<form>` 提交事件
                    2. `.prevent` 阻止表单默认提交（避免刷新页面）
                    3. 触发 `login` 方法，执行登录逻辑 -->
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <!-- `v-model` 双向绑定，用户输入的内容会同步到 `username` 变量 -->
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <!-- `v-model` 绑定密码输入框，与 `password` 变量同步 -->
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <!-- 显示错误信息，如果 `error_message` 变量有值，就会显示 -->
                    <div class="error-message">{{ error_message }}</div>
                    <!-- 提交按钮，点击时触发 `login` 方法 -->
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue' // 引入 `ContentField` 组件
import { useStore } from 'vuex' // 导入 Vuex，方便访问全局状态管理
import { ref } from 'vue' // `ref` 是 Vue 3 的响应式 API，用于创建响应式变量
import router from '../../../router/index'

export default {
    components: {
        ContentField // 注册 `ContentField` 组件，使其可以在 `template` 中使用
    },
    setup() {
        const store = useStore(); // 获取 Vuex `store` 实例，用于操作全局状态
        let username = ref(''); // 创建 `username` 响应式变量，绑定到输入框
        let password = ref(''); // 创建 `password` 响应式变量，绑定到输入框
        let error_message = ref(''); // 创建 `error_message` 响应式变量，存储错误信息

        const jwt_token = localStorage.getItem("jwt_token");
        if (jwt_token) {
            // 更新token的函数，使用mutation里的函数要用commit，使用actions里面的函数用dispatch
            store.commit("updateToken", jwt_token);
            // 从云端获取信息
            store.dispatch("getinfo", {
                success() {
                    router.push({ name: "home" });
                    store.commit("updatePullingInfo", false);
                },
                error() {
                    store.commit("updatePullingInfo", false);
                }
            })
        } else {
            store.commit("updatePullingInfo", false);
        }

        // `login` 方法：当用户点击提交按钮时触发
        const login = () => {
            error_message.value = ""; // 清空之前的错误信息

            // 触发 Vuex `login` action，向后端发送登录请求
            store.dispatch("login", {
                username: username.value, // 传入用户名
                password: password.value, // 传入密码
                success() { // 登录成功的回调函数
                    // 登录成功后，调用 `getinfo` 获取用户信息
                    store.dispatch("getinfo", {
                        // 定义回调函数
                        success() {
                            // 获取用户信息成功后，跳转到 `home` 页面
                            router.push({ name: 'home'});
                            console.log(store.state.user);
                        }
                    })
                },
                error() { // 登录失败的回调函数
                    // 如果用户名或密码错误，更新 `error_message` 提示用户
                    error_message.value = "用户名或密码错误"; 
                }
            })
        }

        return {
            username, // 让 `template` 能够访问 `username`
            password, // 让 `template` 能够访问 `password`
            error_message, // 让 `template` 能够访问 `error_message`
            login, // 让 `template` 能够访问 `login` 方法
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}
div.error-message {
    color: red;
}
</style>


