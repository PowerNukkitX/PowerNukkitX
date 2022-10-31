package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Arrays;
import java.util.List;

/**
 * 用于控制自定义方块的变化特征，可用于实现条件渲染，部分渲染等功能。一个方块可以有多个变化特征。
 * <p>
 * Used to control the {@link Permutation} of custom blocks, which can be used to implement conditional rendering, partial rendering and other functions.
 *
 * @see <a href="https://wiki.bedrock.dev/blocks/blocks-16.html#block-permutations">wiki.bedrock.dev</a>
 */
@PowerNukkitXOnly
@Since("1.19.31-r1")
public class Permutations {
    private final ListTag<CompoundTag> permutations;

    public Permutations(Permutation.PermutationBuilder... permutations) {
        this(Arrays.stream(permutations).toList());
    }

    public Permutations(List<Permutation.PermutationBuilder> permutations) {
        var listTag = new ListTag<CompoundTag>();
        for (var per : permutations) {
            listTag.add(getPermutationData(per.build()));
        }
        this.permutations = listTag;
    }

    private CompoundTag getPermutationData(Permutation permutation) {
        return new CompoundTag()
                .putCompound("components", new CompoundTag()
                        .putCompound("minecraft:collision_box", new CompoundTag()
                                .putBoolean("enabled", permutation.collision_box_enabled)
                                .putList(new ListTag<FloatTag>("origin")
                                        .add(new FloatTag("", permutation.collision_box_origin.x))
                                        .add(new FloatTag("", permutation.collision_box_origin.y))
                                        .add(new FloatTag("", permutation.collision_box_origin.z)))
                                .putList(new ListTag<FloatTag>("size")
                                        .add(new FloatTag("", permutation.collision_box_size.x))
                                        .add(new FloatTag("", permutation.collision_box_size.y))
                                        .add(new FloatTag("", permutation.collision_box_size.z))))
                        .putCompound("minecraft:selection_box", new CompoundTag()
                                .putBoolean("enabled", permutation.selection_box_enabled)
                                .putList(new ListTag<FloatTag>("origin")
                                        .add(new FloatTag("", permutation.selection_box_origin.x))
                                        .add(new FloatTag("", permutation.selection_box_origin.y))
                                        .add(new FloatTag("", permutation.selection_box_origin.z)))
                                .putList(new ListTag<FloatTag>("size")
                                        .add(new FloatTag("", permutation.selection_box_size.x))
                                        .add(new FloatTag("", permutation.selection_box_size.y))
                                        .add(new FloatTag("", permutation.selection_box_size.z)))))
                .putString("condition", permutation.condition);
    }

    public ListTag<CompoundTag> data() {
        return permutations;
    }
}
