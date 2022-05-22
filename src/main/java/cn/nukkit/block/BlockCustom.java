package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockPropertyData;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Optional;

/**
 * 继承这个类实现自定义方块
 * 重写Block中的方法控制方块属性
 */
public abstract class BlockCustom extends Block {
    private final String namespace;

    /**
     * 自定义方块需要传入方块的标识符
     * @param namespace 方块的标识符,形如 test:test_block
     */
    public BlockCustom(String namespace) {
        this.namespace = namespace.toLowerCase(Locale.ENGLISH);
    }

    public String getNamespace() {
        return this.namespace;
    }

    /**
     * 控制自定义方块挖掘时间
     * 目前无法控制不同工具不同的挖掘时间
     *
     * @return 自定义方块的挖掘时间，越大挖掘需要的时间越久(这个值设置8就等同于原版黑曜石的挖掘时间)
     */
    public abstract double getBreakTime();

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
                        .putString("category", "nature")
                        .putString("group", "itemGroup.name.ore"))
                .putCompound("minecraft:friction", new CompoundTag()
                        .putFloat("value", (float) this.getFrictionFactor()))
                .putCompound("minecraft:rotation", new CompoundTag()
                        .putFloat("x", 0)
                        .putFloat("y", 0)
                        .putFloat("z", 0))
                .putCompound("minecraft:destroy_time", new CompoundTag()
                        .putFloat("value", (float) getBreakTime()))
                .putCompound("minecraft:explosion_resistance", new CompoundTag()
                        .putInt("value", (int) this.getResistance()))
                .putCompound("minecraft:block_light_absorption", new CompoundTag()
                        .putInt("value", this.getLightFilter()))
                .putCompound("minecraft:block_light_emission", new CompoundTag()
                        .putFloat("emission", this.getLightLevel()));

        if (this.getGeometry().isPresent()) {
            componentsNBT.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", this.getGeometry().get()));
        }

        return new BlockPropertyData(this.getNamespace(), new CompoundTag()
                .putCompound("description", new CompoundTag()
                        .putString("identifier", this.getNamespace())
                        .putBoolean("is_experimental", false)
                        .putBoolean("register_to_creative_menu",true))
                .putCompound("components", componentsNBT));
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
        return new Item[]{new ItemBlock(Block.get(getId()))};
    }

    @Override
    public double calculateBreakTime(@Nonnull Item item) {
        return calculateBreakTime(item, null);
    }

    @Override
    public double calculateBreakTime(@Nonnull Item item, @Nullable Player player) {
        return getBreakTime();
    }

    /**
     * 控制自定义方块是否可以被该物品挖掘
     *
     * @param item 破坏该方块的物品
     * @return 是否可以被该物品挖掘, 返回false挖掘事件被取消
     */
    @Override
    public boolean isBreakable(Item item) {
        if (item.getTier() < getToolTier()) return false;
        if (getToolType() == ItemTool.TYPE_NONE) return true;
        else if (item.isShovel() && getToolType() == ItemTool.TYPE_SHOVEL) return true;
        else if (item.isPickaxe() && getToolType() == ItemTool.TYPE_PICKAXE) return true;
        else if (item.isAxe() && getToolType() == ItemTool.TYPE_AXE) return true;
        else if (item.isShears() && getToolType() == ItemTool.TYPE_SHEARS) return true;
        else return item.isHoe() && getToolType() == ItemTool.TYPE_HOE;
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }
}
