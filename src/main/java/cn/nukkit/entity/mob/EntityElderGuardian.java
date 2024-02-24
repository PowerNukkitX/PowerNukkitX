package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityElderGuardian extends EntityMob implements EntitySwimmable {

    @Override
    @NotNull
    public String getIdentifier() {
        return ELDER_GUARDIAN;
    }

    public EntityElderGuardian(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    @Override
    protected void initEntity() {
        this.setMaxHealth(80);
        super.initEntity();
        this.setDataFlag(EntityFlag.ELDER, true);
    }

    @Override
    public float getWidth() {
        return 1.99f;
    }

    @Override
    public float getHeight() {
        return 1.99f;
    }

    @Override
    public String getOriginalName() {
        return "Elder Guardian";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
