package com.kob.backend.consumer.utils; // 声明当前类所在的包

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random; // 导入随机数类
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread { // 定义 Game 类，用于生成地图
    // 定义地图行数、列数、内部墙的数量，以及地图数组 g（0 表示空地，1 表示墙）
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g; // 二维数组，存放地图数据
    // 定义四个方向的数组，分别表示上、右、下、左（用于搜索时计算邻居坐标）
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB;
    private Integer nextStepA = null; // 第一名玩家的下一步操作 初始为空表示没有获取到下一步的操作
    private Integer nextStepB = null; // 第二名玩家的下一步操作 如果不是空的话，0123表示上下左右四个方向
    private ReentrantLock lock = new ReentrantLock(); // 定义锁
    private String status = "playing"; // playing -> finished
    private String loser = ""; // all: 平局 A: A输 B: B输了

    // 构造函数，传入地图的行数、列数和内部墙数量
    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB) {
        this.rows = rows; // 初始化行数
        this.cols = cols; // 初始化列数
        this.inner_walls_count = inner_walls_count; // 初始化内部墙的数量
        this.g = new int[rows][cols]; // 根据行数和列数创建二维数组，初始默认值为0
        playerA = new Player(idA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    // 返回地图数组
    public int[][] getG() {
        return g;
    }

    // 检查从起点 (sx, sy) 到目标 (tx, ty) 是否连通（使用深度优先搜索）
    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        // 如果当前坐标正好等于目标坐标，则认为连通
        if (sx == tx && sy == ty) return true;
        // 将当前点标记为已访问，防止重复访问，这里用 1 表示“访问过”或“障碍”
        g[sx][sy] = 1;

        // 遍历四个方向
        for (int i = 0; i < 4; i ++) {
            int x = sx + dx[i], y = sy + dy[i]; // 计算下一个坐标
            // 如果下一个坐标在地图范围内，且没有被访问（值为 0）
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                // 如果从下一个坐标可以连通到目标，先恢复当前点，再返回 true
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0; // 恢复现场（回溯）
                    return true;
                }
            }
        }
        // 恢复当前点状态（回溯）
        g[sx][sy] = 0;
        // 如果所有方向都没有通路，则返回 false
        return false;
    }

    // 绘制地图的方法：0表示空地，1表示墙；返回值表示从蛇的初始点到另一初始点是否连通
    private boolean draw() {
        // 初始化地图：将每个格子设置为 0（空地）
        for (int i = 0; i < this.rows; i ++) {
            for (int j = 0; j < this.cols; j ++) {
                g[i][j] = 0;
            }
        }

        // 给地图四周添加墙（边界），即将第一列和最后一列的所有行设置为 1
        for (int r = 0; r < this.rows; r ++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        // 给地图上下边界添加墙：第一行和最后一行全部设置为 1
        for (int c = 0; c < this.cols; c ++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        // 随机生成内部障碍物
        Random random = new Random(); // 创建随机数生成器
        // 循环生成 inner_walls_count / 2 组随机墙，每组会在地图上生成一对对称的墙
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            // 内层最多尝试 1000 次生成一组墙
            for (int j = 0; j < 1000; j ++) {
                int r = random.nextInt(this.rows); // 随机生成行号
                int c = random.nextInt(this.cols); // 随机生成列号

                // 如果当前位置已经是墙，或者与对称位置已经是墙，则跳过本次循环
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                // 避免覆盖两条蛇的起始位置（这里假设蛇的起始位置为 (rows-2, 1) 和 (1, cols-2)）
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;

                // 设置当前位置以及其对称位置为墙（1）
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                // 成功生成一组墙后跳出内层循环
                break;
            }
        }

        // 调用连通性检查，判断从蛇的一个起点到另一个起点是否连通
        // 假定蛇的两个起始位置分别为 (rows-2, 1) 和 (1, cols-2)
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    // 外部调用的方法，生成地图。尝试最多 1000 次，直到地图满足连通性要求为止
    public void createMap() {
        for (int i = 0; i < 1000; i ++) {
            if (draw())
                break;
        }
    }

    private boolean nextStep() { // 等待两名玩家的下一步操作
        try {
            // 防止动画没画完之前就变道了
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 50; i ++) {
            // 调用 Thread 类的静态方法 sleep，让 当前线程 休眠指定的毫秒数
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    // 判断两名玩家有没有都获得输入
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }

            } catch (InterruptedException e) {
                // throw new RuntimeException(e);
                e.printStackTrace();
            }
        }
        return false;
    }

    // 辅助函数：向前端返回信息
    private void sendMove() { // 向两个Client传递移动信息
        // 由于需要读入nextStep，这里要加锁
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            // 向前端返回完两个玩家的操作之后，就要进行下一步操作了，进行下一步操作之前需要清空
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
         Cell cell = cellsA.get(n - 1);
         if (g[cell.x][cell.y] == 1) return false;

         // 判断最后一位是不是和A有重合
         for (int i = 0; i < n - 1; i ++) {
             if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                 return false;
         }

         for (int i = 0; i < n - 1; i ++) {
             if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                 return false;
         }

         return true;
    }

    // 辅助函数
    private void judge() { //判断两名玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished";

            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    // 辅助函数：向两名玩家广播信息
    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < cols; j ++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void saveToDatabase() {
       Record record = new Record(
               null,
               playerA.getId(),
               playerA.getSx(),
               playerA.getSy(),
               playerB.getId(),
               playerB.getSx(),
               playerB.getSy(),
               playerA.getStepsString(),
               playerB.getStepsString(),
               getMapString(),
               loser,
               new Date()
       );
       WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() { // 向两个Client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i ++) {
            if (nextStep()) { // 是否获取了两条蛇的下一步操作
                // 判断两名玩家
                judge();
                if (status.equals("playing")) {
                    // 服务器需要将两名玩家的输入分别广播给两个人 C1和C2是知道自己的操作的，但是C1不知道C2的操作，C2不知道C1的操作。
                    // 这里依赖于中心服务器向两名玩家广播每条蛇的操作。
                    sendMove();
                } else {
                    sendResult();
                    break;
                }

            } else {
                status = "finished";
                lock.lock();
                try {
                    // 这里涉及到对nextStep的读，也要加锁
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                // break表示整个游戏结束了，break前要向两名玩家发送消息
                sendResult();
                break;
            }
        }
    }
}

