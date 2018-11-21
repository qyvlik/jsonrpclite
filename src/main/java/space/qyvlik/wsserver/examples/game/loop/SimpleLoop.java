package space.qyvlik.wsserver.examples.game.loop;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import space.qyvlik.wsserver.handle.WebSocketSessionContainer;
import space.qyvlik.wsserver.jsonsub.pub.ChannelMessage;
import space.qyvlik.wsserver.jsonsub.sub.ChannelSession;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SimpleLoop {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("scheduledExecutorService")
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    @Qualifier("webSocketSessionContainer")
    private WebSocketSessionContainer webSocketSessionContainer;

    @PostConstruct
    public void loopStart() {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    timeTick();
                } catch (Exception e) {
                    logger.error("timeTick error:{}", e.getMessage());
                }
            }
        }, 10000, 1000, TimeUnit.MILLISECONDS);
    }

    private void timeTick() {
        List<ChannelSession> sessionList = webSocketSessionContainer.getSessionListFromChannel("pub.tick");

        if (sessionList == null || sessionList.size() == 0) {
            return;
        }

        ChannelMessage<Long> tickMessage = new ChannelMessage<>();
        tickMessage.setChannel("pub.tick");
        tickMessage.setResult(System.currentTimeMillis());
        for (ChannelSession channelSession : sessionList) {
            boolean r = safeSend(channelSession.getWebSocketSession(), tickMessage);
            if (!r) {
                webSocketSessionContainer.onUnSub("pub.tick", channelSession.getWebSocketSession());
            }
        }
    }

    private boolean safeSend(WebSocketSession session, Object obj) {
        try {
            session.sendMessage(new TextMessage(JSON.toJSONString(obj)));
            return true;
        } catch (Exception e) {
            logger.error("safeSend:{}", e.getMessage());
            return false;
        }
    }
}
