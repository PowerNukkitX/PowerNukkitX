package cn.nukkit.network.connection;

@FunctionalInterface
public interface BedrockSessionFactory {

    cn.nukkit.network.connection.BedrockSession createSession(cn.nukkit.network.connection.BedrockPeer peer, int subClientId);
}
