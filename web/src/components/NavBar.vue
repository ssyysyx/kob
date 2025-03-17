<template>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <!-- <a class="navbar-brand" href="/">King Of Bots</a> -->
        <router-link class="navbar-brand" :to="{name: 'home'}">King Of Bots</router-link>
        <div class="collapse navbar-collapse" id="navbarText">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <!-- <a class="nav-link" aria-current="page" href="/pk/">对战</a> -->
              <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name:'pk_index'}">对战</router-link>
            </li>
            <li class="nav-item">
              <!-- <a class="nav-link" href="/record/">对局列表</a> -->
              <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name:'record_index'}">对局列表</router-link>
            </li>
            <li class="nav-item">
              <!-- <a class="nav-link" href="/ranklist/">排行榜</a> -->
              <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name:'ranklist_index'}">排行榜</router-link>
            </li>
          </ul>
          <ul class="navbar-nav" v-if="$store.state.user.is_login">
            <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                {{ $store.state.user.username }}
            </a>
            <ul class="dropdown-menu">
                <li>
                    <!-- <a class="dropdown-item" href="/user/bot/">我的Bot</a> -->
                    <router-link class="dropdown-item" :to="{name:'user_bot_index'}">我的Bot</router-link>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#" @click="logout">退出</a></li>
            </ul>
            </li>
          </ul>
          <ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">
            <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'user_account_login' }" role="button">
                登录
            </router-link>
            </li>
            <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'user_account_register' }" role="button">
                注册
            </router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </template>
  

  <script>
  // 引入 vue-router 中的 useRoute 钩子，获取当前路由信息
  import { useRoute } from 'vue-router'

  // 引入 vue 中的 computed 函数，用于创建响应式的计算属性
  import { computed } from 'vue'

  import { useStore } from 'vuex'

  export default {
      setup() {
          // 使用 useRoute 钩子获取当前路由对象
          const route = useRoute();
          const store = useStore();

          // 使用 computed 创建一个计算属性 route_name，实时获取路由的 name
          let route_name = computed(() => route.name)

          const logout = () => {
              store.dispatch("logout");
          }

          // 返回计算属性 route_name，以便模板中使用
          return {
              route_name,
              logout
          }
      }
  }
</script>


<style scoped>

</style>