package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockEndGateway;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.projectile.EntityEnderPearl;
import org.powernukkitx.event.player.PlayerTeleportEvent.TeleportCause;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectEndGateway;
import org.powernukkitx.level.generator.object.ObjectEndIsland;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.random.NukkitRandom;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket;

/**
 * @author GoodLucky777
 */
public class BlockEntityEndGateway extends BlockEntitySpawnable {
    public BlockEntityEndGateway(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private int age;
    private BlockVector3 exitPortal;
    private static final BlockVector3 defaultExitPortal = new BlockVector3(0, 0, 0);
    private boolean needsExitPortalCalculation;
    public int teleportCooldown;

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();

        if (!this.nbt.containsInt("Age")
                || !this.nbt.containsList("ExitPortal", Tag.TAG_Int)
                || !this.nbt.containsByte("EndGatewayBadPosChecked")
                || this.nbt.getByte("EndGatewayBadPosChecked") != 1) {
            throw new IllegalStateException("Invalid EndGateway NBT at " + getFloorX() + "," + getFloorY() + "," + getFloorZ());
        }

        ListTag<IntTag> exitPortalList = this.getNbt().getList("ExitPortal", IntTag.class);
        if (exitPortalList.size() != 3) {
            throw new IllegalStateException("Invalid EndGateway ExitPortal at " + getFloorX() + "," + getFloorY() + "," + getFloorZ());
        }

        this.age = this.getNbt().getInt("Age");
        this.exitPortal = new BlockVector3(exitPortalList.get(0).getData(), exitPortalList.get(1).getData(), exitPortalList.get(2).getData());
        this.needsExitPortalCalculation = this.exitPortal.x == 0 && this.exitPortal.y == 0 && this.exitPortal.z == 0;
        this.teleportCooldown = 0;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == Block.END_GATEWAY;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        ListTag<IntTag> exitPortal = new ListTag<>();
        exitPortal.add(new IntTag(this.exitPortal.x))
                .add(new IntTag(this.exitPortal.y))
                .add(new IntTag(this.exitPortal.z));
        this.nbt.putInt("Age", this.age)
                .putList("ExitPortal", exitPortal)
                .putByte("EndGatewayBadPosChecked", (byte) 1);
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
        }

        if (isGenerated != isGenerating()) {
            setDirty();
            this.spawnToAll();
        }

