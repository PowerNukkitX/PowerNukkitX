package org.powernukkitx.blockentity;

import org.powernukkitx.block.copper.golem.AbstractBlockCopperGolemStatue;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author Buddelbubi
 * @see <a href="https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/translator/level/block/entity/CopperBlockEntityTranslator.java#L35">NBT Info</a>
 */
public class BlockEntityCopperGolemStatue extends BlockEntitySpawnable {
    public BlockEntityCopperGolemStatue(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public static final String TAG_ACTOR = "Actor";
    public static final String TAG_ACTOR_IDENTIFIER = "ActorIdentifier";
    public static final String TAG_SAVE_DATA = "SaveData";
    public static final String TAG_POSE = "Pose";
    public static final String COPPER_GOLEM_ACTOR_IDENTIFIER = "minecraft:copper_golem<>";

    @Override
    public void loadNBT() {
        super.loadNBT();

        int pose = this.nbt.containsNumber(TAG_POSE) ? this.nbt.getInt(TAG_POSE) : 0;
        this.nbt.putInt(TAG_POSE, Math.max(0, Math.min(3, pose)));

        CompoundTag actor;

        if (this.nbt.get(TAG_ACTOR) instanceof CompoundTag existingActor) {
            actor = existingActor;
        } else {
            actor = new CompoundTag();
            this.nbt.putCompound(TAG_ACTOR, actor);
        }

        actor.putString(TAG_ACTOR_IDENTIFIER, COPPER_GOLEM_ACTOR_IDENTIFIER);

        if (!(actor.get(TAG_SAVE_DATA) instanceof CompoundTag)) {
            actor.putCompound(TAG_SAVE_DATA, new CompoundTag());
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        int pose = this.nbt.getInt(TAG_POSE);
        this.nbt.putInt(TAG_POSE, Math.max(0, Math.min(3, pose)));

        CompoundTag actor;

        if (this.nbt.get(TAG_ACTOR) instanceof CompoundTag existingActor) {
            actor = existingActor;
        } else {
            actor = new CompoundTag();
            this.nbt.putCompound(TAG_ACTOR, actor);
        }

        actor.putString(TAG_ACTOR_IDENTIFIER, COPPER_GOLEM_ACTOR_IDENTIFIER);

        if (!(actor.get(TAG_SAVE_DATA) instanceof CompoundTag)) {
            actor.putCompound(TAG_SAVE_DATA, new CompoundTag());
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock() instanceof AbstractBlockCopperGolemStatue;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound().putInt(TAG_POSE, this.getPose().ordinal());
    }

    /**
     * Replaces the persisted copper golem actor save data.
     *
     * @param saveData actor save data, or {@code null} for an empty compound
     */
    public void setActorSaveData(CompoundTag saveData) {
        CompoundTag actor = new CompoundTag()
                .putString(TAG_ACTOR_IDENTIFIER, COPPER_GOLEM_ACTOR_IDENTIFIER)
                .putCompound(TAG_SAVE_DATA, saveData != null ? saveData.copy() : new CompoundTag());

        this.nbt.putCompound(TAG_ACTOR, actor);

        setDirty();
    }

    /**
     * Returns a copy of the persisted copper golem actor save data.
     *
     * @return actor save data, or an empty compound when absent
     */
    public CompoundTag getActorSaveData() {
        if (!(this.nbt.get(TAG_ACTOR) instanceof CompoundTag actor)) {
            return new CompoundTag();
        }

        if (!(actor.get(TAG_SAVE_DATA) instanceof CompoundTag saveData)) {
            return new CompoundTag();
        }

        return saveData.copy();
    }

    public void setPose(CopperPose pose) {
        if (pose == null) {
            pose = CopperPose.STANDING;
        }

        this.nbt.putInt(TAG_POSE, pose.ordinal());

        setDirty();
    }

    public CopperPose getPose() {
        int pose = Math.max(0, Math.min(3, this.nbt.getInt(TAG_POSE)));
        return CopperPose.values()[pose];
    }

    public enum CopperPose {
        STANDING,
        SITTING,
        RUNNING,
        STAR
    }
}
