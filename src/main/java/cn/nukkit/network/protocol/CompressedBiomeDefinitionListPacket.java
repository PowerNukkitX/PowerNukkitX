package cn.nukkit.network.protocol;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompressedBiomeDefinitionListPacket extends DataPacket {
    public CompoundTag definitions;
    protected static final byte[] COMPRESSED_INDICATOR = new byte[]{(byte) 0xe4, (byte) 0x92, 0x3f, 0x43, 0x4f, 0x4d, 0x50, 0x52, 0x45, 0x53, 0x53, 0x45, 0x44}; // __?COMPRESSED

    @Override
    public void encode(HandleByteBuf byteBuf) {
        ByteBuf compressed = byteBuf.alloc().ioBuffer();

        writeCompressed(definitions, compressed);

        byteBuf.writeUnsignedVarInt(compressed.readableBytes());
        byteBuf.writeBytes(compressed);

        compressed.release();
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int length = byteBuf.readUnsignedVarInt();
        ByteBuf slice = byteBuf.readBytes(length);

        this.definitions = readCompressed(slice, COMPRESSED_INDICATOR.length);

        slice.release();
    }

    @Override
    public int pid() {
        return ProtocolInfo.COMPRESSED_BIOME_DEFINITIONS_LIST;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    protected CompoundTag readCompressed(ByteBuf buffer, int indicatorLength) {
        buffer.skipBytes(indicatorLength);

        int dictSize = buffer.readUnsignedShortLE();
        ByteBuf[] dictionary = new ByteBuf[dictSize];

        ByteBuf decompressed = Unpooled.buffer();

        try {
            for (int i = 0; i < dictSize; i++) {
                int len = buffer.readUnsignedByte();
                dictionary[i] = buffer.readBytes(len);
            }

            while (buffer.isReadable()) {
                int key = buffer.readUnsignedByte();

                if (key != 0xff) {
                    decompressed.writeByte(key);
                    continue;
                }

                int index = buffer.readUnsignedShortLE();

                if (index >= 0 && index < dictionary.length) {
                    decompressed.writeBytes(dictionary[index].slice());
                } else {
                    decompressed.writeByte(key);
                }
            }

            HandleByteBuf temp = HandleByteBuf.of(decompressed);
            return temp.readTag();

        } finally {
            decompressed.release();

            for (ByteBuf buf : dictionary) {
                if (buf != null) buf.release();
            }
        }
    }

    private void writeCompressed(CompoundTag tag, ByteBuf buffer) {
        buffer.writeBytes(COMPRESSED_INDICATOR);

        buffer.writeShortLE(0);

        ByteBuf serialized = buffer.alloc().ioBuffer();
        HandleByteBuf tmp = HandleByteBuf.of(serialized);
        tmp.writeTag(tag);

        while (serialized.isReadable()) {
            int key = serialized.readUnsignedByte();
            buffer.writeByte(key);

            if (key == 0xff) {
                buffer.writeShortLE(1);
            }
        }

        serialized.release();
    }
}
