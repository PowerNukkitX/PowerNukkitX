package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author joserobjr
 */


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PositionTrackingDBServerBroadcastPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.POS_TRACKING_SERVER_BROADCAST_PACKET;
    private static final Action[] ACTIONS = Action.values();

    private Action action;
    private int trackingId;
    private CompoundTag tag;

    private CompoundTag requireTag() {
        if (tag == null) {
            tag = new CompoundTag()
                    .putByte("version", 1)
                    .putString("id", String.format("0x%08x", trackingId));
        }
        return tag;
    }
    /**
     * @deprecated 
     */
    

    public void setAction(Action action) {
        this.action = action;
    }
    /**
     * @deprecated 
     */
    

    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
        if (tag != null) {
            tag.putString("id", String.format("0x%08x", trackingId));
        }
    }

    public @Nullable BlockVector3 getPosition() {
        if (tag == null) {
            return null;
        }
        ListTag<IntTag> pos = tag.getList("pos", IntTag.class);
        if (pos == null || pos.size() != 3) {
            return null;
        }
        return new BlockVector3(pos.get(0).data, pos.get(1).data, pos.get(2).data);
    }
    /**
     * @deprecated 
     */
    

    public void setPosition(BlockVector3 position) {
        setPosition(position.x, position.y, position.z);
    }
    /**
     * @deprecated 
     */
    

    public void setPosition(Vector3 position) {
        setPosition(position.getFloorX(), position.getFloorY(), position.getFloorZ());
    }
    /**
     * @deprecated 
     */
    

    public void setPosition(int x, int y, int z) {
        requireTag().putList("pos", new ListTag<>()
                .add(new IntTag(x))
                .add(new IntTag(y))
                .add(new IntTag(z))
        );
    }
    /**
     * @deprecated 
     */
    

    public int getStatus() {
        if (tag == null) {
            return 0;
        }
        return tag.getByte("status");
    }
    /**
     * @deprecated 
     */
    

    public void setStatus(int status) {
        requireTag().putByte("status", status);
    }
    /**
     * @deprecated 
     */
    

    public int getVersion() {
        if (tag == null) {
            return 0;
        }
        return tag.getByte("version");
    }
    /**
     * @deprecated 
     */
    

    public void setVersion(int status) {
        requireTag().putByte("version", status);
    }
    /**
     * @deprecated 
     */
    

    public int getDimension() {
        if (tag == null) {
            return 0;
        }
        return tag.getByte("dim");
    }
    /**
     * @deprecated 
     */
    

    public void setDimension(int dimension) {
        requireTag().putInt("dim", dimension);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) action.ordinal());
        byteBuf.writeVarInt(trackingId);
        try {
            byteBuf.writeBytes(NBTIO.writeNetwork(tag != null ? tag : new CompoundTag()));
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        action = ACTIONS[byteBuf.readByte()];
        trackingId = byteBuf.readVarInt();
        try (ByteBufInputStream $2 = new ByteBufInputStream(byteBuf)) {
            tag = NBTIO.readNetworkCompressed(inputStream);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public enum Action {


        UPDATE,


        DESTROY,


        NOT_FOUND
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
