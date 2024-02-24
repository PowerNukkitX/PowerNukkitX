package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.Utils;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.ToString;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author CreeperFace
 * @since 5.3.2017
 */
@ToString
public class ClientboundMapItemDataPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET;
    public static final long[] EMPTY_LONGS = new long[0];

    public long[] eids = EMPTY_LONGS;

    public long mapId;
    public int update;
    public byte scale;
    public boolean isLocked;
    public int width;
    public int height;
    public int offsetX;
    public int offsetZ;

    public byte dimensionId;
    public BlockVector3 origin = new BlockVector3();

    public final List<MapTrackedObject> trackedObjects = new ObjectArrayList<>();
    public MapDecorator[] decorators = MapDecorator.EMPTY_ARRAY;
    public int[] colors = EmptyArrays.EMPTY_INTS;
    public BufferedImage image = null;

    //update
    public static final int TEXTURE_UPDATE = 0x02;
    public static final int DECORATIONS_UPDATE = 0x04;
    public static final int ENTITIES_UPDATE = 0x08;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityUniqueId(mapId);

        int update = 0;
        if (image != null || colors.length > 0) {
            update |= TEXTURE_UPDATE;
        }
        if (decorators.length > 0 || !trackedObjects.isEmpty()) {
            update |= DECORATIONS_UPDATE;
        }
        if (eids.length > 0) {
            update |= ENTITIES_UPDATE;
        }

        byteBuf.writeUnsignedVarInt(update);
        byteBuf.writeByte(this.dimensionId);
        byteBuf.writeBoolean(this.isLocked);
        byteBuf.writeBlockVector3(this.origin);

        if ((update & ENTITIES_UPDATE) != 0) { //TODO: find out what these are for
            byteBuf.writeUnsignedVarInt(eids.length);
            for (long eid : eids) {
                byteBuf.writeEntityUniqueId(eid);
            }
        }
        if ((update & (ENTITIES_UPDATE | TEXTURE_UPDATE | DECORATIONS_UPDATE)) != 0) {
            byteBuf.writeByte(this.scale);
        }

        if ((update & DECORATIONS_UPDATE) != 0) {
            byteBuf.writeUnsignedVarInt(this.trackedObjects.size());
            for (MapTrackedObject object : this.trackedObjects) {
                switch (object.getType()) {
                    case BLOCK -> {
                        byteBuf.writeIntLE(object.getType().ordinal());
                        byteBuf.writeBlockVector3(object.getPosition().getFloorX(), object.getPosition().getFloorY(), object.getPosition().getFloorZ());
                    }
                    case ENTITY -> {
                        byteBuf.writeIntLE(object.getType().ordinal());
                        byteBuf.writeEntityUniqueId(object.getEntityId());
                    }
                }
            }

            byteBuf.writeUnsignedVarInt(decorators.length);
            for (MapDecorator decorator : decorators) {
                byteBuf.writeByte(decorator.rotation);
                byteBuf.writeByte(decorator.icon);
                byteBuf.writeByte(decorator.offsetX);
                byteBuf.writeByte(decorator.offsetZ);
                byteBuf.writeString(decorator.label);
                byteBuf.writeVarInt(decorator.color.getRGB());
            }
        }

        if ((update & TEXTURE_UPDATE) != 0) {
            byteBuf.writeVarInt(width);
            byteBuf.writeVarInt(height);
            byteBuf.writeVarInt(offsetX);
            byteBuf.writeVarInt(offsetZ);

            if (image != null) {
                byteBuf.writeUnsignedVarInt(width * height);
                for (int y = 0; y < width; y++) {
                    for (int x = 0; x < height; x++) {
                        byteBuf.writeUnsignedVarInt(this.image.getRGB(x, y));
                    }
                }

                image.flush();
            } else if (colors.length > 0) {
                byteBuf.writeUnsignedVarInt(colors.length);
                for (int color : colors) {
                    byteBuf.writeUnsignedVarInt(color);
                }
            }
        }
    }

    public static class MapDecorator {


        public static final MapDecorator[] EMPTY_ARRAY = new MapDecorator[0];

        public byte rotation;
        public byte icon;
        public byte offsetX;
        public byte offsetZ;
        public String label;
        public Color color;
    }

    @Getter
    public static class MapTrackedObject {
        private final Type type;
        private long entityId;
        private Vector3 position;

        public MapTrackedObject(long entityId) {
            this.type = Type.ENTITY;
            this.entityId = entityId;
        }

        public MapTrackedObject(Vector3 position) {
            this.type = Type.BLOCK;
            this.position = position;
        }

        public enum Type {
            ENTITY,
            BLOCK
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
