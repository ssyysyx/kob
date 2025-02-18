import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";

// 定义地图类 GameMap，继承自 AcGameObject
export class GameMap extends AcGameObject {
    // 构造函数，ctx 是画布的 2D 上下文，parent 是画布的父元素，用来动态修改画布的长宽
    constructor(ctx, parent) {
        super();  // 调用父类的构造函数

        this.ctx = ctx;  // 保存画布的 2D 上下文
        this.parent = parent;  // 保存画布的父元素
        // 由于浏览器有可能放大或缩小，所以需要使用相对尺寸而非绝对尺寸
        // L 代表每个单位格子的边长
        this.L = 0;

        this.rows = 13;  // 地图的行数
        this.cols = 13;  // 地图的列数

        this.inner_walls_count = 20;
        this.walls = [];
    }

    check_connectivity(g, sx, sy, tx, ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = true;

        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        for (let i = 0; i < 4; i ++) {
            let x = sx + dx[i], y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }
        return false;
    }

    create_walls() {
        // new Wall(0, 0, this);
        const g = [];//开一个布尔数组
        for (let r = 0; r < this.rows; r ++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c ++) {
                g[r][c] = false;
            }
        }

        // 给四周加上障碍物
        for (let r = 0; r < this.rows; r ++) {
            g[r][0] = g[r][this.cols - 1] = true;
        }

        for (let c = 0; c < this.cols; c ++) {
            g[0][c] = g[this.rows - 1][c] = true;
        }

        //创建随机障碍物
        for (let i = 0; i < this.inner_walls_count / 2; i ++) {
            for (let j = 0; j < 1000; j ++) {
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c] || g[c][r]) continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;
                g[r][c] = g[c][r] = true;
                break;
            }
        }
    
        //先把当前状态复制一遍。怎么深度复制一个对象？在js里面，把它先转化成json，再把json解析出来。
        const copy_g = JSON.parse(JSON.stringify(g));
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) return false;

        for (let r = 0; r < this.rows; r ++) {
            for (let c = 0; c < this.cols; c ++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
        return true;
    }

    // start 方法：初始化操作，只会执行一次
    start() {
        for (let i = 0; i < 1000; i ++) {
            if (this.create_walls())
                break;
        }
    }

    // 辅助函数：每一帧更新格子边长，保持适应父容器大小
    update_size() {
        // 计算每个格子的边长，保证地图大小适应父元素的宽高
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        
        // 设置画布的宽度和高度，保证画布的大小和地图一致
        this.ctx.canvas.width = this.L * this.cols;  // 画布宽度 = 每格边长 * 列数
        this.ctx.canvas.height = this.L * this.rows;  // 画布高度 = 每格边长 * 行数
    }

    // update 方法：每一帧执行，更新格子大小并渲染地图
    update() {
        this.update_size();  // 更新地图的格子大小
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
