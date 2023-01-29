package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

@Since("1.19.60-r1")
@PowerNukkitXOnly
@Builder
@Getter
public class Component implements NBTData {
    private final CompoundTag result = new CompoundTag("components");
    @Nullable
    CollisionBox collisionBox;
    @Nullable
    SelectionBox selectionBox;
    @Nullable
    CraftingTable craftingTable;
    @Nullable
    Float destructibleByMining;
    @Nullable
    Integer destructibleByExplosion;
    @Nullable
    String displayName;
    @Nullable
    Integer lightEmission;
    @Nullable
    Integer lightDampening;
    @Nullable
    Integer friction;
    @Nullable
    String geometry;
    @Nullable
    Materials materialInstances;
    @Nullable
    List<BoneCondition> partVisibility;
    @Nullable
    Vector3f rotation;

    public CompoundTag toCompoundTag() {
        if (this.geometry != null) {
            this.result.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", geometry.toLowerCase(Locale.ENGLISH)));
        } else this.result.putCompound("minecraft:unit_cube", new CompoundTag());
        if (collisionBox != null) {
            this.result.putCompound(collisionBox.toCompoundTag());
        }
        if (selectionBox != null) {
            this.result.putCompound(selectionBox.toCompoundTag());
        }
        if (craftingTable != null) {
            this.result.putCompound(craftingTable.toCompoundTag());
        }
        if (destructibleByMining != null) {
            this.result.putCompound("minecraft:destructible_by_mining", new CompoundTag()
                    .putFloat("value", destructibleByMining));
        }
        if (destructibleByExplosion != null) {
            this.result.putCompound("minecraft:destructible_by_explosion", new CompoundTag()
                    .putInt("explosion_resistance", destructibleByExplosion));
        }
        if (displayName != null) {
            this.result.putCompound("minecraft:display_name", new CompoundTag()
                    .putString("value", displayName));//todo 验证
        }
        if (lightEmission != null) {
            this.result.putCompound("minecraft:light_emission", new CompoundTag()
                    .putByte("emission", lightEmission.byteValue()));
        }
        if (lightDampening != null) {
            this.result.putCompound("minecraft:light_dampening", new CompoundTag()
                    .putByte("lightLevel", lightDampening.byteValue()));
        }
        if (friction != null) {
            this.result.putCompound("minecraft:friction", new CompoundTag()
                    .putByte("value", friction.byteValue()));
        }
        if (geometry != null) {
            this.result.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", geometry.toLowerCase(Locale.ENGLISH)));
        }
        if (materialInstances != null) {
            this.result.putCompound("minecraft:material_instances", new CompoundTag()
                    .putCompound("mappings", new CompoundTag())
                    .putCompound("materials", materialInstances.toCompoundTag()));
        }
        if (partVisibility != null) {
            var boneConditionsNBT = new CompoundTag("boneConditions");
            for (var boneCondition : partVisibility) {
                boneConditionsNBT.putCompound(boneCondition.toCompoundTag());
            }
            this.result.putCompound("minecraft:part_visibility", new CompoundTag().putCompound(boneConditionsNBT));
        }
        if (rotation != null) {
            this.result.putCompound("minecraft:rotation", new CompoundTag()
                    .putFloat("x", rotation.x)
                    .putFloat("y", rotation.y)
                    .putFloat("z", rotation.z));
        }
        return this.result;
    }
}
