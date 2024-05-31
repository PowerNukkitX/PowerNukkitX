package cn.nukkit.network.query.enveloped;


import cn.nukkit.network.query.QueryPacket;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class DirectAddressedQueryPacket extends DefaultAddressedEnvelope<QueryPacket, InetSocketAddress> {
    /**
     * @deprecated 
     */
    
    public DirectAddressedQueryPacket(QueryPacket message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }
    /**
     * @deprecated 
     */
    

    public DirectAddressedQueryPacket(QueryPacket message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}
