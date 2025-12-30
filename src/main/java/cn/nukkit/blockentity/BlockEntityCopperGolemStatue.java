package cn.nukkit.blockentity;

import cn.nukkit.block.copper.golem.AbstractBlockCopperGolemStatue;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Buddelbubi
 * @see <a href="https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/translator/level/block/entity/CopperBlockEntityTranslator.java#L35">NBT Info</a>
 */
public class BlockEntityCopperGolemStatue extends BlockEntitySpawnable {

    public BlockEntityCopperGolemStatue(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if(this.namedTag.containsInt("Pose")) {
            this.setPose(CopperPose.STANDING);
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock() instanceof AbstractBlockCopperGolemStatue;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putBoolean("isMovable", false)
                .putInt("Pose", this.namedTag.getInt("Pose")
                );
    }

    public void setPose(CopperPose pose) {
        this.namedTag.putInt("Pose", pose.ordinal());
    }

    public CopperPose getPose() {
        return CopperPose.values()[this.namedTag.getInt("Pose")];
    }

    public enum CopperPose {
        STANDING,
        SITTING,
        RUNNING,
        STAR
    }

}
