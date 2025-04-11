<template>
    <!-- 页面中只渲染一个 PlayGround 组件 -->
    <PlayGround v-if="$store.state.pk.status === 'playing'"/>
    <MatchGround v-if="$store.state.pk.status === 'matching'"/>
    <ResultBoard v-if="$store.state.pk.loser != 'none'" />
</template>

<script>
// 引入 PlayGround 组件，用于显示游戏界面等功能
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import ResultBoard from '../../components/ResultBoard.vue'

// onMounted：组件挂载时执行；onUnmounted：组件卸载时执行
import { onMounted, onUnmounted } from 'vue'

// 引入 vuex 的 useStore，用于访问全局状态（store）
import { useStore } from 'vuex'

export default {
    components: {
        // 注册局部组件
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup() {
        // 获取全局的 Vuex store 实例
        const store = useStore();

        // 根据当前用户的 id 生成 WebSocket 地址（用于后端身份识别）
        const socketUrl = `ws://localhost:3000/websocket/${store.state.user.token}/`

        // 声明一个变量用于保存 WebSocket 实例
        let socket = null;

        // 组件挂载时执行：创建 WebSocket 连接
        onMounted(() => {
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            })
            // WebSocket 是 JavaScript 自带的类，用于客户端和服务端之间的长连接通信
            socket = new WebSocket(socketUrl);

            // 连接建立成功时触发
            socket.onopen = () => {
                console.log("connected!");
                store.commit("updateSocket", socket);
            }

            // 接收到服务器发送的消息时触发
            socket.onmessage = msg => {
                const data = JSON.parse(msg.data); // 将 JSON 字符串解析为对象
                console.log(data); // 打印收到的消息数据
                if (data.event === "start-matching") { // 匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    // 2000毫秒之后再执行，调试的时候可以先200ms
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 200);
                    store.commit("updateGame", data.game);
                } else if (data.event === "move") {
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                } else if (data.event === "result") {
                    // 先取出蛇
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;

                   if (data.loser === "all" || data.loser === "A") {
                    snake0.status = "die";
                   } 
                   if (data.loser === "all" || data.loser === "B") {
                    snake1.status = "die";
                   }
                   store.commit("updateLoser", data.loser);
                }
            }

            // 连接关闭时触发（正常关闭或异常断开）
            socket.onclose = () => {
                console.log("disconnected!");
            }
        });

        // 组件卸载时执行：主动关闭 WebSocket 连接，防止内存泄漏
        onUnmounted(() => {
            socket.close();
            store.commit("updateStatus", "matching");
        })
    }
}
</script>

<style scoped>
/* 这里目前没有样式，scoped 表示样式仅作用于当前组件 */
</style>


