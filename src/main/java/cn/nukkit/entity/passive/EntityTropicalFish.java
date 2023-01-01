package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PetteriM1
 */
public class EntityTropicalFish extends EntityFish {

    public static final int NETWORK_ID = 111;
    public int color = 0;
    private static final int[] VARIANTS = {0, 1};
    private static final int[] MARK_VARIANTS = {0, 1, 2, 3, 4, 5};
    private static final int[] COLOR2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
    private int variant;
    private int mark_variant;
    private int color2;

    public EntityTropicalFish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Tropical Fish";
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
    public void initEntity() {
        this.setMaxHealth(6);
        super.initEntity();
        if (this.namedTag.contains("Variant")) {
            this.variant = this.namedTag.getInt("Variant");
        } else {
            this.variant = getRandomVariant();
        }
        if (this.namedTag.contains("Mark_Variant")) {
            this.mark_variant = this.namedTag.getInt("Mark_Variant");
        } else {
            this.mark_variant = getRandomMarkVariant();
        }
        if (!this.namedTag.contains("Color")) {
            this.setColor(getRandomColor());
        } else {
            this.setColor(this.namedTag.getByte("Color"));
        }
        if (this.namedTag.contains("Color2")) {
            this.color2 = this.namedTag.getInt("Color2");
        } else {
            this.color2 = getRandomColor2();
        }
        this.setDataProperty(new IntEntityData(DATA_MARK_VARIANT, this.mark_variant));
        this.setDataProperty(new IntEntityData(DATA_VARIANT, this.variant));
        this.setDataProperty(new IntEntityData(DATA_COLOR_2, this.color2));
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

        this.namedTag.putByte("Color", this.color);
    }

    @Override
    public Item[] getDrops() {
        if (Utils.rand(0, 3) == 1) {
            return new Item[]{Item.get(Item.CLOWNFISH), Item.get(Item.BONE, 0, Utils.rand(1, 2))};
        }
        return new Item[]{Item.get(Item.CLOWNFISH)};
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    public void setColor(int color) {
        this.color = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color));
        this.namedTag.putByte("Color", this.color);
    }

}
