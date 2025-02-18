// 定义一个空数组，用于存储所有的游戏对象
const AC_GAME_OBJECTS = [];

// 定义一个 AC_GAME_OBJECT 类，用来表示游戏中的各个对象
export class AcGameObject {
    // 构造函数：每当创建一个新的 AC_GAME_OBJECT 实例时，将其加入到 AC_GAME_OBJECTS 数组中
    constructor() {
        AC_GAME_OBJECTS.push(this);  // 将当前实例（this）加入数组中，便于后续管理
        this.timedelta = 0;  // 用于存储上一帧到当前帧的时间间隔
        this.has_called_start = false;  // 用于判断 start 方法是否已经调用过
    }

    // start 方法：该方法在游戏对象创建时只会执行一次
    start() { 
        // 可以在这里初始化游戏对象，执行一些只需要执行一次的操作
    }

    // update 方法：每一帧执行一次，除了第一帧之外
    update() { 
        // 在这里编写每一帧需要执行的逻辑，如位置更新、动画等
    }

    // on_destroy 方法：在销毁之前执行一些清理工作
    on_destroy() { 
        // 可以在这里执行一些资源清理操作，例如移除事件监听等
    }

    // destroy 方法：销毁游戏对象，移除它在 AC_GAME_OBJECTS 数组中的记录
    destroy() {
        this.on_destroy();  // 调用 on_destroy 方法执行销毁前的清理操作

        // 从 AC_GAME_OBJECTS 数组中删除当前对象
        for (let i in AC_GAME_OBJECTS) {
            const obj = AC_GAME_OBJECTS[i];
            if (obj === this) {
                AC_GAME_OBJECTS.splice(i, 1);  // 从数组中移除当前对象
                break;  // 一旦找到并删除，就退出循环
            }
        }
    }
}

// 用于记录上一帧的时间戳
let last_timestamp;

// 定义一个回调函数 step，它会通过 requestAnimationFrame 反复调用自己
const step = timestamp => {
    // 遍历所有的游戏对象
    for (let obj of AC_GAME_OBJECTS) {
        // 如果当前游戏对象的 start 方法没有被调用过
        if (!obj.has_called_start) {
            obj.has_called_start = true;  // 标记为已调用 start
            obj.start();  // 调用 start 方法，执行初始化操作
        } else {
            // 计算当前帧和上一帧之间的时间间隔，并存储在 timedelta 中
            obj.timedelta = timestamp - last_timestamp;
            obj.update();  // 调用 update 方法，执行每一帧的更新操作
        }
    }
    
    // 更新上一帧的时间戳为当前帧的时间戳
    last_timestamp = timestamp;

    // 使用 requestAnimationFrame 进行动画帧的循环调用，确保每一帧都能顺利执行
    requestAnimationFrame(step);
}

// 启动动画循环，开始执行 step 函数
requestAnimationFrame(step);
