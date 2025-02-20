package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
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
    private static final BlockVector3 defaultExitPortal = new BlockVector3(0, 0, 0);

    // Others
    public int teleportCooldown;

    private static final BlockState STATE_BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();

    public BlockEntityEndGateway(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
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
            if(this.toHorizontal().distance(Vector2.ZERO) < 100) {
                shift:
                for(int shift : new int[] {0, -5, 5, -10, 10}) { //Reduces the probability of a no hit
                    for(int i = 0; i < 16; i++) {
                        if(exitPortal.getY() <= 16 || exitPortal.getY() > 128) {
                            this.exitPortal = new Vector3(this.x+shift, 0, this.z+shift).normalize().multiply(0x500 + (i*0xF)).asBlockVector3();
                            this.exitPortal = getSafeExitPortal();
                        } else break shift;
                    }
                }
            }
        }

        this.teleportCooldown = 0;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == Block.END_GATEWAY;
    }

    @Override
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
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        boolean isGenerated = isGenerating();

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

    public void teleportEntity(Entity entity) {
        if (exitPortal != null) {
            if (entity instanceof EntityEnderPearl enderPearl) {
                if (enderPearl.shootingEntity != null) {
                    enderPearl.shootingEntity.teleport(checkTeleport(getSafeExitPortal().asVector3().asBlockVector3()).add(0.5, 0, 0.5), TeleportCause.END_GATEWAY);
                    enderPearl.close();
                } else {
                    entity.teleport(checkTeleport(getSafeExitPortal().asVector3().asBlockVector3()).add(0.5, 0, 0.5), TeleportCause.END_GATEWAY);
                }
            } else {
                entity.teleport(checkTeleport(getSafeExitPortal().asVector3().asBlockVector3()).add(0.5, 0, 0.5), TeleportCause.END_GATEWAY);
            }
        }
        setTeleportCooldown();
    }

    protected BlockVector3 checkTeleport(BlockVector3 vector3) {
        if(vector3.getY() <= 16 || vector3.getY() > 128) {
            // Place a little platform in case no safe spawn was found
            vector3.setY(65);
            for(int i = -2; i <= 2; i++) {
                for(int j = -1; j <= 1; j++) {
                    getLevel().setBlock(new Vector3(vector3.x+j, 64, vector3.z+j), Block.get(Block.END_STONE));
                    getLevel().setBlock(new Vector3(vector3.x+j, 64, vector3.z+i), Block.get(Block.END_STONE));
                }
            }
        }
        return vector3;
    }


    public BlockVector3 getSafeExitPortal() {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                int chunkX = (exitPortal.getX() >> 4) + x;
                int chunkZ = (exitPortal.getZ() >> 4) + z;
                IChunk chunk = getLevel().getChunk(chunkX, chunkZ, false);
                if (chunk == null || !(chunk.isGenerated() || chunk.isPopulated())) {
                    getLevel().syncGenerateChunk(chunkX, chunkZ);
                }
            }
        }

        for (int x = exitPortal.getX() - 5; x <= exitPortal.getX() + 5; x++) {
            for (int z = exitPortal.getZ() - 5; z <= exitPortal.getZ() + 5; z++) {
                for (int y = 192; y > Math.max(0, exitPortal.getY() + 2); y--) {
                    if (!getLevel().getBlockStateAt(x, y, z).equals(BlockAir.STATE)) {
                        if (getLevel().getBlockStateAt(x, y, z) != STATE_BEDROCK) {
                            return new BlockVector3(x, y + 1, z);
                        }
                    }
                }
            }
        }

        return exitPortal.up(2);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BlockVector3 getExitPortal() {
        return exitPortal;
    }

    public void setExitPortal(BlockVector3 exitPortal) {
        this.exitPortal = exitPortal;
    }

    public boolean isGenerating() {
        return age < 200;
    }

    public boolean isTeleportCooldown() {
        return teleportCooldown > 0;
    }

    public void setTeleportCooldown() {
        this.setTeleportCooldown(40);
    }

    public void setTeleportCooldown(int teleportCooldown) {
        this.teleportCooldown = teleportCooldown;
        setDirty();
        sendBlockEventPacket(0);
        this.spawnToAll();
    }

    private void sendBlockEventPacket(int eventData) {
        if (this.closed) {
            return;
        }

        if (this.getLevel() == null) {
            return;
        }

        BlockEventPacket pk = new BlockEventPacket();
        pk.x = this.getFloorX();
        pk.y = this.getFloorY();
        pk.z = this.getFloorZ();
        pk.type = 1;
        pk.value = eventData;
        this.getLevel().addChunkPacket(this.getChunkX(), this.getChunkZ(), pk);
    }
}
