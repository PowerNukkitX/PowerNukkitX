package cn.nukkit.camera.data;

import cn.nukkit.Server;
import cn.nukkit.api.DoNotModify;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.UpdateSoftEnumPacket;
import cn.nukkit.player.Player;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

/**
 * @author daoge_cmd
 * @date 2023/6/11
 * PowerNukkitX Project
 */
@PowerNukkitXOnly
@Since("1.20.0-r2")
public final class CameraPreset {

    private static final Map<String, CameraPreset> PRESETS = new TreeMap<>();

    @DoNotModify
    public static Map<String, CameraPreset> getPresets() {
        return PRESETS;
    }

    @Nullable public static CameraPreset getPreset(String identifier) {
        return getPresets().get(identifier);
    }

    public static void registerCameraPresets(CameraPreset... presets) {
        for (var preset : presets) {
            if (PRESETS.containsKey(preset.getIdentifier()))
                throw new IllegalArgumentException("Camera preset " + preset.getIdentifier() + " already exists!");
            PRESETS.put(preset.getIdentifier(), preset);
            CommandEnum.CAMERA_PRESETS.updateSoftEnum(UpdateSoftEnumPacket.Type.ADD, preset.getIdentifier());
        }
        int id = 0;
        // 重新分配id
        for (var preset : presets) {
            preset.id = id++;
        }
        Server.getInstance().getPlayerManager().getOnlinePlayers().values().forEach(Player::sendCameraPresets);
    }

    public static final CameraPreset FIRST_PERSON;
    public static final CameraPreset FREE;
    public static final CameraPreset THIRD_PERSON;
    public static final CameraPreset THIRD_PERSON_FRONT;

    static {
        FIRST_PERSON =
                CameraPreset.builder().identifier("minecraft:first_person").build();
        FREE = CameraPreset.builder()
                .identifier("minecraft:free")
                .pos(new Pos(0, 0, 0))
                .rot(new Rot(0, 0))
                .build();
        THIRD_PERSON =
                CameraPreset.builder().identifier("minecraft:third_person").build();
        THIRD_PERSON_FRONT = CameraPreset.builder()
                .identifier("minecraft:third_person_front")
                .build();

        registerCameraPresets(FIRST_PERSON, FREE, THIRD_PERSON, THIRD_PERSON_FRONT);
    }

    @Getter
    private final String identifier;

    @Getter
    private final String inheritFrom;

    @Getter
    @Nullable private final Pos pos;

    @Getter
    @Nullable private final Rot rot;

    @Getter
    private int id;

    /**
     * Remember to call the registerCameraPresets() method to register!
     */
    @Builder
    public CameraPreset(String identifier, String inheritFrom, @Nullable Pos pos, @Nullable Rot rot) {
        this.identifier = identifier;
        this.inheritFrom = inheritFrom != null ? inheritFrom : "";
        this.pos = pos;
        this.rot = rot;
    }

    private CompoundTag cache;

    @DoNotModify
    public CompoundTag serialize() {
        if (cache == null) {
            cache = new CompoundTag().putString("identifier", identifier).putString("inherit_from", inheritFrom);
            if (pos != null) {
                cache.putFloat("pos_x", pos.x()).putFloat("pos_y", pos.y()).putFloat("pos_z", pos.z());
            }
            if (rot != null) {
                cache.putFloat("rot_x", rot.x()).putFloat("rot_y", rot.y());
            }
        }
        return cache;
    }
}
