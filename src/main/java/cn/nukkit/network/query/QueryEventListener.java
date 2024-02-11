package cn.nukkit.network.query;

import cn.nukkit.event.server.QueryRegenerateEvent;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface QueryEventListener {
    QueryRegenerateEvent onQuery(InetSocketAddress address);
}
