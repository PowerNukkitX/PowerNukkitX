package cn.nukkit.block.customblock.data;

import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import lombok.Builder;
import lombok.Getter;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

import javax.annotation.Nullable;


@Builder
@Getter
public class Component implements NBTData {
    private NbtMap result = NbtMap.EMPTY;
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
    Float friction;
    @Nullable
    Geometry geometry;
    @Nullable
    Materials materialInstances;
    @Nullable
    Transformation transformation;
    @Nullable
    Boolean unitCube;
    @Nullable
    Vector3f rotation;

    public NbtMap toCompoundTag() {
        final NbtMapBuilder builder = this.result.toBuilder();
        if (unitCube != null) {
            builder.putCompound("minecraft:unit_cube", NbtMap.EMPTY);
        }
        if (collisionBox != null) {
            builder.putCompound("minecraft:collision_box", collisionBox.toCompoundTag());
        }
        if (selectionBox != null) {
            builder.putCompound("minecraft:selection_box", selectionBox.toCompoundTag());
        }
        if (craftingTable != null) {
            builder.putCompound("minecraft:crafting_table", craftingTable.toCompoundTag());
        }
        if (destructibleByMining != null) {
            builder.putCompound("minecraft:destructible_by_mining", NbtMap.builder()
                    .putFloat("value", destructibleByMining)
                    .build());
        }
        if (destructibleByExplosion != null) {
            builder.putCompound("minecraft:destructible_by_explosion", NbtMap.builder()
                    .putInt("explosion_resistance", destructibleByExplosion)
                    .build());
        }
        if (displayName != null) {
            builder.putCompound("minecraft:display_name", NbtMap.builder()
                    .putString("value", displayName)
                    .build());//todo 验证
        }
        if (lightEmission != null) {
            builder.putCompound("minecraft:light_emission", NbtMap.builder()
                    .putByte("emission", lightEmission.byteValue())
                    .build());
        }
        if (lightDampening != null) {
            builder.putCompound("minecraft:light_dampening", NbtMap.builder()
                    .putByte("lightLevel", lightDampening.byteValue())
                    .build());
        }
        if (friction != null) {
            builder.putCompound("minecraft:friction", NbtMap.builder()
                    .putFloat("value", (float) Math.min(friction, 0.9))
                    .build());
        }
        if (this.geometry != null) {
            builder.putCompound("minecraft:geometry", geometry.toCompoundTag());
            builder.remove("minecraft:unit_cube");
        }
        if (materialInstances != null) {
            builder.putCompound("minecraft:material_instances", NbtMap.builder()
                    .putCompound("mappings", NbtMap.EMPTY)
                    .putCompound("materials", materialInstances.toCompoundTag())
                    .build());
        }
        if (transformation != null) {
            builder.putCompound("minecraft:transformation", transformation.toCompoundTag());
        }
        if (rotation != null) {
            builder.putCompound("minecraft:transformation", new Transformation(new Vector3(0, 0, 0), new Vector3(1, 1, 1), rotation.asVector3()).toCompoundTag());
        }
        this.result = builder.build();
        return this.result;
    }
}
