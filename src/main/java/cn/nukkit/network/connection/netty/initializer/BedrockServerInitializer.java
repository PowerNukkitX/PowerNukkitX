package cn.nukkit.network.connection.netty.initializer;

import cn.nukkit.network.connection.BedrockPeer;
import cn.nukkit.network.connection.BedrockServerSession;

public abstract class BedrockServerInitializer extends BedrockChannelInitializer<BedrockServerSession> {

    @Override
    public BedrockServerSession createSession0(BedrockPeer peer, int subClientId) {
        return new BedrockServerSession(peer, subClientId);
    }
}
