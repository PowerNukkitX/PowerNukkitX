package org.powernukkit.tools;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import lombok.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author joserobjr
 * @since 2021-09-24
 */
public class CurrentBlockMapReader {
    public static void main(String[] args) throws IOException {
        BinaryStream stream = new BinaryStream(Files.readAllBytes(Paths.get("dumps/r12_to_current_block_map.bin")));
        List<CurrentBlockMapEntry> list = new ArrayList<>();
        while(!stream.feof()){
            String id = stream.getString();
            int meta = stream.getLShort();

            int offset = stream.getOffset();
            byte[] buffer = stream.getBuffer();
            ByteArrayInputStream is = new ByteArrayInputStream(buffer, offset, buffer.length);
            int initial = is.available();
            CompoundTag state = NBTIO.read(is, ByteOrder.LITTLE_ENDIAN, true);
            offset += initial - is.available();
            stream.setOffset(offset);
            list.add(new CurrentBlockMapEntry(id, meta, state));
        }
        Files.write(Paths.get("dumps/r12_to_current_block_map.bin.txt"), list.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Value static class CurrentBlockMapEntry {
        String id;
        int meta;
        CompoundTag state;
    }
}
