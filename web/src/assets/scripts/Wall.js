// Wall障碍物也是一个游戏对象
import { AcGameObject } from "./AcGameObject";

export class Wall extends AcGameObject {
    // 传一下横纵坐标r, c
    constructor (r, c, gamemap) {
        super();

        this.r = r;
        this.c = c;
        this.gamemap = gamemap;
        this.color = "#B37226";
    }

    update() {
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        ctx.fillRect(this.c * L, this.r * L, L, L);
    }
}