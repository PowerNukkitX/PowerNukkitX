package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityBlaze extends EntityMob implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return BLAZE;
    }

    public EntityBlaze(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public String getOriginalName() {
        return "Blaze";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public int getFrostbiteInjury() {
        return 5;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.BLAZE_ROD, 0, Utils.rand(0, 1))};
    }
}
