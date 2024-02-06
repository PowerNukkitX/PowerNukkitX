package cn.nukkit.network;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Server;
import cn.nukkit.network.connection.netty.initializer.BedrockChannelInitializer;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.NetworkSettingsPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.ByteBufVarInt;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.channel.raknet.RakConstants;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.netty.channel.raknet.packet.RakMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.InetSocketAddress;

@Slf4j
@ExtendWith(GameMockExtension.class)
public class RakNetInterfaceTest {
    private static Bootstrap clientBootstrap(int mtu) {
        return new Bootstrap()
                .channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
                .group(new NioEventLoopGroup())
                .option(RakChannelOption.RAK_PROTOCOL_VERSION, 11)
                .option(RakChannelOption.RAK_MTU, mtu)
                .option(RakChannelOption.RAK_ORDERING_CHANNELS, 1);
    }

    @Test
    void test(GameMockExtension gameMockExtension) {
        int mtu = RakConstants.MAXIMUM_MTU_SIZE;
        System.out.println("Testing client with MTU " + mtu);
        System.out.println(Server.getInstance());
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 19132);
        Channel channel = clientBootstrap(mtu)
                .handler(new ChannelInitializer<RakClientChannel>() {
                    @Override
                    protected void initChannel(RakClientChannel ch) throws Exception {
                        System.out.println("Client channel initialized");
                        //raknet datagram -> channelinboudhander
                        ch.pipeline()
                                .addLast(new SimpleChannelInboundHandler<RakMessage>() {
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
                                        assert header == ProtocolInfo.NETWORK_SETTINGS_PACKET;
                                        DataPacket dataPacket = Registries.PACKET.get(header);
                                        byte[] data = new byte[byteBuf.readableBytes()];
                                        byteBuf.readBytes(data);
                                        dataPacket.setBuffer(data);
                                        dataPacket.decode();
                                        NetworkSettingsPacket target = (NetworkSettingsPacket) dataPacket;
                                        assert target.compressionAlgorithm == PacketCompressionAlgorithm.ZLIB;
                                        gameMockExtension.stop();
                                    }
                                })
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        ByteBuf buf = ctx.alloc().buffer();//refCnt = 1
                                        assert buf.refCnt() == 1;
                                        buf.writeByte(BedrockChannelInitializer.RAKNET_MINECRAFT_ID);//frame id
                                        //batch packet
                                        ByteBufVarInt.writeUnsignedInt(buf, 6);//packet length
                                        //RequestNetworkSettingsPacket
                                        ByteBufVarInt.writeUnsignedInt(buf, 193);//packet header
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
