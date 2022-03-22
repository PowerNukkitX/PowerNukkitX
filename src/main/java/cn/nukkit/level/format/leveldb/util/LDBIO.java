package cn.nukkit.level.format.leveldb.util;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.leveldb.palette.Palette;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;

import java.util.Set;

public final class LDBIO {
    public static Palette<BlockState> readPalette(BinaryStream stream) {
        Palette<BlockState> palette = new Palette<>();
        int paletteLength = stream.getLInt();
        for (int i = 0; i < paletteLength; i++) {
            CompoundTag compound = stream.getLTag();
            palette.addEntry(LDBBlockUtils.nbt2BlockState(compound));
        }
        return palette;
    }

    public static void writePalette(BinaryStream stream, Palette<BlockState> palette) {
        Set<BlockState> entries = palette.getEntries();
        stream.putLInt(entries.size());
        for (BlockState data : entries) {
            stream.putLTag(LDBBlockUtils.blockState2Nbt(data));
        }
    }
}
