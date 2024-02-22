package cn.nukkit.network.connection.netty.initializer;

import cn.nukkit.network.connection.BedrockPeer;
import cn.nukkit.network.connection.BedrockSession;

public abstract class BedrockServerInitializer extends BedrockChannelInitializer<BedrockSession> {

    @Override
    public BedrockSession createSession0(BedrockPeer peer, int subClientId) {
        return new BedrockSession(peer, subClientId);
    }
}
