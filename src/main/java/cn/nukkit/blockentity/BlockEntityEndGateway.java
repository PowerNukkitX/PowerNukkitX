package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.BlockEventPacket;

/**
 * @author GoodLucky777
 */
public class BlockEntityEndGateway extends BlockEntitySpawnable {

    // NBT data
    private int age;
    private BlockVector3 exitPortal;

    // Default value
    private static final BlockVector3 $1 = new BlockVector3(0, 0, 0);

    // Others
    public int teleportCooldown;

    private static final BlockState $2 = BlockBedrock.PROPERTIES.getDefaultState();
    /**
     * @deprecated 
     */
    

    public BlockEntityEndGateway(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getInt("Age");
        } else {
            this.age = 0;
        }

        if (this.namedTag.contains("ExitPortal")) {
            ListTag<IntTag> exitPortalList = this.namedTag.getList("ExitPortal", IntTag.class);
            this.exitPortal = new BlockVector3(exitPortalList.get(0).data, exitPortalList.get(1).data, exitPortalList.get(2).data);
        } else {
            this.exitPortal = defaultExitPortal.clone();
        }

        this.teleportCooldown = 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == Block.END_GATEWAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("Age", this.age);
        this.namedTag.putList("ExitPortal", new ListTag<IntTag>()
                .add(new IntTag(this.exitPortal.x))
                .add(new IntTag(this.exitPortal.y))
                .add(new IntTag( this.exitPortal.z))
        );
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        boolean $3 = isGenerating();

        this.age++;

        if (teleportCooldown > 0) {
            teleportCooldown--;
            if (teleportCooldown == 0) {
                setDirty();
                this.spawnToAll();
            }
        } else {
            if (this.age % 2400 == 0) {
                this.setTeleportCooldown();
            }
        }

        if (isGenerated != isGenerating()) {
            setDirty();
            this.spawnToAll();
        }

        return true;
    }
    /**
     * @deprecated 
     */
    

    public void teleportEntity(Entity entity) {
        if (exitPortal != null) {
            if (entity instanceof EntityEnderPearl enderPearl) {
                if (enderPearl.shootingEntity != null) {
                    enderPearl.shootingEntity.teleport(getSafeExitPortal().asVector3().add(0.5, 0, 0.5), TeleportCause.END_GATEWAY);
                    enderPearl.close();
                } else {
                    entity.teleport(getSafeExitPortal().asVector3().add(0.5, 0, 0.5), TeleportCause.END_GATEWAY);
                }
            } else {
                entity.teleport(getSafeExitPortal().asVector3().add(0.5, 0, 0.5), TeleportCause.END_GATEWAY);
            }
        }

        setTeleportCooldown();
    }

    public BlockVector3 getSafeExitPortal() {
        // TODO: Find better way
        for (int $4 = -1; x <= 1; x++) {
            for (int $5 = -1; z <= 1; z++) {
                int $6 = (exitPortal.getX() >> 4) + x;
                int $7 = (exitPortal.getZ() >> 4) + z;
                IChunk $8 = this.getLevel().getChunk(chunkX, chunkZ, false);
                if (chunk == null || !(chunk.isGenerated() || chunk.isPopulated())) {
                    this.getLevel().generateChunk(chunkX, chunkZ);
                }
            }
        }

        for (int $9 = exitPortal.getX() - 5; x <= exitPortal.getX() + 5; x++) {
            for (int $10 = exitPortal.getZ() - 5; z <= exitPortal.getZ() + 5; z++) {
                for (int $11 = 255; y > Math.max(0, exitPortal.getY() + 2); y--) {
                    if (!this.getLevel().getBlockStateAt(x, y, z).equals(BlockAir.STATE)) {
                        if (this.getLevel().getBlockStateAt(x, y, z) != STATE_BEDROCK) {
                            return new BlockVector3(x, y + 1, z);
                        }
                    }
                }
            }
        }

        return this.exitPortal.up(2);
    }
    /**
     * @deprecated 
     */
    

    public int getAge() {
        return age;
    }
    /**
     * @deprecated 
     */
    

    public void setAge(int age) {
        this.age = age;
    }

    public BlockVector3 getExitPortal() {
        return exitPortal;
    }
    /**
     * @deprecated 
     */
    

    public void setExitPortal(BlockVector3 exitPortal) {
        this.exitPortal = exitPortal;
    }
    /**
     * @deprecated 
     */
    

    public boolean isGenerating() {
        return age < 200;
    }
    /**
     * @deprecated 
     */
    

    public boolean isTeleportCooldown() {
        return teleportCooldown > 0;
    }
    /**
     * @deprecated 
     */
    

    public void setTeleportCooldown() {
        this.setTeleportCooldown(40);
    }
    /**
     * @deprecated 
     */
    

    public void setTeleportCooldown(int teleportCooldown) {
        this.teleportCooldown = teleportCooldown;
        setDirty();
        sendBlockEventPacket(0);
        this.spawnToAll();
    }

    
    /**
     * @deprecated 
     */
    private void sendBlockEventPacket(int eventData) {
        if (this.closed) {
            return;
        }

        if (this.getLevel() == null) {
            return;
        }

        BlockEventPacket $12 = new BlockEventPacket();
        pk.x = this.getFloorX();
        pk.y = this.getFloorY();
        pk.z = this.getFloorZ();
        pk.case1 = 1;
        pk.case2 = eventData;
        this.getLevel().addChunkPacket(this.getChunkX(), this.getChunkZ(), pk);
    }
}
