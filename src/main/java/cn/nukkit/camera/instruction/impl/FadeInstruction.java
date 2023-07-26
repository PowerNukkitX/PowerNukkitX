package cn.nukkit.camera.instruction.impl;

import cn.nukkit.api.DoNotModify;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.camera.data.Color;
import cn.nukkit.camera.data.Time;
import cn.nukkit.camera.instruction.CameraInstruction;
import cn.nukkit.nbt.tag.CompoundTag;
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
@Builder
@Getter
public class FadeInstruction implements CameraInstruction {

    @Nullable private final Color color;

    @Nullable private final Time time;

    @Override
    @DoNotModify
    public CompoundTag serialize() {
        var nbt = new CompoundTag("fade");
        if (color != null) nbt.putCompound("color", color.serialize());
        if (time != null) nbt.putCompound("time", time.serialize());
        return nbt;
    }
}
