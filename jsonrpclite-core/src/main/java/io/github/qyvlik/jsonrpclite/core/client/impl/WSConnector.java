package io.github.qyvlik.jsonrpclite.core.client.impl;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.websocket.WebSocketContainer;
import java.util.concurrent.Future;

public class WSConnector {
    private StandardWebSocketClient webSocketClient;
    private WebSocketConnectionManager webSocketConnectionManager;
    private HandlerDecorator decorator;
    private String wsUrl;

    public WSConnector(String wsUrl, WebSocketContainer container, AbstractWebSocketHandler handler) {
        this.wsUrl = wsUrl;
        this.webSocketClient = new StandardWebSocketClient(container);
        this.decorator = new HandlerDecorator(handler);
        this.webSocketConnectionManager = new WebSocketConnectionManager(
                this.webSocketClient, this.decorator, this.wsUrl);
    }

    public Future<Boolean> startupAsync() {
        webSocketConnectionManager.start();
        return this.decorator.getStartup();
    }

    public StandardWebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(StandardWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public WebSocketConnectionManager getWebSocketConnectionManager() {
        return webSocketConnectionManager;
    }

    public void setWebSocketConnectionManager(WebSocketConnectionManager webSocketConnectionManager) {
        this.webSocketConnectionManager = webSocketConnectionManager;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    private static class HandlerDecorator implements WebSocketHandler {
        private WebSocketHandler delegate;
        private ResultFuture<Boolean> startup;

        public HandlerDecorator(WebSocketHandler delegate) {
            this.delegate = delegate;
            this.startup = new ResultFuture<Boolean>();
        }

        public Future<Boolean> getStartup() {
            return startup;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
            if (!this.startup.isDone()) {
                this.startup.setResult(true);
            }
            this.delegate.afterConnectionEstablished(webSocketSession);
        }

        @Override
        public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
            this.delegate.handleMessage(webSocketSession, webSocketMessage);
        }

        @Override
        public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
            if (!this.startup.isDone()) {
                this.startup.setResult(false);
            }
            this.delegate.handleTransportError(webSocketSession, throwable);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
            if (!this.startup.isDone()) {
                this.startup.setResult(false);
            }
            this.delegate.afterConnectionClosed(webSocketSession, closeStatus);
        }

        @Override
        public boolean supportsPartialMessages() {
            return this.delegate.supportsPartialMessages();
        }
    }
}
