package org.powernukkitx.config.category.network;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class NetherNetSettings extends OkaeriConfig {
    /**
     * How the server obtains SDP offers from clients.
     */
    public enum SignalingMode {
        /**
         * The server runs the HTTP signalling endpoint itself.
         */
        BUILTIN,
        /**
         * The server registers with an NXS provider, which hands it admitted connections.
         */
        NXS,
        /**
         * Both of the above at once, so players can arrive either way.
         */
        HYBRID,
        /**
         * No endpoint is served. Offers arrive through the signalling API, driven by a plugin.
         */
        PLUGIN;

        public boolean builtin() {
            return this == BUILTIN || this == HYBRID;
        }

        public boolean nxs() {
            return this == NXS || this == HYBRID;
        }

        public boolean binds() {
            return this != NXS;
        }
    }

    @Comment("pnx.settings.network.nethernet.signalingmode")
    String signalingMode = "builtin";
    @Comment("pnx.settings.network.nethernet.nxs")
    @CustomKey("nxs")
    NxsSettings nxsSettings = new NxsSettings();
    @Comment("pnx.settings.network.nethernet.signalingport")
    int signalingPort = 0;
    @Comment("pnx.settings.network.nethernet.udpport")
    int udpPort = 0;
    @Comment("pnx.settings.network.nethernet.handshaketimeout")
    int handshakeTimeout = 30;
    @Comment("pnx.settings.network.nethernet.iceservers")
    @CustomKey("ice-servers")
    List<String> iceServers = new ArrayList<>();
    @Comment("pnx.settings.network.nethernet.advertiseaddresses")
    @CustomKey("advertise-addresses")
    List<String> advertiseAddresses = new ArrayList<>();
    @Comment("pnx.settings.network.nethernet.trustedproxies")
    @CustomKey("trusted-proxies")
    List<String> trustedProxies = new ArrayList<>();
    @Comment("pnx.settings.network.nethernet.proxyprotocol")
    boolean proxyProtocol = false;
    @Comment("pnx.settings.network.nethernet.identityfile")
    String identityFile = "keys/identity.pem";
    @Comment("pnx.settings.network.nethernet.identitydomain")
    String identityDomain = "";
    @Comment("pnx.settings.network.nethernet.nativeloglevel")
    String nativeLogLevel = "WARN";

    public SignalingMode resolvedSignalingMode() {
        try {
            return SignalingMode.valueOf(this.signalingMode.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (RuntimeException invalid) {
            return SignalingMode.BUILTIN;
        }
    }
}
