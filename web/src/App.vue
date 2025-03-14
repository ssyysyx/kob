<template>
  <NavBar />
  <router-view/>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// import $ from 'jquery'

export default {
  components: {
    NavBar
  },

  setup() {
    // $.ajax({
    //   url: "http://localhost:3000/user/account/token/",
    //   type: "post",
    //   data: {
    //     username: 'syx',
    //     password: "psyx",
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp);
    //   },      
    // });

    // $.ajax({
    //   url: "http://localhost:3000/user/account/info/",
    //   type: "get",
    //   // 报文需要传输到authorization里面，这不是固定的，但是它是官方推荐的。
    //   headers: {
    //     Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2M2E2NjhlY2Q4MmQ0OTAzYTM3ZjE2Y2UwZDIzYjc3NyIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTc0MTg3Mzk2MywiZXhwIjoxNzQzMDgzNTYzfQ.tGr-UNtc0WeP27Wxm2kQ3fZuQ9XT6AGBTt7GFEbpi38"
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp);
    //   }
    // });

    // $.ajax({
    //   url:"http://localhost:3000/user/account/register/",
    //   type: "post",
    //   // 注册不需要验证，不需要headers，但是需要数据
    //   data: {
    //     username: "syxx",
    //     password: "psyxx",
    //     confirmedPassword: "psyxx",
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp);
    //   }
    // });
  }
}
</script>

<style>
body {
  background-image: url("@/assets/images/background.png");
  background-size: cover;
}
</style>

