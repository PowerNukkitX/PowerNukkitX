package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PetteriM1
 */
public class EntityTropicalfish extends EntityFish {

    @Override
    @NotNull
    public String getIdentifier() {
        return TROPICALFISH;
    }

    private static final int[] VARIANTS = {0, 1};
    private static final int[] MARK_VARIANTS = {0, 1, 2, 3, 4, 5};
    private static final int[] COLOR2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
    public int color = 0;
    private int variant;
    private int mark_variant;
    private int color2;

    public EntityTropicalfish(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Tropical Fish";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("tropicalfish", "fish");
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.4f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(6);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.12f);
    }

    @Override
    public void initEntity() {
        super.initEntity();
        if (this.nbt.contains("Variant")) {
            this.variant = this.getNbt().getInt("Variant");
        } else {
            this.variant = getRandomVariant();
        }
        if (this.nbt.contains("Mark_Variant")) {
            this.mark_variant = this.getNbt().getInt("Mark_Variant");
        } else {
            this.mark_variant = getRandomMarkVariant();
        }
        if (!this.nbt.contains("Color")) {
            this.setColor(getRandomColor());
        } else {
            this.setColor(this.getNbt().getByte("Color"));
        }
        if (this.nbt.contains("Color2")) {
            this.color2 = this.getNbt().getInt("Color2");
        } else {
            this.color2 = getRandomColor2();
        }
        this.setDataProperty(ActorDataTypes.MARK_VARIANT, this.mark_variant);
        this.setDataProperty(ActorDataTypes.VARIANT, this.variant);
        this.setDataProperty(ActorDataTypes.COLOR_2_INDEX, (byte) this.color2);
    }

    private int getRandomColor() {
        return DyeColor.values()[ThreadLocalRandom.current().nextInt(0, 16)].getWoolData();
    }

    private int getRandomMarkVariant() {
        return MARK_VARIANTS[Utils.rand(0, MARK_VARIANTS.length - 1)];
    }

    private int getRandomColor2() {
        return COLOR2[Utils.rand(0, COLOR2.length - 1)];
    }

    private int getRandomVariant() {
        return VARIANTS[Utils.rand(0, VARIANTS.length - 1)];
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.putByte("Color", (byte) this.color);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        if (Utils.rand(0, 3) == 1) {
            return new Item[]{Item.get(Item.TROPICAL_FISH), Item.get(Item.BONE, 0, Utils.rand(1, 2))};
        }
        return new Item[]{Item.get(Item.TROPICAL_FISH)};
    }

    public int getColor() {
        return getNbt().getByte("Color");
    }

    public void setColor(int color) {
        this.color = color;
        this.setDataProperty(ActorDataTypes.COLOR_INDEX, (byte) color);
        this.nbt.putByte("Color", (byte) this.color);
    }

}
