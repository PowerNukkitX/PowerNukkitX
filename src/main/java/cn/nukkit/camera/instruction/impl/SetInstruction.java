package cn.nukkit.camera.instruction.impl;

import cn.nukkit.api.DoNotModify;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.camera.data.CameraPreset;
import cn.nukkit.camera.data.Ease;
import cn.nukkit.camera.data.Pos;
import cn.nukkit.camera.data.Rot;
import cn.nukkit.camera.instruction.CameraInstruction;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nullable;

/**
 * @author daoge_cmd <br>
 * Date: 2023/6/11 <br>
 * PowerNukkitX Project <br>
 */
@PowerNukkitXOnly
@Since("1.20.0-r2")
@Builder
@Getter
public class SetInstruction implements CameraInstruction {

    @Nullable
    private final Ease ease;
    @Nullable
    private final Pos pos;
    @Nullable
    private final Rot rot;
    private final CameraPreset preset;

    @Override
    @DoNotModify
    public Tag serialize() {
        var tag = new CompoundTag("set");
        if (ease != null)
            tag.putCompound("ease", ease.serialize());
        if (pos != null)
            tag.putCompound("pos", pos.serialize());
        if (rot != null)
            tag.putCompound("rot", rot.serialize());
        tag.putInt("preset", preset.getId());
        return tag;
    }
}
