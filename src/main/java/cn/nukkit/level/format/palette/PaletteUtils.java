package cn.nukkit.level.format.palette;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.HashUtils;
import cn.nukkit.utils.SemVersion;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;

import java.io.IOException;

/**
 * Allay Project 11/12/2023
 *
 * @author Cool_Loong
 */
public class PaletteUtils {
    public static Pair<Integer, SemVersion> fastReadBlockHash(NBTInputStream input, ByteBuf byteBuf) {
        try {
            byteBuf.markReaderIndex();
            int typeId = input.readUnsignedByte();
            input.skipBytes(input.readUnsignedShort()); //Skip Root tag name
            return deserialize(input, byteBuf, typeId, 16);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Pair<Integer, SemVersion> deserialize(NBTInputStream input,
                                                         ByteBuf byteBuf,
                                                         int type,
                                                         int maxDepth
    ) throws IOException {
        if (maxDepth < 0) {
            throw new IllegalArgumentException("NBT compound is too deeply nested");
        }
        switch (type) {
            case Tag.TAG_End -> {
            }
            case Tag.TAG_Byte -> input.skipBytes(1);
            case Tag.TAG_Short -> input.skipBytes(2);
            case Tag.TAG_Int, Tag.TAG_Float -> input.skipBytes(4);
            case Tag.TAG_Long, Tag.TAG_Double -> input.skipBytes(8);
            case Tag.TAG_Byte_Array -> input.skipBytes(input.readInt());
            case Tag.TAG_String -> input.skipBytes(input.readUnsignedShort());
            case Tag.TAG_Compound -> {
                int nbtType;
                while ((nbtType = input.readUnsignedByte()) != Tag.TAG_End) {
                    String name;
                    int end = byteBuf.readerIndex();
                    name = input.readUTF();
                    if (name.equals("version")) {
                        int version = input.readInt();
                        byteBuf.resetReaderIndex();
                        if (version != ProtocolInfo.BLOCK_STATE_VERSION_NO_REVISION) {
                            return Pair.of(null, getSemVersion(version));
                        }
                        byte[] result = new byte[end - byteBuf.readerIndex()];
                        input.readFully(result);
                        result[result.length - 1] = 0;//because an End Tag be put when at the end serialize tag

                        input.skipBytes(input.readUnsignedShort());//UTF
                        deserialize(input, byteBuf, nbtType, maxDepth - 1);//Value
                        input.skipBytes(1);//end tag
                        int i = HashUtils.fnv1a_32(result);
                        if (i == 147887818) i = -2;//minecraft:unknown
                        return Pair.of(i, null);
                    }
                    deserialize(input, byteBuf, nbtType, maxDepth - 1);
                }
            }
            case Tag.TAG_List -> {
                int typeId = input.readUnsignedByte();
                int listLength = input.readInt();
                for (int i = 0; i < listLength; i++) {
                    deserialize(input, byteBuf, typeId, maxDepth - 1);
                }
            }
            case Tag.TAG_Int_Array -> input.skipBytes(input.readInt() * 4);
        }
        return null;
    }

    public static SemVersion getSemVersion(int version) {
        int major = (version >> 24) & 0xFF;
        int minor = (version >> 16) & 0xFF;
        int patch = (version >> 8) & 0xFF;
        int revision = version & 0xFF;
        return new SemVersion(major, minor, patch, revision, 0);
    }
}
