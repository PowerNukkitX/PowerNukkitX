package org.powernukkitx.network;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.packet.SetTimePacket;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HoldableServerSessionTest {

    private BedrockPeer peer;
    private List<Runnable> eventLoopTasks;
    private HoldableServerSession session;

    @BeforeEach
    void setUp() {
        this.peer = mock(BedrockPeer.class);
        Channel channel = mock(Channel.class);
        EventLoop eventLoop = mock(EventLoop.class);
        this.eventLoopTasks = new ArrayList<>();
        when(this.peer.getChannel()).thenReturn(channel);
        when(channel.eventLoop()).thenReturn(eventLoop);
        doAnswer(invocation -> {
            this.eventLoopTasks.add(invocation.getArgument(0));
            return null;
        }).when(eventLoop).execute(any(Runnable.class));
        this.session = new HoldableServerSession(this.peer, 0);
    }

    @Test
    void sendsDirectlyWhenNotHeld() {
        StartGamePacket startGame = new StartGamePacket();
        this.session.sendPacketImmediately(startGame);
        verify(this.peer).sendPacketImmediately(anyInt(), anyInt(), same(startGame));
    }

    @Test
    void heldPacketsAreReplayedInOrderOnTheEventLoop() {
        SetTimePacket queuedFirst = new SetTimePacket();
        StartGamePacket startGame = new StartGamePacket();
        List<String> order = new ArrayList<>();

        this.session.holdOutbound();
        this.session.sendPacket(queuedFirst);
        this.session.runInOrder(() -> order.add("action"));
        this.session.sendPacketImmediately(startGame);

        verify(this.peer, never()).sendPacket(anyInt(), anyInt(), any());
        verify(this.peer, never()).sendPacketImmediately(anyInt(), anyInt(), any());
        Assertions.assertTrue(order.isEmpty());

        this.session.releaseOutbound();
        // Nothing is written off the event loop
        verify(this.peer, never()).sendPacketImmediately(anyInt(), anyInt(), any());
        Assertions.assertEquals(1, this.eventLoopTasks.size());

        this.eventLoopTasks.getFirst().run();
        InOrder inOrder = inOrder(this.peer);
        inOrder.verify(this.peer).sendPacket(anyInt(), anyInt(), same(queuedFirst));
        inOrder.verify(this.peer).sendPacketImmediately(anyInt(), anyInt(), same(startGame));
        Assertions.assertEquals(List.of("action"), order);

        // Held no longer: sent straight through
        SetTimePacket after = new SetTimePacket();
        this.session.sendPacket(after);
        verify(this.peer).sendPacket(anyInt(), anyInt(), same(after));
    }

    @Test
    void releaseWithoutHoldDoesNothing() {
        this.session.releaseOutbound();
        Assertions.assertTrue(this.eventLoopTasks.isEmpty());
    }
}
