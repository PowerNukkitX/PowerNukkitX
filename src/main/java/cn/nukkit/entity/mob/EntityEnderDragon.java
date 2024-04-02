package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityEnderDragon extends EntityMob implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return ENDER_DRAGON;
    }

    public EntityEnderDragon(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getWidth() {
        return 13f;
    }

    @Override
    public float getHeight() {
        return 4f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(200);
        super.initEntity();
    }

    @Override
    protected boolean applyNameTag(@NotNull Player player, @NotNull Item item) {
        return false;
    }

    @Override
    public String getOriginalName() {
        return "Ender Dragon";
    }

    @Override
    public boolean isBoss() {
        return true;
    }
}