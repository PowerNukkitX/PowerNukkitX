package org.powernukkitx.network.process.auth;

public interface ProxyAuthProvider {

    /**
     * Whether an unsigned login chain should be accepted even when xbox-auth is required.
     * Proxies strip the Mojang signature when forwarding, so proxy integrations return true here.
     */
    default boolean isUnsignedLoginAllowed() {
        return false;
    }

    /**
     * Resolve the XUID for a connecting client.
     *
     * @param chainData the parsed client chain data, raw claims available via {@link ClientChainData#getRawClaims()}
     * @param fallback  the XUID from the identity chain
     * @return the XUID to use for this client
     */
    default String getXuid(ClientChainData chainData, String fallback) {
        return fallback;
    }
}
