package cn.nukkit.entity.mob;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class EntityParched extends EntitySkeleton {
    @Override
    @NotNull public String getIdentifier() {
        return PARCHED;
    }


    public EntityParched(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    @Override
    protected void initEntity() {
        this.setMaxHealth(16);
        super.initEntity();
        if (getItemInHand().isNull()) {
            setItemInHand(Item.get(ItemID.BOW));
        }
    }

    @Override
    public String getOriginalName() {
        return "Parched";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("parched", "skeleton", "undead", "monster", "mob");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        int bones = Utils.rand(0, 2 + looting);
        if (bones > 0) {
            drops.add(Item.get(Item.BONE, 0, bones));
        }

        int arrows = Utils.rand(0, 2 + looting);
        if (arrows > 0) {
            drops.add(Item.get(Item.ARROW, 0, arrows));
        }

        //ToDo Weakness arrow

        return drops.toArray(Item.EMPTY_ARRAY);
    }

}
