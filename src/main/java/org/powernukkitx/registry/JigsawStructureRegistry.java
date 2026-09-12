package org.powernukkitx.registry;

import lombok.Getter;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.packet.JigsawStructureDataPacket;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * The jigsaw structure data registry
 *
 * @author xRookieFight
 * @since 12/09/2026
 */
public final class JigsawStructureRegistry {
    @Getter
    private static JigsawStructureDataPacket PACKET = new JigsawStructureDataPacket();

    public void init() {
        try (var stream = JigsawStructureRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/jigsaw_structure_data.nbt");
             var nbtInputStream = NbtUtils.createGZIPReader(stream)) {
            final JigsawStructureDataPacket packet = new JigsawStructureDataPacket();
            packet.setJigsawStructureDataTag((NbtMap) nbtInputStream.readTag());
            PACKET = packet;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
