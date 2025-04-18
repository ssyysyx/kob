package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
// 有参构造函数
@AllArgsConstructor
// 无参构造函数
@NoArgsConstructor

public class Player {
    private Integer id;
    private Integer sx; // 起点位置行数
    private Integer sy; // 起点位置列数
    // 每名玩家历史上执行过的方向的序列，可以完全确定玩家对应的蛇的路径是什么
    private List<Integer> steps; // 每一步的方向

    private boolean check_tail_increasing(int step) {
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    //辅助函数，用来判断蛇的身体有哪些
    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>(); // 存储蛇的身体每一节的 Cell 对象

        // 定义方向数组：上、右、下、左（顺时针）
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

        int x = sx, y = sy; // 当前蛇头位置初始化为起点坐标
        int step = 0;       // 当前是第几步

        res.add(new Cell(x, y)); // 初始位置加入身体列表

        // 遍历每一个方向
        for (int d : steps) {
            x += dx[d]; // 根据方向更新 x
            y += dy[d]; // 根据方向更新 y
            res.add(new Cell(x, y)); // 加入当前新的身体单元格

            // 如果当前步数不增长尾巴，就移除最早的身体单元格（即蛇尾）
            if (!check_tail_increasing(++step)) {
                res.remove(0); // 蛇尾收缩，移除头部元素
            }
        }
        return res; // 返回当前完整蛇身体的路径
    }

    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d: steps) {
            res.append(d);
        }
        return res.toString();
    }
}


