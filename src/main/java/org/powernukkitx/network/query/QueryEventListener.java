package org.powernukkitx.network.query;

import org.powernukkitx.event.server.QueryRegenerateEvent;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface QueryEventListener {
    QueryRegenerateEvent onQuery(InetSocketAddress address);
}
