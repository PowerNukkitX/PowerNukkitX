package cn.nukkit.network;

import cn.nukkit.GameMockExtension;
import cn.nukkit.network.connection.netty.initializer.BedrockChannelInitializer;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestNetworkSettingsPacket;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.ByteBufVarInt;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakChildChannel;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.channel.raknet.RakConstants;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.netty.channel.raknet.packet.RakMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@ExtendWith(GameMockExtension.class)
public class RakNetInterfaceTest {
    private static final byte[] ADVERTISEMENT = new StringJoiner(";", "", ";")
            .add("MCPE")
            .add("RakNet unit test")
            .add(Integer.toString(542))
            .add("1.19.0")
            .add(Integer.toString(0))
            .add(Integer.toString(4))
            .add(Long.toUnsignedString(ThreadLocalRandom.current().nextLong()))
            .add("C")
            .add("Survival")
            .add("1")
            .add("19132")
            .add("19132")
            .toString().getBytes(StandardCharsets.UTF_8);

    private static Bootstrap clientBootstrap(int mtu) {
        return new Bootstrap()
                .channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
                .group(new NioEventLoopGroup())
                .option(RakChannelOption.RAK_PROTOCOL_VERSION, 11)
                .option(RakChannelOption.RAK_MTU, mtu)
                .option(RakChannelOption.RAK_ORDERING_CHANNELS, 1);
    }

    private static ServerBootstrap serverBootstrap() {
        return new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(NioDatagramChannel.class))
                .group(new NioEventLoopGroup())
                .option(RakChannelOption.RAK_SUPPORTED_PROTOCOLS, new int[]{11})
                .option(RakChannelOption.RAK_MAX_CONNECTIONS, 1)
                .childOption(RakChannelOption.RAK_ORDERING_CHANNELS, 1)
                .option(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong())
                .option(RakChannelOption.RAK_ADVERTISEMENT, Unpooled.wrappedBuffer(ADVERTISEMENT));
    }

    @Test()
    @Timeout(20)
    void test(GameMockExtension gameMockExtension) {
        int mtu = RakConstants.MAXIMUM_MTU_SIZE;
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 55555);
        serverBootstrap().childHandler(new ChannelInitializer<RakChildChannel>() {
            @Override
            protected void initChannel(RakChildChannel ch) throws Exception {
                System.out.println("Server child channel initialized");
                ch.pipeline().addLast(new SimpleChannelInboundHandler<RakMessage>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, RakMessage msg) throws Exception {
                        ByteBuf content = msg.content();
                        int id = content.readUnsignedByte();//frame id
                        if (id != BedrockChannelInitializer.RAKNET_MINECRAFT_ID) {
                            log.error("Client receive a Invalid packet for frame ID!");
                            System.exit(1);
                        }

                        //batch packet
                        int packetLength = ByteBufVarInt.readUnsignedInt(content);
                        ByteBuf byteBuf = content.readSlice(packetLength);

                        //decode to game packet
                        int header = ByteBufVarInt.readUnsignedInt(byteBuf);
                        assert header == ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET;
                        DataPacket dataPacket = Registries.PACKET.get(header);
                        dataPacket.decode(HandleByteBuf.of(Unpooled.wrappedBuffer(byteBuf)));
                        RequestNetworkSettingsPacket target = (RequestNetworkSettingsPacket) dataPacket;
                        assert target.protocolVersion == ProtocolInfo.CURRENT_PROTOCOL;
                        gameMockExtension.stopNetworkTickLoop();
                    }
                });
            }
        }).bind(inetSocketAddress).syncUninterruptibly();
        clientBootstrap(mtu)
                .handler(new ChannelInitializer<RakClientChannel>() {
                    @Override
                    protected void initChannel(RakClientChannel ch) throws Exception {
                        System.out.println("Client channel initialized");
                        //raknet datagram -> channelinboudhander
                        ch.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        ByteBuf buf = ctx.alloc().buffer();//refCnt = 1
                                        assert buf.refCnt() == 1;
                                        buf.writeByte(BedrockChannelInitializer.RAKNET_MINECRAFT_ID);//frame id
                                        //batch packet
                                        ByteBufVarInt.writeUnsignedInt(buf, 6);//packet length
                                        //RequestNetworkSettingsPacket
                                        ByteBufVarInt.writeUnsignedInt(buf, ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET);//packet header
                                        buf.writeInt(ProtocolInfo.CURRENT_PROTOCOL);
                                        //RequestNetworkSettingsPacket
                                        ctx.channel().writeAndFlush(new RakMessage(buf));
                                    }
                                });
                    }
                })
                .connect(inetSocketAddress)
                .awaitUninterruptibly()
                .channel();
        gameMockExtension.mockNetworkTickLoop();
    }
}
