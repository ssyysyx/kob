<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <!-- 画一个小的白色区域 -->
                    <div class="card" style="margin-top: 20px;">
                        <div class="card-body">
                            <img :src="$store.state.user.photo" alt="" style="width: 100%">
                        </div>
                    </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                        <span style="font-size: 130%">我的Bot</span>
                        <!-- 向右对齐：float-end -->
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot
                        </button>
 
                        <!-- Modal -->
                        <div class="modal fade" id="add-bot-btn" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title fs-5" >创建Bot</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    
                                    <div class="modal-body">
                                        <form>
                                            <div class="mb-3">
                                                <label for="add-bot-title" class="form-label">名称</label>
                                                <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入bot名称">
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-description" class="form-label">简介</label>
                                                <textarea v-model="botadd.description" type="text" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-code" class="form-label">代码</label>
                                                <!-- <textarea v-model="botadd.content" type="text" class="form-control" id="add-bot-code" rows="7" placeholder="请编写Bot代码"></textarea> -->
                                                <VAceEditor
                                                    v-model:value="botadd.content"
                                                    @init="editorInit"
                                                    lang="c_cpp"
                                                    theme="textmate"
                                                    style="height: 300px" />
                                            </div>
                                        </form>
                                    </div>

                                    <div class="modal-footer">
                                        <div class="error-message">{{ botadd.error_message }}</div>
                                        <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- 每个bot占一行，v-for必须要绑定一个关键字key-->
                                 <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary" style="margin-right: 10px;" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id">修改</button>
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>

                                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title fs-5">创建Bot</h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    
                                                    <div class="modal-body">
                                                        <form>
                                                            <div class="mb-3">
                                                                <label for="add-bot-title" class="form-label">名称</label>
                                                                <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入bot名称">
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="add-bot-description" class="form-label">简介</label>
                                                                <textarea v-model="bot.description" type="text" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="add-bot-code" class="form-label">代码</label>
                                                                <!-- <textarea v-model="bot.content" type="text" class="form-control" id="add-bot-code" rows="7" placeholder="请编写Bot代码"></textarea> -->
                                                                <VAceEditor
                                                                    v-model:value="bot.content"
                                                                    @init="editorInit"
                                                                    lang="c_cpp"
                                                                    theme="textmate"
                                                                    style="height: 300px" />
                                                            </div>
                                                        </form>
                                                    </div>

                                                    <div class="modal-footer">
                                                        <div class="error-message">{{ botadd.error_message }}</div>
                                                        <button type="button" class="btn btn-primary" @click="update_bot(bot)">保存修改</button>
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                 </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';

export default {
    components: {
        VAceEditor
    },

    setup() {
        ace.config.set(
            "basePath", 
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

        const store = useStore();
        // 要返回bots列表，一开始是空的
        let bots = ref([]);

        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        })

        const refresh_bots = () => {
            $.ajax({
                url: "http://localhost:3000/user/bot/getlist/",
                type: "get",
                // 不需要参数data
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                },
              });
        }

        // 定义完函数之后需要执行一下
        refresh_bots();

        const add_bot = () => {
            botadd.error_message = "";
            $.ajax({
                url: "http://localhost:3000/user/bot/add/",
                type: "post",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") { 
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        // 在前端里面，很多地方都需要加#，如果是class就加.
                        Modal.getInstance("#add-bot-btn").hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }

        const update_bot = (bot) => {
            botadd.error_message = "";
            $.ajax({
                url: "http://localhost:3000/user/bot/update/",
                type: "post",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") { 
                        // 在前端里面，很多地方都需要加#，如果是class就加.
                        Modal.getInstance('#update-bot-modal-' + bot.id).hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }

        const remove_bot = (bot) => {
            $.ajax({
                url: "http://localhost:3000/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    }
                }
            })
        } 

        return {
            bots,
            botadd,
            add_bot,
            update_bot,
            remove_bot,
        }
    }
}
</script>

<style scoped>
div.error-message {
    color: red;
}
</style>


<!-- <template>
    <ContentField>
        我的Bot
    </ContentField>
</template>2

<script>
import ContentField from '../../../components/ContentField.vue'
import $ from 'jquery'
// 从全局变量中读取token
import { useStore } from 'vuex'

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        $.ajax({
            url: "http://localhost:3000/user/bot/add/",
            type: "post",
            data: {
                title: "Bot的标题",
                description: "Bot的描述",
                content: "Bot的代码",
            },
            headers:{
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })

        $.ajax({
            url: "http://localhost:3000/user/bot/remove/",
            type: "post",
            data: {
                bot_id: 6,
            },
            headers:{
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })

        $.ajax({
            url: "http://localhost:3000/user/bot/update/",
            type: "post",
            data: {
                bot_id: 8,
                title: "Bot的标题7",
                description: "Bot的描述7",
                content: "Bot的代码7",
            },
            headers:{
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })

        $.ajax({
            url: "http://localhost:3000/user/bot/getlist/",
            type: "get",
            headers:{
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })
    }
}
</script>

<style scoped>

</style> -->