package cn.nukkit.plugin.js.feature.ws;

import cn.nukkit.plugin.js.JSConcurrentManager;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class WsClientBuilder implements ProxyObject {
    protected final Context sourceContext;

    protected WebSocket.Builder webSocketBuilder = HttpClient.newHttpClient().newWebSocketBuilder();
    protected URI uri;

    protected Value onOpenHandler;
    protected Value onErrorHandler;
    protected Value onBinaryHandler;
    protected Value onCloseHandler;
    protected Value onPingHandler;
    protected Value onPongHandler;
    protected Value onTextHandler;

    public static List<String> memberKeys = List.of("reset", "setURI", "onOpen", "onError", "onBinary", "onClose", "onPing", "onPong", "onText", "addHeader", "setTimeout", "buildAsync");

    public WsClientBuilder(Context sourceContext) {
        this.sourceContext = sourceContext;
    }

    @Override
    public Object getMember(String key) {
        return switch (key) {
            case "reset" -> (ProxyExecutable) arguments -> {
                uri = null;
                onOpenHandler = null;
                onErrorHandler = null;
                onBinaryHandler = null;
                onCloseHandler = null;
                onPingHandler = null;
                onPongHandler = null;
                onTextHandler = null;
                webSocketBuilder = HttpClient.newHttpClient().newWebSocketBuilder();
                return this;
            };
            case "setURI" -> (ProxyExecutable) arguments -> {
                uri = URI.create(arguments[0].asString());
                return this;
            };
            case "onOpen" -> (ProxyExecutable) arguments -> {
                onOpenHandler = arguments[0];
                return this;
            };
            case "onError" -> (ProxyExecutable) arguments -> {
                onErrorHandler = arguments[0];
                return this;
            };
            case "onBinary" -> (ProxyExecutable) arguments -> {
                onBinaryHandler = arguments[0];
                return this;
            };
            case "onClose" -> (ProxyExecutable) arguments -> {
                onCloseHandler = arguments[0];
                return this;
            };
            case "onPing" -> (ProxyExecutable) arguments -> {
                onPingHandler = arguments[0];
                return this;
            };
            case "onPong" -> (ProxyExecutable) arguments -> {
                onPongHandler = arguments[0];
                return this;
            };
            case "onText" -> (ProxyExecutable) arguments -> {
                onTextHandler = arguments[0];
                return this;
            };
            case "addHeader" -> (ProxyExecutable) arguments -> {
                webSocketBuilder.header(arguments[0].asString(), arguments[1].asString());
                return this;
            };
            case "setTimeout" -> (ProxyExecutable) arguments -> {
                webSocketBuilder.connectTimeout(Duration.ofMillis(arguments[0].asLong()));
                return this;
            };
            case "buildAsync" -> (ProxyExecutable) arguments -> JSConcurrentManager.wrapPromise(sourceContext, webSocketBuilder.buildAsync(uri,
                    new WebSocket.Listener() {
                        @Override
                        public void onOpen(WebSocket webSocket) {
                            if (onOpenHandler.canExecute()) {
                                var res = onOpenHandler.execute(webSocket);
                                if (res.isBoolean() && res.asBoolean()) {
                                    webSocket.request(1);
                                }
                            }
                        }

                        @Override
                        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                            if (onTextHandler.canExecute()) {
                                synchronized (sourceContext) {
                                    var res = onTextHandler.execute(webSocket, data, last);
                                    if (res.isBoolean() && res.asBoolean()) {
                                        webSocket.request(1);
                                    }
                                }
                            }
                            return null;
                        }

                        @Override
                        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                            if (onBinaryHandler.canExecute()) {
                                synchronized (sourceContext) {
                                    var res = onBinaryHandler.execute(webSocket, Value.asValue(data), last);
                                    if (res.isBoolean() && res.asBoolean()) {
                                        webSocket.request(1);
                                    }
                                }
                            }
                            return null;
                        }

                        @Override
                        public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
                            if (onPingHandler.canExecute()) {
                                synchronized (sourceContext) {
                                    var res = onPingHandler.execute(webSocket, Value.asValue(message));
                                    if (res.isBoolean() && res.asBoolean()) {
                                        webSocket.request(1);
                                    }
                                }
                            }
                            return null;
                        }

                        @Override
                        public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
                            if (onPongHandler.canExecute()) {
                                synchronized (sourceContext) {
                                    var res = onPongHandler.execute(webSocket, Value.asValue(message));
                                    if (res.isBoolean() && res.asBoolean()) {
                                        webSocket.request(1);
                                    }
                                }
                            }
                            return null;
                        }

                        @Override
                        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                            if (onCloseHandler.canExecute()) {
                                synchronized (sourceContext) {
                                    onCloseHandler.execute(webSocket, statusCode, reason);
                                }
                            }
                            return null;
                        }

                        @Override
                        public void onError(WebSocket webSocket, Throwable error) {
                            if (onErrorHandler.canExecute()) {
                                synchronized (sourceContext) {
                                    onErrorHandler.execute(webSocket, error);
                                }
                            }
                        }
                    }));
            default -> null;
        };
    }

    @Override
    public Object getMemberKeys() {
        //noinspection unchecked ,rawtypes
        return ProxyArray.fromList((List) memberKeys);
    }

    @Override
    public boolean hasMember(String key) {
        return memberKeys.contains(key);
    }

    @Override
    public void putMember(String key, Value value) {
        throw new UnsupportedOperationException();
    }

}
