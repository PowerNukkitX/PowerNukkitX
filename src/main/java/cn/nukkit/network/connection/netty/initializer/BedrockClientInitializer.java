package cn.nukkit.network.connection.netty.initializer;

import cn.nukkit.network.connection.BedrockClientSession;
import cn.nukkit.network.connection.BedrockPeer;

public abstract class BedrockClientInitializer extends BedrockChannelInitializer<BedrockClientSession> {

    @Override
    public BedrockClientSession createSession0(BedrockPeer peer, int subClientId) {
        return new BedrockClientSession(peer, subClientId);
    }
}
