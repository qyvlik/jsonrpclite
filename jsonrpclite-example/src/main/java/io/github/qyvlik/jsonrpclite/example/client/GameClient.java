package io.github.qyvlik.jsonrpclite.example.client;

import com.google.common.collect.Lists;
import io.github.qyvlik.jsonrpclite.core.client.ChannelMessageHandler;
import io.github.qyvlik.jsonrpclite.core.client.RpcClient;
import io.github.qyvlik.jsonrpclite.core.jsonrpc.entity.response.ResponseObject;
import io.github.qyvlik.jsonrpclite.core.jsonsub.pub.ChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.Future;

public class GameClient {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private RpcClient rpcClient;
    private boolean sub = false;

    public GameClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public void startupClient() {
        logger.info("startupClient start");
        if (!this.rpcClient.isOpen() && this.rpcClient != null) {
            try {
                boolean result = this.rpcClient.startup().get();
                logger.info("rpcClient startup : {}", result);
            } catch (Exception e) {
                logger.error("startupClient failure:{}", e.getMessage());
            }
        }
        logger.info("startupClient end");
    }

    private void ping() {
        if (rpcClient == null || !rpcClient.isOpen()) {
            return;
        }

        try {
            Future<ResponseObject> future = rpcClient.callRpc(
                    "pub.ping", Lists.newArrayList());

            ResponseObject responseObject = future.get();

            logger.info("ping:{}", responseObject);

            rpcClient.callRpcAsync(
                    "pub.ping", Lists.newArrayList());

        } catch (Exception e) {
            logger.error("ping error:", e);
        }
    }

    private void listen() {
        if (rpcClient == null || !rpcClient.isOpen()) {
            return;
        }

        try {
            rpcClient.listenSub("pub.sub.tick", true, Lists.newArrayList(), new ChannelMessageHandler() {
                @Override
                public void handle(ChannelMessage channelMessage) {
                    logger.info("channelMessage:{}", channelMessage);
                }
            });
            sub = true;
        } catch (Exception e) {
            logger.error("listen error:", e);
        }
    }


    @Scheduled(fixedRate = 1000L, initialDelay = 1000)
    public void tick() {
        if (!this.rpcClient.isOpen()) {
            startupClient();
        }

        if (!sub) {
            listen();
        }

        if (this.rpcClient != null && this.rpcClient.isOpen()) {
            ping();
        }
    }

}
