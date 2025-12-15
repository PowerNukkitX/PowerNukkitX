package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityCod extends EntityFish {
    @Override
    @NotNull public String getIdentifier() {
        return COD;
    }

    public EntityCod(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Cod";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("cod", "mob");
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.3f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(3);
        super.initEntity();
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        List<Item> drops = new ArrayList<>();
        drops.add(Item.get(
                this.isOnFire() ? Item.COOKED_COD : Item.COD,
                0,
                1
        ));

        float boneChance = 0.25f + (looting * 0.01f);
        if (Utils.rand(0f, 1f) < boneChance) {
            int boneAmount = Utils.rand(1 + looting, 2 + (looting * 2));
            drops.add(Item.get(Item.BONE, 0, boneAmount));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }
}
