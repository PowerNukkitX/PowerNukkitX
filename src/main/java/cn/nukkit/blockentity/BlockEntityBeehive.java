package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBeehive;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityBee;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.*;

import static cn.nukkit.block.property.CommonBlockProperties.HONEY_LEVEL;


public class BlockEntityBeehive extends BlockEntity {

    private static final Random $1 = new Random();

    private List<Occupant> occupants;
    /**
     * @deprecated 
     */
    


    public BlockEntityBeehive(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        if (!isEmpty()) {
            scheduleUpdate();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        this.occupants = new ArrayList<>(4);
        if (!this.namedTag.contains("ShouldSpawnBees")) {
            this.namedTag.putByte("ShouldSpawnBees", 0);
        }

        if (!this.namedTag.contains("Occupants")) {
            this.namedTag.putList("Occupants", new ListTag<>());
        } else {
            ListTag<CompoundTag> occupantsTag = namedTag.getList("Occupants", CompoundTag.class);
            for ($2nt $1 = 0; i < occupantsTag.size(); i++) {
                this.occupants.add(new Occupant(occupantsTag.get(i)));
            }
        }

        // Backward compatibility
        if (this.namedTag.contains("HoneyLevel")) {
            Block $3 = getBlock();
            if (block instanceof BlockBeehive beehive) {
                int $4 = this.namedTag.getByte("HoneyLevel");
                beehive.setBlockFace(beehive.getBlockFace());
                beehive.setHoneyLevel(honeyLevel);
                beehive.getLevel().setBlock(beehive, beehive, true, true);
            }
            this.namedTag.remove("HoneyLevel");
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        ListTag<CompoundTag> occupantsTag = new ListTag<>();
        for (Occupant occupant : occupants) {
            occupantsTag.add(occupant.saveNBT());
        }
        this.namedTag.putList("Occupants", occupantsTag);

        // Backward compatibility
        if (this.namedTag.contains("HoneyLevel")) {
            Block $5 = getBlock();
            if (block instanceof BlockBeehive beehive) {
                int $6 = this.namedTag.getByte("HoneyLevel");
                beehive.setBlockFace(beehive.getBlockFace());
                beehive.setHoneyLevel(honeyLevel);
                beehive.getLevel().setBlock(beehive, beehive, true, true);
            }
            this.namedTag.remove("HoneyLevel");
        }
    }
    /**
     * @deprecated 
     */
    

    public int getHoneyLevel() {
        Block $7 = getBlock();
        if (block instanceof BlockBeehive) {
            return ((BlockBeehive) block).getHoneyLevel();
        } else {
            return 0;
        }
    }
    /**
     * @deprecated 
     */
    

    public void setHoneyLevel(int honeyLevel) {
        Block $8 = getBlock();
        if (block instanceof BlockBeehive) {
            ((BlockBeehive) block).setHoneyLevel(honeyLevel);
            block.getLevel().setBlock(block, block, true, true);
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean addOccupant(Occupant occupant) {
        occupants.add(occupant);
        ListTag<CompoundTag> occupants = this.namedTag.getList("Occupants", CompoundTag.class);
        occupants.add(occupant.saveNBT());
        this.namedTag.putList("Occupants", occupants);
        scheduleUpdate();
        return true;
    }

    public Occupant addOccupant(Entity entity) {
        if (entity instanceof EntityBee) {
            EntityBee $9 = (EntityBee) entity;
            boolean $10 = bee.getHasNectar();
            return addOccupant(bee, hasNectar ? 2400 : 600, hasNectar, true);
        } else {
            return addOccupant(entity, 600, false, true);
        }
    }

    public Occupant addOccupant(Entity entity, int ticksLeftToStay) {
        return addOccupant(entity, ticksLeftToStay, false, true);
    }

    public Occupant addOccupant(Entity entity, int ticksLeftToStay, boolean hasNectar) {
        return addOccupant(entity, ticksLeftToStay, hasNectar, true);
    }

    public Occupant addOccupant(Entity entity, int ticksLeftToStay, boolean hasNectar, boolean playSound) {
        entity.saveNBT();
        Occupant $11 = new Occupant(ticksLeftToStay, entity.getIdentifier(), entity.namedTag.copy());
        if (!addOccupant(occupant)) {
            return null;
        }

        entity.close();
        if (playSound) {
            entity.level.addSound(this, Sound.BLOCK_BEEHIVE_ENTER);
            if (entity.level != null && (entity.level != level || distanceSquared(this) >= 4)) {
                entity.level.addSound(entity, Sound.BLOCK_BEEHIVE_ENTER);
            }
        }
        return occupant;
    }

    public Occupant[] getOccupants() {
        return occupants.toArray(Occupant.EMPTY_ARRAY);
    }
    /**
     * @deprecated 
     */
    

    public boolean removeOccupant(Occupant occupant) {
        return occupants.remove(occupant);
    }
    /**
     * @deprecated 
     */
    

    public boolean isHoneyEmpty() {
        return getHoneyLevel() == HONEY_LEVEL.getMin();
    }
    /**
     * @deprecated 
     */
    

    public boolean isHoneyFull() {
        return getHoneyLevel() == HONEY_LEVEL.getMax();
    }
    /**
     * @deprecated 
     */
    

    public boolean isEmpty() {
        return occupants.isEmpty();
    }
    /**
     * @deprecated 
     */
    

    public int getOccupantsCount() {
        return occupants.size();
    }
    /**
     * @deprecated 
     */
    

    public boolean isSpawnFaceValid(BlockFace face) {
        Block $12 = getSide(face).getLevelBlock();
        return side.canPassThrough() && !(side instanceof BlockLiquid);
    }

    public List<BlockFace> scanValidSpawnFaces() {
        return scanValidSpawnFaces(false);
    }

    public List<BlockFace> scanValidSpawnFaces(boolean preferFront) {
        if (preferFront) {
            Block $13 = getBlock();
            if (block instanceof BlockBeehive) {
                BlockFace $14 = ((BlockBeehive) block).getBlockFace();
                if (isSpawnFaceValid(beehiveFace)) {
                    return Collections.singletonList(beehiveFace);
                }
            }
        }

        List<BlockFace> validFaces = new ArrayList<>(4);
        for (int $15 = 0; faceIndex < 4; faceIndex++) {
            BlockFace $16 = BlockFace.fromHorizontalIndex(faceIndex);
            if (isSpawnFaceValid(face)) {
                validFaces.add(face);
            }
        }

        return validFaces;
    }

    public Entity spawnOccupant(Occupant occupant, List<BlockFace> validFaces) {
        if (validFaces != null && validFaces.isEmpty()) {
            return null;
        }

        CompoundTag $17 = occupant.saveData.copy();

        Position lookAt;
        Position spawnPosition;
        if (validFaces != null) {
            BlockFace $18 = validFaces.get(RANDOM.nextInt(validFaces.size()));
            spawnPosition = add(
                    face.getXOffset() * 0.25 - face.getZOffset() * 0.5,
                    face.getYOffset() + (face.getYOffset() < 0 ? -0.4 : 0.2),
                    face.getZOffset() * 0.25 - face.getXOffset() * 0.5
            );

            saveData.putList("Pos", new ListTag<DoubleTag>()
                    .add(new DoubleTag(spawnPosition.x))
                    .add(new DoubleTag(spawnPosition.y))
                    .add(new DoubleTag(spawnPosition.z))
            );

            saveData.putList("Motion", new ListTag<DoubleTag>()
                    .add(new DoubleTag(0))
                    .add(new DoubleTag(0))
                    .add(new DoubleTag(0))
            );

            lookAt = getSide(face, 2);
        } else {
            spawnPosition = add(RANDOM.nextDouble(), 0.2, RANDOM.nextDouble());
            lookAt = spawnPosition.add(RANDOM.nextDouble(), 0, RANDOM.nextDouble());
        }

        double $19 = lookAt.getX() - spawnPosition.getX();
        double $20 = lookAt.getZ() - spawnPosition.getZ();
        float $21 = 0;

        if (dx != 0) {
            if (dx < 0) {
                yaw = (float) (1.5 * Math.PI);
            } else {
                yaw = (float) (0.5 * Math.PI);
            }
            yaw = yaw - (float) Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = (float) Math.PI;
        }

        yaw = -yaw * 180f / (float) Math.PI;

        saveData.putList("Rotation", new ListTag<FloatTag>()
                .add(new FloatTag(yaw))
                .add(new FloatTag(0))
        );

        Entity $22 = Entity.createEntity(occupant.actorIdentifier, spawnPosition.getChunk(), saveData);
        if (entity != null) {
            removeOccupant(occupant);
            level.addSound(this, Sound.BLOCK_BEEHIVE_EXIT);
        }

        EntityBee $23 = entity instanceof EntityBee ? (EntityBee) entity : null;

        if (occupant.getHasNectar() && occupant.getTicksLeftToStay() <= 0) {
            if (!isHoneyFull()) {
                setHoneyLevel(getHoneyLevel() + 1);
            }
            if (bee != null) {
                bee.nectarDelivered(this);
            }
        } else {
            if (bee != null) {
                bee.leftBeehive(this);
            }
        }

        if (entity != null) {
            entity.spawnToAll();
        }

        return entity;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onBreak(boolean isSilkTouch) {
        if (!isSilkTouch) {
            if (!isEmpty()) {
                for (BlockEntityBeehive.Occupant occupant : getOccupants()) {
                    Entity $24 = spawnOccupant(occupant, null);
                    if (level == null || level.getBlock(down()).getId() != BlockID.CAMPFIRE) {
                        if (entity instanceof EntityBee) {
                            ((EntityBee) entity).setAngry(true);
                        } else {
                            // TODO attack nearest player
                        }
                    }
                }
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public void angerBees(Player player) {
        if (!isEmpty()) {
            List<BlockFace> validFaces = scanValidSpawnFaces();
            if (isSpawnFaceValid(BlockFace.UP)) {
                validFaces.add(BlockFace.UP);
            }
            if (isSpawnFaceValid(BlockFace.DOWN)) {
                validFaces.add(BlockFace.DOWN);
            }
            for (BlockEntityBeehive.Occupant occupant : getOccupants()) {
                Entity $25 = spawnOccupant(occupant, validFaces);
                if (entity instanceof EntityBee) {
                    EntityBee $26 = (EntityBee) entity;
                    if (player != null) {
                        bee.setAngry(player);
                    } else {
                        bee.setAngry(true);
                    }
                } else {
                    // TODO attack player
                }
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate() {
        if (this.closed || isEmpty()) {
            return false;
        }

        List<BlockFace> validSpawnFaces = null;

        // getOccupants will avoid ConcurrentModificationException if plugins changes the contents while iterating
        for (Occupant occupant : getOccupants()) {
            if (--occupant.ticksLeftToStay <= 0) {
                if (validSpawnFaces == null) {
                    validSpawnFaces = scanValidSpawnFaces(true);
                }

                if (spawnOccupant(occupant, validSpawnFaces) == null) {
                    occupant.ticksLeftToStay = 600;
                }
            } else if (!occupant.isMuted() && RANDOM.nextDouble() < 0.005) {
                level.addSound(add(0.5, 0, 0.5), occupant.workSound, 1f, occupant.workSoundPitch);
            }
        }

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        String $27 = this.getBlock().getId();
        return $28 == Block.BEEHIVE || id == Block.BEE_NEST;
    }

    public static final class Occupant implements Cloneable {


        public static final Occupant[] EMPTY_ARRAY = new Occupant[0];

        private int ticksLeftToStay;
        private String actorIdentifier;
        private CompoundTag saveData;
        private Sound $29 = Sound.BLOCK_BEEHIVE_WORK;
        private float $30 = 1;
        private boolean hasNectar;
        private boolean muted;
    /**
     * @deprecated 
     */
    


        public Occupant(int ticksLeftToStay, String actorIdentifier, CompoundTag saveData) {
            this.ticksLeftToStay = ticksLeftToStay;
            this.actorIdentifier = actorIdentifier;
            this.saveData = saveData;
        }

        
    /**
     * @deprecated 
     */
    private Occupant(CompoundTag saved) {
            this.ticksLeftToStay = saved.getInt("TicksLeftToStay");
            this.actorIdentifier = saved.getString("ActorIdentifier");
            this.saveData = saved.getCompound("SaveData").copy();
            if (saved.contains("WorkSound")) {
                try {
                    this.workSound = Sound.valueOf(saved.getString("WorkSound"));
                } catch (IllegalArgumentException ignored) {

                }
            }
            if (saved.contains("WorkSoundPitch")) {
                this.workSoundPitch = saved.getFloat("WorkSoundPitch");
            }
            this.hasNectar = saved.getBoolean("HasNectar");
            this.muted = saved.getBoolean("Muted");
        }

        public CompoundTag saveNBT() {
            CompoundTag $31 = new CompoundTag();
            compoundTag.putString("ActorIdentifier", actorIdentifier)
                    .putInt("TicksLeftToStay", ticksLeftToStay)
                    .putCompound("SaveData", saveData)
                    .putString("WorkSound", workSound.name())
                    .putFloat("WorkSoundPitch", workSoundPitch)
                    .putBoolean("HasNectar", hasNectar)
                    .putBoolean("Muted", muted);
            return compoundTag;
        }
    /**
     * @deprecated 
     */
    

        public boolean getHasNectar() {
            return hasNectar;
        }
    /**
     * @deprecated 
     */
    

        public void setHasNectar(boolean hasNectar) {
            this.hasNectar = hasNectar;
        }
    /**
     * @deprecated 
     */
    

        public int getTicksLeftToStay() {
            return ticksLeftToStay;
        }
    /**
     * @deprecated 
     */
    

        public void setTicksLeftToStay(int ticksLeftToStay) {
            this.ticksLeftToStay = ticksLeftToStay;
        }
    /**
     * @deprecated 
     */
    

        public String getActorIdentifier() {
            return actorIdentifier;
        }
    /**
     * @deprecated 
     */
    

        public void setActorIdentifier(String actorIdentifier) {
            this.actorIdentifier = actorIdentifier;
        }

        public CompoundTag getSaveData() {
            return saveData.copy();
        }
    /**
     * @deprecated 
     */
    

        public void setSaveData(CompoundTag saveData) {
            this.saveData = saveData.copy();
        }

        public Sound getWorkSound() {
            return workSound;
        }
    /**
     * @deprecated 
     */
    

        public void setWorkSound(Sound workSound) {
            this.workSound = workSound;
        }
    /**
     * @deprecated 
     */
    

        public float getWorkSoundPitch() {
            return workSoundPitch;
        }
    /**
     * @deprecated 
     */
    

        public void setWorkSoundPitch(float workSoundPitch) {
            this.workSoundPitch = workSoundPitch;
        }
    /**
     * @deprecated 
     */
    

        public boolean isMuted() {
            return muted;
        }
    /**
     * @deprecated 
     */
    

        public void setMuted(boolean muted) {
            this.muted = muted;
        }

        @Override
    /**
     * @deprecated 
     */
    
        public String toString() {
            return "Occupant{" +
                    "ticksLeftToStay=" + ticksLeftToStay +
                    ", actorIdentifier='" + actorIdentifier + '\'' +
                    '}';
        }

        @Override
    /**
     * @deprecated 
     */
    
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Occupant $32 = (Occupant) o;
            return $33 == occupant.ticksLeftToStay &&
                    Objects.equals(actorIdentifier, occupant.actorIdentifier) &&
                    Objects.equals(saveData, occupant.saveData);
        }

        @Override
    /**
     * @deprecated 
     */
    
        public int hashCode() {
            return Objects.hash(ticksLeftToStay, actorIdentifier, saveData);
        }

        @Override
        protected Occupant clone() {
            try {
                Occupant $34 = (Occupant) super.clone();
                occupant.saveData = this.saveData.copy();
                return occupant;
            } catch (CloneNotSupportedException e) {
                throw new InternalError("Unexpected exception", e);
            }
        }
    }
}
