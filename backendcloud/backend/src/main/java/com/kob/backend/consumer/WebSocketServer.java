package com.kob.backend.consumer; // 声明当前类所在的包名

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P; // 可能是误导入的，不需要，可删除
import org.springframework.stereotype.Component; // Spring注解：将该类交由Spring容器管理
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*; // 导入WebSocket相关核心类（Session、OnOpen等）
import javax.websocket.server.PathParam; // 用于获取WebSocket路径参数
import javax.websocket.server.ServerEndpoint; // 声明该类为WebSocket服务端点

import java.io.IOException; // 用于处理IO异常（比如发送消息失败）
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component // 将当前类注册为Spring组件（这样可以与其他组件自动注入配合）
@ServerEndpoint("/websocket/{token}")  // 声明WebSocket服务端地址，带路径参数token（注意不能以'/'结尾）
public class WebSocketServer {

    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Session session = null; // 表示当前客户端的连接会话，用于收发消息

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    // 定义一个静态变量
    private static RestTemplate restTemplate;
    private Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;;
    }

    @OnOpen // 客户端连接建立时触发的方法
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接，可以做一些身份校验、记录连接等
        this.session = session; // 保存当前连接的会话对象

        System.out.println("connected!");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }

        System.out.println(users);
    }

    @OnClose // 客户端关闭连接时触发的方法
    public void onClose() {
        // 关闭链接时的处理，比如释放资源、从在线用户列表中移除该用户等
        System.out.println("disconnected!");
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    // 给指定的两名玩家 a 和 b 创建一局新的对战游戏，并分别通知双方客户端，让他们进入对战状态。
    public static void startGame(Integer aId, Integer bId) {
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);

        Game game = new Game(13, 14, 20, a.getId(), b.getId());
        game.createMap();
        // 把 game “塞” 给两名玩家的 WebSocket 会话中：
        if (users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if (users.get(b.getId()) != null)
            users.get(b.getId()).game = game;

        // 先设置完字段再启动
        game.start();

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        // 把地图信息一块传进来
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername() );
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        if (users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername() );
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if (users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    // 这个地方应该向matching server发送一个请求，表示传一个玩家过去
    private void startMatching() {
        System.out.println("startMatching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        // RestTemplate作用：发送请求
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    // 这个地方应该向matching server发送一个请求，表示取消这名玩家的匹配
    private void stopMatching() {
        System.out.println("stopMatching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {
        // 当前需要判断一下你是谁，是蛇A还是蛇B
        if (game.getPlayerA().getId().equals(user.getId())) {
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(direction);
        }
    }

    @OnMessage // 客户端发送消息时触发的方法
    public void onMessage(String message, Session session) {
        // 从Client接收消息，可以解析内容、转发给其他用户等
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError // WebSocket通信发生错误时触发的方法
    public void onError(Session session, Throwable error) {
        error.printStackTrace(); // 打印错误信息（可选地添加日志记录或清理资源）
    }

    // 发送消息给当前客户端
    public void sendMessage(String message) {
        synchronized (this.session) { // 使用同步块，保证线程安全（避免并发写入同一Session）
            try {
                this.session.getBasicRemote().sendText(message); // 向客户端发送文本消息
            } catch (IOException e) {
                e.printStackTrace(); // 如果发送失败，打印异常
            }
        }
    }
}

