import { createStore } from 'vuex'
import ModuleUser from './user'
// ModulePk是自己起名的，用来代表从 pk.js 这个模块中 export default 出来的对象
import ModulePk from './pk'

export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user: ModuleUser,
    pk: ModulePk,
  }
})

