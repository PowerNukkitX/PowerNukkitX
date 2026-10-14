package org.powernukkitx.network.nethernet;

import org.cloudburstmc.netty.channel.nethernet.signaling.PongData;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * Drives NetherNet signalling from outside the server.
 * <p>
 * With {@code signalingMode: plugin} the server binds no HTTP endpoint and a plugin feeds it offers
 * through here over whatever transport it likes. This is also the receiving half of a load balanced
 * setup: one node terminates signalling and hands offers to another through this API, and the
 * client's media connects straight to the node that answered.
 *
 * @author irrelevantdev (WaterdogPE Project)
 * @since 12/09/2026
 */
public interface SignalingService {

    /**
     * What an external endpoint should answer {@code GET /v1/join} with. The client only reads the
     * status, but a body keeps the endpoint useful to look at.
     */
    PongData advertisement();

    /**
     * Answers an SDP offer. The returned SDP already carries every ICE candidate and the operator
     * identity assertion, so it can be written to the client verbatim.
     *
     * @param networkId     the client's NetworkID, from the request path
     * @param offer         the raw SDP offer
     * @param clientAddress the client address, used for security decisions
     */
    CompletableFuture<String> acceptOffer(String networkId, String offer, InetSocketAddress clientAddress);

    /**
     * Whether NetherNet is bound and willing to take more players.
     */
    boolean acceptsConnections();
}
