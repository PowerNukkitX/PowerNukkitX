package org.powernukkitx.network;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import org.cloudburstmc.netty.channel.nethernet.NetherNetChildChannel;
import org.cloudburstmc.netty.util.nethernet.PlayerInfo;
import org.cloudburstmc.netty.util.nethernet.TransportIdentityBinding;
import org.jose4j.jwt.JwtClaims;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * NetherNet skips the Bedrock encryption handshake, which was the only thing tying a login chain to
 * the sender. These cover the check that replaces it, because getting it wrong lets anyone replay a
 * chain they captured somewhere else.
 */
class TransportIdentityBindingTest {

    private static PublicKey key() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp384r1"));
        KeyPair pair = generator.generateKeyPair();
        return pair.getPublic();
    }

    private static PlayerInfo player(PublicKey key) {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("cpk", Base64.getEncoder().encodeToString(key.getEncoded()));
        return new PlayerInfo("2535000000000000", "Tester", "1234567890",
            new InetSocketAddress("127.0.0.1", 19132), claims);
    }

    private static Channel netherNetChannel() {
        return new NetherNetChildChannel(null, null,
            new InetSocketAddress("127.0.0.1", 51234), new InetSocketAddress("127.0.0.1", 19132));
    }

    @Test
    void unboundNetherNetTransportIsRefused() throws Exception {
        // Fail closed: a channel nothing validated must not be able to carry any chain
        assertNotNull(TransportIdentityBinding.mismatch(netherNetChannel(), key()));
    }

    @Test
    void matchingKeyPasses() throws Exception {
        PublicKey identity = key();
        Channel channel = netherNetChannel();
        TransportIdentityBinding.install(channel, TransportIdentityBinding.forPlayer(player(identity)));

        assertNull(TransportIdentityBinding.mismatch(channel, identity));
    }

    @Test
    void replayedChainIsRefused() throws Exception {
        Channel channel = netherNetChannel();
        TransportIdentityBinding.install(channel, TransportIdentityBinding.forPlayer(player(key())));

        // A chain signed by somebody else, presented over this peer's own transport
        assertNotNull(TransportIdentityBinding.mismatch(channel, key()));
    }

    @Test
    void bindingIsSpentOnFirstLogin() throws Exception {
        PublicKey identity = key();
        Channel channel = netherNetChannel();
        TransportIdentityBinding.install(channel, TransportIdentityBinding.forPlayer(player(identity)));

        assertNull(TransportIdentityBinding.mismatch(channel, identity));
        assertNotNull(TransportIdentityBinding.mismatch(channel, identity));
    }

    @Test
    void otherTransportsAreLeftAlone() throws Exception {
        // RakNet bound the chain through the encryption handshake, so there is nothing to check
        assertNull(TransportIdentityBinding.mismatch(new EmbeddedChannel(), key()));
    }
}
