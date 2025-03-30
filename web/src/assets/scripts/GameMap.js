import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
import { Snake } from "./Snake";

// 定义地图类 GameMap，继承自 AcGameObject
export class GameMap extends AcGameObject {
    // 构造函数，ctx 是画布的 2D 上下文，parent 是画布的父元素，用来动态修改画布的长宽
    constructor(ctx, parent, store) {
        super();  // 调用父类的构造函数

        this.ctx = ctx;  // 保存画布的 2D 上下文
        this.parent = parent;  // 保存画布的父元素
        this.store = store;

        // 由于浏览器有可能放大或缩小，所以需要使用相对尺寸而非绝对尺寸
        // L 代表每个单位格子的边长
        this.L = 0;

        this.rows = 13;  // 地图的行数
        this.cols = 14;  // 地图的列数

        this.inner_walls_count = 20;
        this.walls = [];
        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this),
        ]
    }

    create_walls() {
        const g = this.store.state.pk.gamemap;

        for (let r = 0; r < this.rows; r ++) {
            for (let c = 0; c < this.cols; c ++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }

    add_listening_events() {
        this.ctx.canvas.focus();
        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if (e.key === 'w') snake0.set_direction(0); //上的编号是0
            else if (e.key === 'd') snake0.set_direction(1);
            else if (e.key === 's') snake0.set_direction(2);
            else if (e.key === 'a') snake0.set_direction(3);
            else if (e.key === 'ArrowUp') {snake1.set_direction(0)}
            else if (e.key === 'ArrowRight') snake1.set_direction(1);
            else if (e.key === 'ArrowDown') snake1.set_direction(2);
            else if (e.key === 'ArrowLeft') snake1.set_direction(3);
        });
    }

    // start 方法：初始化操作，只会执行一次
    start() {
        this.create_walls();

        this.add_listening_events();
    }

    // 辅助函数：每一帧更新格子边长，保持适应父容器大小
    update_size() {
        // 计算每个格子的边长，保证地图大小适应父元素的宽高
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        
        // 设置画布的宽度和高度，保证画布的大小和地图一致
        this.ctx.canvas.width = this.L * this.cols;  // 画布宽度 = 每格边长 * 列数
        this.ctx.canvas.height = this.L * this.rows;  // 画布高度 = 每格边长 * 行数
    }

    check_ready() { //判断两条蛇是否都准备好下一回合了
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }
        return true;
    }

    next_step () { //让两条蛇进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    check_valid(cell) { //检测目标位置是否合法：没有撞到两条蛇的身体和障碍物
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) { // 当蛇尾会前进的时候，蛇尾不要判断
                k --;
            }
            for (let i = 0; i < k; i ++) {
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }

    // update 方法：每一帧执行，更新格子大小并渲染地图
    update() {
        this.update_size();  // 更新地图的格子大小
        if (this.check_ready()) {
            this.next_step();
        }
        this.render();  // 渲染地图
    }

    // 渲染方法：绘制地图
    render() {
        // 使用 ctx 来绘制地图背景
        // this.ctx.fillStyle = "green";  // 设置填充颜色为绿色
        // this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);  // 绘制一个填满整个画布的绿色矩形

        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r ++) {
            for (let c = 0; c < this.cols; c ++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}
