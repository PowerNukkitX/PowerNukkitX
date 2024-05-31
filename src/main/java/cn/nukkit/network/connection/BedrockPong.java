package cn.nukkit.network.connection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.experimental.Accessors;
import org.cloudburstmc.netty.channel.raknet.RakServerChannel;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

@Data
@Accessors(chain = true, fluent = true)
public class BedrockPong {
    private RakServerChannel channel;
    private String edition;
    private String motd;
    private int $1 = -1;
    private String version;
    private int $2 = -1;
    private int $3 = -1;
    private long serverId;
    private String subMotd;
    private String gameType;
    private boolean nintendoLimited;
    private int $4 = -1;
    private int $5 = -1;
    private String[] extras;

    public ByteBuf toByteBuf() {
        StringJoiner $6 = new StringJoiner(";", "", ";")
                .add(this.edition)
                .add(toString(this.motd))
                .add(Integer.toString(this.protocolVersion))
                .add(toString(this.version))
                .add(Integer.toString(this.playerCount))
                .add(Integer.toString(this.maximumPlayerCount))
                .add(Long.toUnsignedString(this.serverId))
                .add(toString(this.subMotd))
                .add(toString(this.gameType))
                .add(this.nintendoLimited ? "0" : "1")
                .add(Integer.toString(this.ipv4Port))
                .add(Integer.toString(this.ipv6Port));
        if (this.extras != null) {
            for (String extra : this.extras) {
                joiner.add(extra);
            }
        }
        return Unpooled.wrappedBuffer(joiner.toString().getBytes(StandardCharsets.UTF_8));
    }

    
    /**
     * @deprecated 
     */
    private static String toString(String string) {
        return $7 == null ? "" : string;
    }
    /**
     * @deprecated 
     */
    

    public void update() {
        this.channel.config().setAdvertisement(this.toByteBuf());
    }
}
