import $ from 'jquery'

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true, //是否正在拉取信息，如果正在获取信息，就不要展示登录页面
    },
    // getters一般用不到
    getters: {
    },
    // mutations是用来修改数据的
    mutations: {
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        }
    },
    // 在actions里面调用mutations里面的函数需要用commit+字符串
    // context来自state变量，data来自前端传入
    actions: {
        login(context, data) {
            $.ajax({
                url: "http://localhost:3000/user/account/token/",
                type: "post",
                data: {
                  username: data.username,
                  password: data.password,
                },
                success(resp) {
                    // `resp` 是后端返回的数据
                    if (resp.error_message === "success") {
                        // 登录成功，存储 token
                        localStorage.setItem("jwt_token", resp.token);
                        context.commit("updateToken", resp.token);

                        // 调用前端传入的 success 回调函数
                        data.success(resp);
                    } else {
                        // 登录失败，调用 error 回调函数
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                },      
              });
        },
        getinfo(context, data) {
            $.ajax({
                url: "http://localhost:3000/user/account/info/", // 请求用户信息的 API
                type: "get", // 使用 GET 请求
                // 添加 headers 以携带 token（身份验证）
                headers: {
                    Authorization: "Bearer " + context.state.token, // 认证 token
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        // 调用 mutations 里的 updateUser 方法，更新 Vuex 里的用户信息
                        context.commit("updateUser", {
                            ...resp, // 展开后端返回的数据
                            is_login: true, // 标记用户已登录
                        });
            
                        // 调用前端传入的成功回调，让 .vue 文件里的逻辑执行成功时的操作
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    // 请求失败，调用 error 回调
                    data.error(resp);
                }
            });
        },
        logout(context) {
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        },
    },
    modules: {
    }
}

