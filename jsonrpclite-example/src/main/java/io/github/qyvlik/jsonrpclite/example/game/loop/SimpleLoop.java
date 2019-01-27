package io.github.qyvlik.jsonrpclite.example.game.loop;

import com.alibaba.fastjson.JSON;
import io.github.qyvlik.jsonrpclite.core.handle.WebSocketSessionContainer;
import io.github.qyvlik.jsonrpclite.core.jsonsub.pub.ChannelMessage;
import io.github.qyvlik.jsonrpclite.core.jsonsub.sub.ChannelSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SimpleLoop {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    @Qualifier("webSocketSessionContainer")
    private WebSocketSessionContainer webSocketSessionContainer;

    @Scheduled(fixedRate = 1000L, initialDelay = 2000)
    public void loopStart() {
        try {
            timeTick();
        } catch (Exception e) {
            logger.error("timeTick error:{}", e.getMessage());
        }
    }

    private void timeTick() {
        List<ChannelSession> sessionList = webSocketSessionContainer.getSessionListFromChannel("pub.sub.tick");

        if (sessionList == null || sessionList.size() == 0) {
            return;
        }

        ChannelMessage<Long> tickMessage = new ChannelMessage<>();
        tickMessage.setChannel("pub.sub.tick");
        tickMessage.setResult(System.currentTimeMillis());
        for (ChannelSession channelSession : sessionList) {
            boolean r = webSocketSessionContainer.safeSend(
                    channelSession.getWebSocketSession(),
                    new TextMessage(JSON.toJSONString(tickMessage))
            );
            if (!r) {
                webSocketSessionContainer.onUnSub("pub.sub.tick", channelSession.getWebSocketSession());
            }
        }
    }
}
