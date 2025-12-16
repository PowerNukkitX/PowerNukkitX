package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class EntityZombieNautilus extends EntityNautilus {

    @Override
    @NotNull public String getIdentifier() {
        return ZOMBIE_NAUTILUS;
    }

    public EntityZombieNautilus(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Zombie Nautilus";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombie_nautilus", "zombie", "undead", "mob");
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[] {
                Item.get(Item.ROTTEN_FLESH, 0, Utils.rand(0, 3))
        };
    }
}
