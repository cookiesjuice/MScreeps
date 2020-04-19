package com.cookiesjuice.mscreeps;

import org.apache.catalina.SessionIdGenerator;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.adapter.standard.WebSocketToStandardExtensionAdapter;
import org.springframework.web.socket.client.AbstractWebSocketClient;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.AbstractXhrTransport;
import org.springframework.web.socket.sockjs.client.InfoReceiver;
import org.springframework.web.socket.sockjs.client.JettyXhrTransport;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.client.XhrClientSockJsSession;
import org.springframework.web.socket.sockjs.client.XhrTransport;
import org.springframework.web.socket.sockjs.transport.TransportType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.WebSocketContainer;

public class SocketClient {
    private MainActivity activity;
    private final String endpoint =
            "wss://screeps.com/socket/";
    private String sessionId;

    public SocketClient(MainActivity activity) throws IOException {
        List<Transport> transports = new ArrayList<>(2);

        transports.add(new WebSocketTransport(new AbstractWebSocketClient(){
            private final WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
            private AsyncListenableTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

            @Override
            protected ListenableFuture<WebSocketSession>
                doHandshakeInternal(WebSocketHandler webSocketHandler, HttpHeaders headers,
                                    URI uri, List<String> subProtocols,
                                    List<WebSocketExtension> extensions, Map<String, Object> attributes) {
                int port = 80;
                InetSocketAddress localAddress = new InetSocketAddress("localhost", port);
                InetSocketAddress remoteAddress = new InetSocketAddress(uri.getHost(), port);

                final StandardWebSocketSession session = new StandardWebSocketSession(headers,
                        attributes, localAddress, remoteAddress);

                final ClientEndpointConfig endpointConfig = ClientEndpointConfig.Builder.create()
                        .configurator(new StandardWebSocketClientConfigurator(headers))
                        .preferredSubprotocols(subProtocols)
                        .extensions(adaptExtensions(extensions)).build();

                endpointConfig.getUserProperties().putAll(new HashMap<>());

                final Endpoint endpoint = new StandardWebSocketHandlerAdapter(webSocketHandler, session);

                Callable<WebSocketSession> connectTask = () -> {
                    this.webSocketContainer.connectToServer(endpoint, endpointConfig, uri);
                    return session;
                };

                if (this.taskExecutor != null) {
                    return this.taskExecutor.submitListenable(connectTask);
                }
                else {
                    ListenableFutureTask<WebSocketSession> task = new ListenableFutureTask<>(connectTask);
                    task.run();
                    return task;
                }
            }

            private List<Extension> adaptExtensions(List<WebSocketExtension> extensions) {
                List<Extension> result = new ArrayList<>();
                for (WebSocketExtension extension : extensions) {
                    result.add(new WebSocketToStandardExtensionAdapter(extension));
                }
                return result;
            }

            class StandardWebSocketClientConfigurator extends ClientEndpointConfig.Configurator {

                private final HttpHeaders headers;

                public StandardWebSocketClientConfigurator(HttpHeaders headers) {
                    this.headers = headers;
                }

                @Override
                public void beforeRequest(Map<String, List<String>> requestHeaders) {
                    requestHeaders.putAll(this.headers);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Handshake request headers: " + requestHeaders);
                    }
                }
                @Override
                public void afterResponse(HandshakeResponse response) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Handshake response headers: " + response.getHeaders());
                    }
                }
            }
        }));

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        XhrTransport xhrTransport = new RestTemplateXhrTransport(new RestTemplate(converters));
        transports.add(xhrTransport);


        WebSocketClient client = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new SimpleMessageConverter());
        WebSocketHandler handler = new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {

            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                System.out.println(message.getPayload());
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                exception.printStackTrace();
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };

        client.doHandshake(handler, endpoint);

    }



}
