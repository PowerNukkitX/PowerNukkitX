package cn.nukkit.block.customblock;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockPropertyData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 继承这个类实现自定义方块
 * 重写Block中的方法控制方块属性
 */
public abstract class BlockCustom extends Block {
    protected AtomicBoolean initialized = new AtomicBoolean(false);

    protected CompoundTag compoundTag;

    public BlockCustom() {
    }

    /**
     * 控制自定义方块的命名空间<br>(例如 wiki:test_block)
     */
    public abstract String getNamespace();

    /**
     * 控制自定义方块所用的材质名称<br>(例如材质图片test.png设置test)
     */
    public abstract String getTexture();

    /**
     * 控制自定义方块在创造栏中的分类
     *
     * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
     */
    public String getCreativeCategory() {
        return "construction";
    }

    /**
     * 控制自定义方块在创造栏中的组
     *
     * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
     */
    public String getCreativeCategoryGroup() {
        return "";
    }

    /**
     * 控制自定义方块的渲染方法<br>可选值:<br>opaque<br>alpha_test<br>blend<br>double_sided
     */
    public String getRenderMethod() {
        return "opaque";
    }

    /**
     * 控制自定义方块的形状<br>
     * Geometry identifier from geo file in 'RP/models/entity' folder
     */
    public Optional<String> getGeometry() {
        return Optional.empty();
    }

    @Override
    public double calculateBreakTime(@NotNull Item item) {
        return calculateBreakTime(item, null);
    }

    /**
     * 控制自定义方块的挖掘时间(单位s)
     */
    @Override
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        return 1;
    }

    @Override
    public int getId() {
        return Block.CUSTOM_BLOCK_ID_MAP.get(getNamespace().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String getName() {
        return this.getNamespace().split(":")[1].toLowerCase(Locale.ENGLISH);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (canHarvest(item)) {
            return new Item[]{new ItemBlock(Block.get(getId()))};
        } else return Item.EMPTY_ARRAY;
    }

    protected void initBlockPropertyData() {
        compoundTag = new CompoundTag()
                .putCompound("minecraft:creative_category", new CompoundTag()
                        .putString("category", getCreativeCategory())
                        .putString("group", getCreativeCategoryGroup()))
                .putCompound("minecraft:friction", new CompoundTag()
                        .putFloat("value", (float) this.getFrictionFactor()))
                .putCompound("minecraft:explosion_resistance", new CompoundTag()
                        .putInt("value", (int) this.getResistance()))
                .putCompound("minecraft:block_light_absorption", new CompoundTag()
                        .putFloat("value", (float) this.getLightFilter() / 15))
                .putCompound("minecraft:block_light_emission", new CompoundTag()
                        .putFloat("emission", (float) this.getLightLevel() / 15))
                .putCompound("minecraft:rotation", new CompoundTag()
                        .putFloat("x", 0)
                        .putFloat("y", 0)
                        .putFloat("z", 0))
                .putCompound("minecraft:destroy_time", new CompoundTag()
                        .putFloat("value", (float) (calculateBreakTime(Item.get(AIR)) * 2 / 3)))
                .putCompound("minecraft:material_instances", new CompoundTag()
                        .putCompound("mappings", new CompoundTag())
                        .putCompound("materials", new CompoundTag()
                                .putCompound("*", new CompoundTag()
                                        .putBoolean("ambient_occlusion", true)
                                        .putBoolean("face_dimming", true)
                                        .putString("render_method", getRenderMethod())
                                        .putString("texture", getTexture()))));
        if (this.getGeometry().isPresent()) {
            compoundTag.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", this.getGeometry().get()));
        }
    }

    public BlockPropertyData getBlockPropertyData() {
        if (initialized.compareAndSet(false, true)) {
            initBlockPropertyData();
        }
        return new BlockPropertyData(this.getNamespace().toLowerCase(Locale.ENGLISH), new CompoundTag().putCompound("components", compoundTag));
    }
}
