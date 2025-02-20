export class Cell {
    //传行数和列数
    constructor(r, c) {
        this.r = r;
        this.c = c;
        // 变换成canvas里面的坐标
        this.x = c + 0.5;
        this.y = r + 0.5;
    }
}