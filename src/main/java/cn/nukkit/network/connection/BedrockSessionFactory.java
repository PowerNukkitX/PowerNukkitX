package cn.nukkit.network.connection;

@FunctionalInterface
public interface BedrockSessionFactory {

    BedrockSession createSession(BedrockPeer peer, int subClientId);
}
