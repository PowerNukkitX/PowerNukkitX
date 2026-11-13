package org.powernukkitx.network.process;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.powernukkitx.config.category.network.RateLimitSettings;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * What a session holds on behalf of a client that has not earned it yet: the session itself before
 * the login arrives, and the packets kept back while its player is created.
 */
class PlayerSessionHolderFloodTest {

    private BedrockServerSession session;
    private EventLoop eventLoop;
    private PlayerSessionHolder holder;

    @BeforeEach
    void setUp() {
        this.session = mock(BedrockServerSession.class);
        final BedrockPeer peer = mock(BedrockPeer.class);
        final Channel channel = mock(Channel.class);
        this.eventLoop = mock(EventLoop.class);
        when(this.session.getPeer()).thenReturn(peer);
        when(this.session.isConnected()).thenReturn(true);
        when(peer.getChannel()).thenReturn(channel);
        when(channel.eventLoop()).thenReturn(this.eventLoop);
        when(channel.closeFuture()).thenReturn(mock(ChannelFuture.class));
        this.holder = new PlayerSessionHolder(this.session, new RateLimitSettings());
    }

    private Runnable startDeadline() {
        this.holder.startLoginDeadline(30);
        final ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
        verify(this.eventLoop).schedule(task.capture(), eq(30L), eq(TimeUnit.SECONDS));
        return task.getValue();
    }

    @Test
    void closesASessionThatNeverSentItsLogin() {
        for (SessionState state : new SessionState[]{
            SessionState.INITIAL, SessionState.REQUESTED_NETWORK_SETTINGS, SessionState.LOGIN}) {
            this.setUp();
            final Runnable deadline = this.startDeadline();
            this.holder.setState(state);

            deadline.run();

            verify(this.session).close("Login timed out");
        }
    }

    @Test
    void leavesASessionWhoseLoginHasArrived() {
        for (SessionState state : new SessionState[]{
            SessionState.AUTHENTICATING, SessionState.RESOURCE_PACK, SessionState.SPAWNING, SessionState.CHUNKS}) {
            this.setUp();
            final Runnable deadline = this.startDeadline();
            this.holder.setState(state);

            deadline.run();

            verify(this.session, never()).close(anyString());
        }
    }

    @Test
    void leavesASessionThatIsAlreadyGone() {
        final Runnable deadline = this.startDeadline();
        when(this.session.isConnected()).thenReturn(false);

        deadline.run();

        verify(this.session, never()).close(anyString());
    }

    @Test
    void setsNoDeadlineWhenTheTimeoutIsOff() {
        this.holder.startLoginDeadline(0);
        this.holder.startLoginDeadline(-1);

        verify(this.eventLoop, never()).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
    }

    @Test
    void holdsBackPacketsUpToTheBacklogLimit() {
        this.holder.setState(SessionState.SPAWNING);

        for (int i = 0; i < PlayerSessionHolder.MAX_SPAWN_BACKLOG; i++) {
            this.holder.deferUntilSpawned(new TextPacket());
        }

        assertFalse(this.holder.isDisconnected());
        assertTrue(this.holder.shouldDeferUntilSpawned());
    }

    @Test
    void disconnectsAClientThatOverflowsTheBacklog() {
        this.holder.setState(SessionState.SPAWNING);

        for (int i = 0; i <= PlayerSessionHolder.MAX_SPAWN_BACKLOG; i++) {
            this.holder.deferUntilSpawned(new TextPacket());
        }

        assertTrue(this.holder.isDisconnected());
    }
}
