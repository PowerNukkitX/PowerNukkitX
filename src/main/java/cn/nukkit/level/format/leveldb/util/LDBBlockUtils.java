package cn.nukkit.level.format.leveldb.util;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.StringTag;

public final class LDBBlockUtils {
    public static CompoundTag blockState2Nbt(BlockState blockState) {
        var tag = new CompoundTag()
                .putString("name", blockState.getPersistenceName())
                .putInt("version", blockState.getVersion());
        var properties = blockState.getProperties();
        var stateTag = BlockStateRegistry.getBlockStateNbtTemplate(blockState.getBlockId());
        if (stateTag == null) {
            stateTag = new CompoundTag();
        } else {
            if (blockState.getVersion() == -1) {
                tag.putInt("version", stateTag.getInt("version"));
            }
            stateTag = stateTag.getCompound("states");
        }
        for (var each : properties.getNames()) {
            var entry = stateTag.get(each);
            if (entry == null) {
                continue;
            }
            if (entry instanceof IntTag) {
                stateTag.putInt(each, blockState.getIntValue(each));
            } else if (entry instanceof ByteTag) {
                var value = blockState.getPropertyValue(each);
                if (value instanceof Boolean booleanValue) {
                    stateTag.putByte(each, booleanValue ? 1 : 0);
                } else {
                    stateTag.putByte(each, blockState.getIntValue(each));
                }
            } else if (entry instanceof StringTag) {
                stateTag.putString(each, blockState.getPersistenceValue(each));
            }
        }
        tag.putCompound("states", stateTag);
        return tag;
    }

    public static BlockState nbt2BlockState(CompoundTag nbt) {
        var blockState = BlockState.of(nbt.getString("name"), true);
        blockState.setVersion(nbt.getInt("version"));
        var stateTag = nbt.getCompound("states");
        for (var each : stateTag.getTags().entrySet()) {
            var value = each.getValue();
            if (value instanceof IntTag intTag) {
                blockState = blockState.withProperty(each.getKey(), intTag.data);
            } else if (value instanceof ByteTag byteTag) {
                if (blockState.getProperty(each.getKey()).getBitSize() == 1) {
                    blockState = blockState.withProperty(each.getKey(), byteTag.data == 1);
                } else {
                    blockState = blockState.withProperty(each.getKey(), byteTag.data);
                }
            } else if (value instanceof StringTag stringTag) {
                blockState = blockState.withProperty(each.getKey(), stringTag.data);
            }
        }
        return blockState;
    }
}
