package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockPropertyData;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

/**
 * 继承这个类实现自定义方块
 * 重写Block中的方法控制方块属性
 */
public abstract class BlockCustom extends Block {
    private final String namespace;

    private final String texture;
    /**
     * 自定义方块需要传入方块的标识符
     *
     * @param namespace 方块的标识符,形如 test:test_block
     * @param texture 方块的材质名,与材质包对应(test_block.png 对应填写 test_block)
     */
    public BlockCustom(String namespace,String texture) {
        this.namespace = namespace.toLowerCase(Locale.ENGLISH);
        this.texture = texture;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getTexture() {
        return texture;
    }

    /**
     * 控制自定义方块的形状
     *
     * @return 自定义方块的形状
     */
    private Optional<String> getGeometry() {
        return Optional.empty();
    }

    public BlockPropertyData getBlockPropertyData() {
        CompoundTag componentsNBT = new CompoundTag()
                .putCompound("minecraft:creative_category", new CompoundTag()
                        .putString("category", "construction")
                        .putString("group", ""))
                .putCompound("minecraft:friction", new CompoundTag()
                        .putFloat("value", (float) this.getFrictionFactor()))
                .putCompound("minecraft:rotation", new CompoundTag()
                        .putFloat("x", 0)
                        .putFloat("y", 0)
                        .putFloat("z", 0))
                .putCompound("minecraft:destroy_time", new CompoundTag()
                        .putFloat("value", (float) (calculateBreakTime(Item.get(AIR)) * 2 / 3)))
                .putCompound("minecraft:material_instances", new CompoundTag()
                        .putCompound("mappings",new CompoundTag())
                        .putCompound("materials",new CompoundTag()
                                .putCompound("*",new CompoundTag()
                                        .putBoolean("ambient_occlusion",true)
                                        .putBoolean("face_dimming",true)
                                        .putString("render_method","opaque")
                                        .putString("texture",getTexture())))
                .putCompound("minecraft:explosion_resistance", new CompoundTag()
                        .putInt("value", (int) this.getResistance()))
                .putCompound("minecraft:block_light_absorption", new CompoundTag()
                        .putInt("value", this.getLightFilter()))
                .putCompound("minecraft:block_light_emission", new CompoundTag()
                        .putFloat("emission", this.getLightLevel())));

        if (this.getGeometry().isPresent()) {
            componentsNBT.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", this.getGeometry().get()));
        }

        return new BlockPropertyData(this.getNamespace(), new CompoundTag().putCompound("components", componentsNBT));
    }

    @Override
    public int getId() {
        return Block.CUSTOM_BLOCK_ID_MAP.get(this.namespace);
    }

    @Override
    public String getName() {
        return this.namespace.split(":")[1].toLowerCase(Locale.ENGLISH);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (canHarvest(item)) {
            return new Item[]{new ItemBlock(Block.get(getId()))};
        } else return Item.EMPTY_ARRAY;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public double calculateBreakTime(@NotNull Item item) {
        return calculateBreakTime(item, null);
    }

    /**
     * 控制自定义方块的挖掘时间需要重写该方法
     *
     * @return 自定义方块的挖掘时间
     */
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        return 1;
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }
}
