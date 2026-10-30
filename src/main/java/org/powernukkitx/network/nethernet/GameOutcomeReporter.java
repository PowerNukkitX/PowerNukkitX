package org.powernukkitx.network.nethernet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.cloudburstmc.netty.signaling.ProviderClient;
import org.cloudburstmc.netty.signaling.admission.AdmissionPrincipal;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntPredicate;

/**
 * Tells the NXS provider how each player it admitted got on: joined once the client spawned,
 * rejected when the server disconnects it before that. Only channels the provider admitted are
 * reported, so players from builtin signalling never reach the sink.
 *
 * @author irrelevantdev (WaterdogPE Project)
 * @since 15/10/2026
 */
public final class GameOutcomeReporter {

    public static final String HANDLER_NAME = "nethernet-game-outcome";

    private static final AttributeKey<State> STATE = AttributeKey.valueOf(GameOutcomeReporter.class, "state");

    private final Sink sink;
    private final AtomicLong dropped = new AtomicLong();

    /**
     * Where outcomes go, normally {@link ProviderClient#reportGameOutcome}.
     */
    @FunctionalInterface
    public interface Sink {
        /**
         * @return false when the outcome was dropped
         */
        boolean report(String ticketId, ProviderClient.GameOutcome outcome, String reason);
    }

    public GameOutcomeReporter(Sink sink) {
        this.sink = sink;
    }

    // Lives on the channel, so a closed connection takes its state with it
    private static final class State {
        boolean unsupportedVersion;
        boolean reported;
    }

    /**
     * @param supportedProtocol whether the server accepts a client protocol version
     */
    public ChannelDuplexHandler observer(IntPredicate supportedProtocol) {
        return new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
                if (message instanceof BedrockPacketWrapper wrapper) {
                    this.read(ctx.channel(), wrapper);
                } else if (message instanceof BedrockBatchWrapper batch) {
                    for (BedrockPacketWrapper wrapper : batch.getPackets()) {
                        this.read(ctx.channel(), wrapper);
                    }
                }
                ctx.fireChannelRead(message);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object message, ChannelPromise promise) throws Exception {
                if (message instanceof BedrockPacketWrapper wrapper) {
                    this.written(ctx.channel(), wrapper);
                } else if (message instanceof BedrockBatchWrapper batch) {
                    for (BedrockPacketWrapper wrapper : batch.getPackets()) {
                        this.written(ctx.channel(), wrapper);
                    }
                }
                ctx.write(message, promise);
            }

            private void read(Channel channel, BedrockPacketWrapper wrapper) {
                if (!primary(wrapper)) {
                    return;
                }
                if (wrapper.getPacket() instanceof RequestNetworkSettingsPacket settings) {
                    State state = state(channel);
                    if (state != null) {
                        synchronized (state) {
                            state.unsupportedVersion = !supportedProtocol.test(settings.getClientNetworkVersion());
                        }
                    }
                } else if (wrapper.getPacket() instanceof SetLocalPlayerAsInitializedPacket) {
                    // The client sends this once it has spawned in
                    report(channel, true);
                }
            }

            private void written(Channel channel, BedrockPacketWrapper wrapper) {
                if (primary(wrapper) && wrapper.getPacket() instanceof DisconnectPacket) {
                    report(channel, false);
                }
            }
        };
    }

    // Split screen players ride the same connection, only the main client counts
    private static boolean primary(BedrockPacketWrapper wrapper) {
        return wrapper.getSenderSubClientId() == 0 && wrapper.getTargetSubClientId() == 0;
    }

    private static State state(Channel channel) {
        if (channel.attr(AdmissionPrincipal.KEY).get() == null) {
            return null;
        }
        Attribute<State> attribute = channel.attr(STATE);
        State state = attribute.get();
        if (state != null) {
            return state;
        }
        State created = new State();
        State previous = attribute.setIfAbsent(created);
        return previous == null ? created : previous;
    }

    private void report(Channel channel, boolean joined) {
        State state = state(channel);
        if (state == null) {
            return;
        }
        synchronized (state) {
            if (state.reported) {
                return;
            }
            // Set even when the sink drops it, a later disconnect must not turn a join into a rejection
            state.reported = true;
            // Never hand over disconnect text, player identity or game payloads
            String reason = joined ? "play_ready" : state.unsupportedVersion ? "unsupported_version" : "server_rejected";
            if (!this.sink.report(channel.attr(AdmissionPrincipal.KEY).get().ticketId(),
                joined ? ProviderClient.GameOutcome.JOINED : ProviderClient.GameOutcome.REJECTED, reason)) {
                this.dropped.incrementAndGet();
            }
        }
    }

    public long droppedEvents() {
        return this.dropped.get();
    }
}
