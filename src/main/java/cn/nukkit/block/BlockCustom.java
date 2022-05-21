package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockPropertyData;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
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
     *
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
     *
     * @return 自定义方块的挖掘时间，越大挖掘需要的时间越久
     */
    public abstract double getBreakTime();

    private Optional<String> getGeometry() {
        return Optional.empty();
    }

    public BlockPropertyData getBlockPropertyData() {
        /*CompoundTag componentsNBT = new CompoundTag()
                .putCompound("minecraft:creative_category",new CompoundTag()
                        .putString("category","nature"))
                .putString("group","itemGroup.name.ore")
                .putCompound("minecraft:block_light_absorption", new CompoundTag()
                        .putInt("value", this.getLightFilter()))
                .putCompound("minecraft:block_light_emission", new CompoundTag()
                        .putFloat("emission", this.getLightLevel()))
                .putCompound("minecraft:friction", new CompoundTag()
                        .putFloat("value", (float) this.getResistance()))
                .putCompound("minecraft:rotation", new CompoundTag()
                        .putFloat("x", 0)
                        .putFloat("y", 0)
                        .putFloat("z", 0));

        if (this.getGeometry().isPresent()) {
            componentsNBT.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", this.getGeometry().get()));
        }

        return new BlockPropertyData(this.getNamespace(), new CompoundTag()
                .putCompound("description",new CompoundTag()
                        .putString("identifier",this.namespace)
                        .putBoolean("is_experimental",false)
                        .putBoolean("register_to_creative_menu",true))
                .putList(new ListTag<StringTag>("blockTags").add(new StringTag("","dextenblocks")))
                .putCompound("components", componentsNBT));*/
        CompoundTag componentsNBT = new CompoundTag()
                .putCompound("minecraft:block_light_emission", new CompoundTag()
                        .putFloat("emission", this.getLightLevel()))
                .putCompound("minecraft:friction", new CompoundTag()
                        .putFloat("value", (float) this.getFrictionFactor()))
                .putCompound("minecraft:rotation", new CompoundTag()
                        .putFloat("x", 0)
                        .putFloat("y", 0)
                        .putFloat("z", 0))
                .putCompound("minecraft:destroy_time", new CompoundTag()
                        .putFloat("value", (float) getBreakTime() - 2))//TODO: 2022/5/21 找到 destroy_time与PNX内部BreakTime的对应关系
                .putCompound("minecraft:explosion_resistance", new CompoundTag()
                        .putInt("value", (int) this.getResistance()))
                .putCompound("minecraft:block_light_absorption", new CompoundTag()
                        .putInt("value", this.getLightFilter()))
                .putCompound("minecraft:creative_category", new CompoundTag()
                        .putString("category", "Construction"));

        if (this.getGeometry().isPresent()) {
            componentsNBT.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", this.getGeometry().get()));
        }

        return new BlockPropertyData(this.getNamespace(), new CompoundTag()
                .putCompound("components", componentsNBT));
    }

    @Override
    public boolean onBreak(Item item) {
        return super.onBreak(item);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new ItemBlock(Block.get(getId()))};
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
    public double calculateBreakTime(@Nonnull Item item) {
        return calculateBreakTime(item, null);
    }

    @Override
    public double calculateBreakTime(@Nonnull Item item, @Nullable Player player) {
        return getBreakTime();
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }
}