        return true;
    }

    public void teleportEntity(Entity entity) {
        if (this.needsExitPortalCalculation && !createExitPortal()) {
            return;
        }

        setTeleportCooldown(100);

        var target = checkTeleport(getSafeExitPortal().asVector3().asBlockVector3()).add(0.5, 0, 0.5);
        boolean teleported;
        if (entity instanceof EntityEnderPearl enderPearl && enderPearl.shootingEntity != null) {
            teleported = enderPearl.shootingEntity.teleport(target, TeleportCause.END_GATEWAY);
            if (teleported) enderPearl.close();
        } else {
            teleported = entity.teleport(target, TeleportCause.END_GATEWAY);
        }

        if (teleported) {
            setTeleportCooldown(20);
        }
    }

    private boolean createExitPortal() {
        BlockVector3 farSeed = calculateExitPortal();

        if (farSeed.y == 0) {
            farSeed.setY(65);
            ensureAreaGenerated(farSeed, 16);

            BlockManager islandManager = new BlockManager(getLevel());
            NukkitRandom random = new NukkitRandom(getLevel().getSeed() ^ Level.chunkHash(farSeed.x >> 4, farSeed.z >> 4));
            new ObjectEndIsland().generate(islandManager, random, farSeed.asVector3());
            islandManager.applySubChunkUpdate();
        }

        BlockVector3 outerSurface = findHighestNaturalSurface(farSeed, 16);
        if (outerSurface == null) return false;

        BlockVector3 sourceGateway = new BlockVector3(getFloorX(), getFloorY(), getFloorZ());
        BlockVector3 outerGateway = outerSurface.up(10);
        BlockVector3 entryExit = findNearestNaturalSurface(outerGateway, 16, true);
        BlockVector3 outerExit = findNearestNaturalSurface(sourceGateway, 16, false);
        if (entryExit == null || outerExit == null) return false;

        BlockManager manager = new BlockManager(getLevel());
        new ObjectEndGateway().generate(manager, null, outerGateway.asVector3());
        manager.applySubChunkUpdate();

        Block outerBlock = getLevel().getBlock(outerGateway.x, outerGateway.y, outerGateway.z);
        if (!(outerBlock instanceof BlockEndGateway outer)) return false;

        BlockEntityEndGateway outerEntity = outer.getOrCreateBlockEntity();
        if (outerEntity == null) return false;

        this.setExitPortal(entryExit);
        this.saveNBT();
        this.setDirty();

        outerEntity.setAge(0);
        outerEntity.setExitPortal(outerExit);
        outerEntity.saveNBT();
        outerEntity.setDirty();
        return true;
    }

    private BlockVector3 calculateExitPortal() {
        float gatewayX = getFloorX();
        float gatewayZ = getFloorZ();
        float length = (float) Math.sqrt(gatewayX * gatewayX + gatewayZ * gatewayZ);
        if (length == 0) return defaultExitPortal.clone();

        float directionX = gatewayX / length;
        float directionZ = gatewayZ / length;
        float targetX = directionX * 1024f;
        float targetZ = directionZ * 1024f;
        float stepX = directionX * 16f;
        float stepZ = directionZ * 16f;
        int height = getGatewayTerrainHeight((int) Math.floor(targetX), (int) Math.floor(targetZ));

        if (height != 0) {
            for (int i = 0; i < 16 && height != 0; i++) {
                targetX -= stepX;
                targetZ -= stepZ;
                height = getGatewayTerrainHeight((int) Math.floor(targetX), (int) Math.floor(targetZ));
            }
        }

        if (height == 0) {
            for (int i = 0; i < 16 && height == 0; i++) {
                targetX += stepX;
                targetZ += stepZ;
                height = getGatewayTerrainHeight((int) Math.floor(targetX), (int) Math.floor(targetZ));
            }
        }

        return new BlockVector3((int) Math.floor(targetX), height, (int) Math.floor(targetZ));
    }

    private int getGatewayTerrainHeight(int blockX, int blockZ) {
        int chunkX = blockX >> 4;
        int chunkZ = blockZ >> 4;
        IChunk chunk = getLevel().getChunk(chunkX, chunkZ, false);

        if (chunk == null || chunk.getFinalizationState() != ChunkFinalizationState.DONE) {
            getLevel().syncGenerateChunkFully(chunkX, chunkZ);
            chunk = getLevel().getChunk(chunkX, chunkZ, false);
        }

        if (chunk == null) return 0;

        int height = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                height = Math.max(height, chunk.getHeightMap(x, z));
            }
        }
        return height > 0 ? height - 1 : 0;
    }

    private BlockVector3 findHighestNaturalSurface(BlockVector3 center, int radius) {
        ensureAreaGenerated(center, radius);
        BlockVector3 best = null;
        int bestY = Integer.MIN_VALUE;
        long bestDistance = Long.MAX_VALUE;

        for (int x = center.x - radius; x <= center.x + radius; x++) {
            for (int z = center.z - radius; z <= center.z + radius; z++) {
                int highest = getLevel().getHighestBlockAt(x, z);
                for (int y = highest; y >= getLevel().getMinHeight(); y--) {
                    if (!isNaturalSurface(x, y, z)) continue;

                    long dx = x - center.x;
                    long dz = z - center.z;
                    long distance = dx * dx + dz * dz;
                    if (y > bestY || y == bestY && distance < bestDistance) {
                        best = new BlockVector3(x, y, z);
                        bestY = y;
                        bestDistance = distance;
                    }
                    break;
                }
            }
        }
        return best;
    }

    private BlockVector3 findNearestNaturalSurface(BlockVector3 center, int radius, boolean landing) {
        ensureAreaGenerated(center, radius);
        BlockVector3 best = null;
        long bestDistance = Long.MAX_VALUE;

        for (int x = center.x - radius; x <= center.x + radius; x++) {
            for (int z = center.z - radius; z <= center.z + radius; z++) {
                int highest = getLevel().getHighestBlockAt(x, z);
                for (int y = highest; y >= getLevel().getMinHeight(); y--) {
                    if (!isNaturalSurface(x, y, z)) continue;

                    int candidateY = landing ? y + 1 : y;
                    long dx = x - center.x;
                    long dy = candidateY - center.y;
                    long dz = z - center.z;
                    long distance = dx * dx + dy * dy + dz * dz;
                    if (distance < bestDistance) {
                        best = new BlockVector3(x, candidateY, z);
                        bestDistance = distance;
                    }
                    break;
                }
            }
        }
        return best;
    }

    private boolean isNaturalSurface(int x, int y, int z) {
        Block block = getLevel().getBlock(x, y, z);
        return block.isSolid() && !(block instanceof BlockBedrock) && !(block instanceof BlockEndGateway) && getLevel().getBlock(x, y + 1, z).isAir();
    }

    private void ensureAreaGenerated(BlockVector3 center, int radius) {
        int minChunkX = (center.x - radius) >> 4;
        int maxChunkX = (center.x + radius) >> 4;
        int minChunkZ = (center.z - radius) >> 4;
        int maxChunkZ = (center.z + radius) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                IChunk chunk = getLevel().getChunk(chunkX, chunkZ, false);
                if (chunk == null || chunk.getFinalizationState() != ChunkFinalizationState.DONE) {
                    getLevel().syncGenerateChunkFully(chunkX, chunkZ);
                }
            }
        }
    }

    protected BlockVector3 checkTeleport(BlockVector3 vector3) {
        if (vector3.getY() <= 16 || vector3.getY() > 128) {
            // Place a little platform in case no safe spawn was found
            vector3.setY(65);
            for (int i = -2; i <= 2; i++) {
                for (int j = -1; j <= 1; j++) {
                    getLevel().setBlock(new Vector3(vector3.x + j, 64, vector3.z + j), Block.get(Block.END_STONE));
                    getLevel().setBlock(new Vector3(vector3.x + j, 64, vector3.z + i), Block.get(Block.END_STONE));
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
                if (chunk == null || chunk.getFinalizationState() != ChunkFinalizationState.DONE) {
                    getLevel().syncGenerateChunkFully(chunkX, chunkZ);
                }
            }
        }

        for (int x = exitPortal.getX() - 5; x <= exitPortal.getX() + 5; x++) {
            for (int z = exitPortal.getZ() - 5; z <= exitPortal.getZ() + 5; z++) {
                for (int y = 192; y > Math.max(0, exitPortal.getY() + 2); y--) {
                    Block block = getLevel().getBlock(x, y, z);
                    if (!block.getId().equals(Block.BEDROCK) && !block.getId().equals(Block.END_GATEWAY) && !block.canPassThrough()) {
                        return new BlockVector3(x, y + 1, z);
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
        this.exitPortal = exitPortal.clone();
        this.needsExitPortalCalculation = this.exitPortal.x == 0 && this.exitPortal.y == 0 && this.exitPortal.z == 0;
    }

    public boolean isGenerating() {
        return age < 200;
    }

    public boolean isTeleportCooldown() {
        return teleportCooldown > 0;
    }

    public void setTeleportCooldown(int teleportCooldown) {
        this.teleportCooldown = teleportCooldown;
        setDirty();
        sendBlockEventPacket(0);
        this.spawnToAll();
    }

    private void sendBlockEventPacket(int eventData) {
        if (this.closed) return;
        if (this.getLevel() == null) return;

        final BlockEventPacket pk = new BlockEventPacket();
        pk.setBlockPosition(Vector3i.from(this.getFloorX(), this.getFloorY(), this.getFloorZ()));
        pk.setEventType(1);
        pk.setEventValue(eventData);
        this.getLevel().addChunkPacket(this.getChunkX(), this.getChunkZ(), pk);
    }
}
