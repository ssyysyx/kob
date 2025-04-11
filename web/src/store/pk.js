// import $ from 'jquery'

export default {
    state: {
        status: "matching", // matching表示匹配界面，playing表示对战界面，其实相当于一个router，帮我们自动切换匹配界面和对战界面。
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
        a_id: 0,
        a_sx: 0,
        a_sy: 0,
        b_id: 0,
        b_sx: 0,
        b_sy: 0,
        gameObject: null,
        loser: "none", // none、all、A、B
    },
    // getters一般用不到
    getters: {
    },
    // mutations是用来修改数据的
    mutations: {
        // 更新 socket 实例
        updateSocket(state, socket) {
            state.socket = socket;
        },

        // 更新对手信息，包括用户名和头像
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },

        // 更新当前状态（matching <-> playing）
        updateStatus(state, status) {
            state.status = status;
        },
        
        updateGame(state, game) {
            state.gamemap = game.map;
            state.a_id = game.a_id;
            state.a_sx = game.a_sx;
            state.a_sy = game.a_sy;
            state.b_id = game.b_id;
            state.b_sx = game.b_sx;
            state.b_sy = game.b_sy;
        },

        updateGameObject(state, gameObject) {
            state.gameObject = gameObject;
        },

        updateLoser(state, loser) {
            state.loser = loser;
        }
    },
    // 在actions里面调用mutations里面的函数需要用commit+字符串
    // context来自state变量，data来自前端传入
    actions: {

    },
    modules: {
    }
}
3
