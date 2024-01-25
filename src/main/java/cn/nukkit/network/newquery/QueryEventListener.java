package cn.nukkit.network.newquery;

import cn.nukkit.event.server.QueryRegenerateEvent;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface QueryEventListener {
    QueryRegenerateEvent onQuery(InetSocketAddress address);
}
