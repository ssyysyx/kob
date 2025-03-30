package com.kob.backend.consumer; // 声明当前类所在的包名

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P; // 可能是误导入的，不需要，可删除
import org.springframework.stereotype.Component; // Spring注解：将该类交由Spring容器管理

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

    final private static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();
    private User user;
    private Session session = null; // 表示当前客户端的连接会话，用于收发消息

    private static UserMapper userMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
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
            matchpool.remove(this.user);
        }
    }

    private void startMatching() {
        System.out.println("startMatching");
        matchpool.add(this.user);

        while (matchpool.size() >= 2) {
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(13, 14, 20);
            game.createMap();

            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername() );
            respA.put("opponent_photo", b.getPhoto());
            respA.put("gamemap", game.getG());
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername() );
            respB.put("opponent_photo", a.getPhoto());
            respB.put("gamemap", game.getG());
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopMatching() {
        System.out.println("stopMatching");
        matchpool.remove(this.user);
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

