package top.flya.system.handel;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.flya.common.helper.LoginHelper;
import top.flya.system.config.ClientCache;
import top.flya.system.config.Event;
import top.flya.system.domain.PzcUser;
import top.flya.system.domain.bo.PzcUserTalkBo;
import top.flya.system.mapper.PzcUserMapper;

import top.flya.system.service.IPzcUserTalkService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.UUID;

/**
 * <p>
 * 消息事件处理
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 18:57
 */
@Component
@Slf4j
public class MessageEventHandler {

    @Autowired
    private ClientCache cache;
    @Resource
    private PzcUserMapper userMapper;

    @Resource
    private IPzcUserTalkService userTalkService;

    /**
     * 添加connect事件，当客户端发起连接时调用
     *
     * @param client 客户端对象
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        if (client != null) {
            log.info("client.getHandshakeData() is {}", JSONUtil.toJsonPrettyStr(client.getHandshakeData()));
            String userId = client.getHandshakeData().getSingleUrlParam("userId"); //我的userId
            UUID sessionId = client.getSessionId();
            if(userId!=null)
            {
                cache.saveClient(userId,sessionId,client);
                //查询当前用户是否存在 或者被封
                PzcUser pzcUser = userMapper.selectById(userId);
                if(pzcUser==null||pzcUser.getState()==0)
                {
                    log.error("无效连接 该用户不存在 或者被封禁");
                    client.disconnect();
                }
                log.info("与对方建立连接成功,【userId】= {},【sessionId】= {}", userId, sessionId);
            }else {
                log.error("无效连接");
                client.disconnect();
            }
        } else {
            log.error("客户端为空");
        }
    }

    /**
     * 添加disconnect事件，客户端断开连接时调用，刷新客户端信息
     *
     * @param client 客户端对象
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        if (client != null) {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            UUID sessionId = client.getSessionId();
            if(userId!=null)
            {
                log.info("客户端断开连接,【userId】= {},【sessionId】= {}", userId, sessionId);
                cache.deleteSessionClient(userId, client.getSessionId());
            }
            client.disconnect();
        } else {
            log.error("客户端为空");
        }
    }


    /**
     * 单聊
     */
    public boolean sendToSingle(String userId, PzcUserTalkBo message) {
        HashMap<UUID, SocketIOClient> userClient = cache.getUserClient(userId);
        if(userClient!=null)
        {
            userClient.forEach(((uuid, socketIOClient) ->
            {
                socketIOClient.sendEvent(Event.CHAT, message);
            }));
            return true;
        }
        return false;
    }



    @OnEvent(value = Event.CHAT)
    public void onChatEvent(SocketIOClient client, AckRequest request, PzcUserTalkBo data) {
        log.info("用户 {} 刚刚私信了用户 {}：{}", data.getFromUserId(), data.getToUserId(), data.getMessage());
        userTalkService.insertByBo(data);
        if(sendToSingle(String.valueOf(data.getToUserId()), data))
        {
            request.sendAckData(Dict.create().set("flag", true).set("message", "发送成功"));
        }else {
            request.sendAckData(Dict.create().set("flag", false).set("message", "用户不在线~ 对方上线后可见 "));
        }

    }





//    /**
//     * 加入群聊
//     *
//     * @param client  客户端
//     * @param request 请求
//     * @param data    群聊
//     */
//    @OnEvent(value = Event.JOIN)
//    public void onJoinEvent(SocketIOClient client, AckRequest request, JoinRequest data) {
//        log.info("用户：{} 已加入群聊：{}", data.getUserId(), data.getGroupId());
//        client.joinRoom(data.getGroupId());
//
//        server.getRoomOperations(data.getGroupId()).sendEvent(Event.JOIN, data);
//    }



//    @OnEvent(value = Event.GROUP)
//    public void onGroupEvent(SocketIOClient client, AckRequest request, GroupMessageRequest data) {
//        Collection<SocketIOClient> clients = server.getRoomOperations(data.getGroupId()).getClients();
//
//        boolean inGroup = false;
//        for (SocketIOClient socketIOClient : clients) {
//            if (ObjectUtil.equal(socketIOClient.getSessionId(), client.getSessionId())) {
//                inGroup = true;
//                break;
//            }
//        }
//        if (inGroup) {
//            log.info("群号 {} 收到来自 {} 的群聊消息：{}", data.getGroupId(), data.getFromUid(), data.getMessage());
//            sendToGroup(data);
//        } else {
//            request.sendAckData("请先加群！");
//        }
//    }



//    /**
//     * 广播
//     */
//    public void sendToBroadcast(BroadcastMessageRequest message) {
//        log.info("系统紧急广播一条通知：{}", message.getMessage());
//        for (UUID clientId : dbTemplate.findAll()) {
//            if (server.getClient(clientId) == null) {
//                continue;
//            }
//            server.getClient(clientId).sendEvent(Event.BROADCAST, message);
//        }
//    }
//
//    /**
//     * 群聊
//     */
//    public void sendToGroup(GroupMessageRequest message) {
//        server.getRoomOperations(message.getGroupId()).sendEvent(Event.GROUP, message);
//    }
}