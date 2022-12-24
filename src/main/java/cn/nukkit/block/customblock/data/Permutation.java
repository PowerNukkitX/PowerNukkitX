package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * The type Permutation builder.
 */
@PowerNukkitXOnly
@Since("1.19.31-r1")
@Builder
@Getter
public class Permutation {
    Boolean collision_box_enabled;
    Vector3f collision_box_origin;
    Vector3f collision_box_size;
    Boolean selection_box_enabled;
    Vector3f selection_box_origin;
    Vector3f selection_box_size;
    /**
     * The molang condition.
     */
    String condition;
    @Builder.Default
    List<CompoundTag> components = List.of();
}
