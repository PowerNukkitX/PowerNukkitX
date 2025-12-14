package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntitySkeletonHorse extends EntityAnimal implements EntitySmite, EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return SKELETON_HORSE;
    }

    public EntitySkeletonHorse(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.6f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(15);
        super.initEntity();
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 2) != 0) {
            int amount = Utils.rand(0, 2 + looting);
            if (amount > 0) {
                return new Item[]{
                        Item.get(Item.BONE, 0, amount)
                };
            }
        }

        return Item.EMPTY_ARRAY;
    }


    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public String getOriginalName() {
        return "Skeleton Horse";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("skeletonhorse", "undead", "mob");
    }

    @Override
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }
}
