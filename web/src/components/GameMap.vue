<template>
    <!-- 游戏地图的容器，包含一个 canvas 元素 -->
    <div ref="parent" class="gamemap">
        <!-- 游戏内容绘制到这个 canvas 画布里面 -->
         <canvas ref="canvas" tabindex="0"></canvas>  <!-- canvas 元素用来作为游戏绘图的区域 -->
    </div>
</template>

<script>
// 导入 GameMap 类，用来处理游戏地图的逻辑
import { GameMap } from "@/assets/scripts/GameMap";

// 引入 Vue 的 ref 和 onMounted，ref 用来创建响应式数据，onMounted 用来处理组件挂载后的逻辑
import { ref, onMounted } from 'vue';
import { useStore } from "vuex";

export default {
    setup() {
        // 在 Vue 3 中，定义响应式变量需要使用 ref
        const store = useStore();
        let parent = ref(null);  // 用来引用父容器 div 元素
        let canvas = ref(null);  // 用来引用 canvas 元素

        // 使用 onMounted 钩子，组件挂载完后执行的操作
        onMounted(() => {
            // 创建 GameMap 对象，并传入 canvas 的 2D 上下文和父元素
            new GameMap(canvas.value.getContext('2d'), parent.value, store)
        })

        // 返回响应式数据，以便模板中使用
        return {
            parent,
            canvas
        }
    }
}

</script>

<style scoped>
/* scoped 样式仅对当前组件有效 */
div.gamemap {
    /* 设置 div 容器的宽度和高度为父元素的 100% */
    width: 100%;  /* 宽度为父元素的 100% */
    height: 100%; /* 高度为父元素的 100% */

    /* 使用 Flexbox 布局，将内容居中 */
    display: flex;  /* 使用 flex 布局 */
    justify-content: center;  /* 水平居中 */
    align-items: center;  /* 垂直居中 */
}
</style>
