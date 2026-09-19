package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBeehive;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.passive.EntityBee;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static org.powernukkitx.block.property.CommonBlockProperties.HONEY_LEVEL;


@Slf4j
public class BlockEntityBeehive extends BlockEntity {

    private static final Random RANDOM = new Random();

    private List<Occupant> occupants;

    @Getter
    @Setter
    private Entity interactingEntity;

    public BlockEntityBeehive(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        if (!isEmpty()) {
            scheduleUpdate();
        }
    }

    @Override
    public void loadNBT() {
        super.loadNBT();

        this.occupants = new ArrayList<>(4);

        if (!this.nbt.contains("ShouldSpawnBees")) {
            this.nbt.putByte("ShouldSpawnBees", 0);
        }

        if (this.nbt.containsList("Occupants")) {
            ListTag<CompoundTag> occupantsTag = this.nbt.getList("Occupants", CompoundTag.class);

            for (CompoundTag occupantTag : occupantsTag.getAll()) {
                this.occupants.add(new Occupant(occupantTag));
            }
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (occupants.isEmpty()) {
            this.nbt.remove("Occupants");
            return;
        }

        ListTag<CompoundTag> occupantsTag = new ListTag<>();

        for (Occupant occupant : occupants) {
            occupantsTag.add(occupant.saveNBT());
        }

        this.nbt.putList("Occupants", occupantsTag);
    }

    public int getHoneyLevel() {
        Block block = getBlock();
        if (block instanceof BlockBeehive hive) {
            return hive.getHoneyLevel();
        } else {
            return 0;
        }
    }

    public void setHoneyLevel(int honeyLevel) {
        Block block = getBlock();
        if (block instanceof BlockBeehive hive) {
            hive.setHoneyLevel(honeyLevel);
            block.getLevel().setBlock(block, block, true, true);
        }
    }

    public boolean addOccupant(Occupant occupant) {
        occupants.add(occupant);
        ListTag<CompoundTag> occupants = this.nbt.getList("Occupants", CompoundTag.class);
        occupants.add(occupant.saveNBT());
        this.nbt.putList("Occupants", occupants);
        scheduleUpdate();
        return true;
    }

    public Occupant addOccupant(Entity entity) {
        if (entity instanceof EntityBee) {
            EntityBee bee = (EntityBee) entity;
            boolean hasNectar = bee.hasNectar();
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
        if (entity instanceof EntityBee bee) {
            bee.setNectar(hasNectar);
        }

        entity.saveNBT();

        CompoundTag saveData = entity.getNbt().copy();
        Occupant occupant = new Occupant(ticksLeftToStay, toStorageActorIdentifier(entity.getIdentifier()), saveData);

        if (!addOccupant(occupant)) return null;

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

    public boolean removeOccupant(Occupant occupant) {
        return occupants.remove(occupant);
    }

    public boolean isHoneyEmpty() {
        return getHoneyLevel() == HONEY_LEVEL.getMin();
    }

    public boolean isHoneyFull() {
        return getHoneyLevel() == HONEY_LEVEL.getMax();
    }

    public boolean isEmpty() {
        return occupants.isEmpty();
    }

    public int getOccupantsCount() {
        return occupants.size();
    }

    public boolean isSpawnFaceValid(BlockFace face) {
        Block side = getSide(face).getLevelBlock();
        return side.canPassThrough() && !(side instanceof BlockLiquid);
    }

    public List<BlockFace> scanValidSpawnFaces() {
        return scanValidSpawnFaces(false);
    }

    public List<BlockFace> scanValidSpawnFaces(boolean preferFront) {
        if (preferFront) {
            Block block = getBlock();
            if (block instanceof BlockBeehive) {
                BlockFace beehiveFace = ((BlockBeehive) block).getBlockFace();
                if (isSpawnFaceValid(beehiveFace)) {
                    return Collections.singletonList(beehiveFace);
                }
            }
        }

        List<BlockFace> validFaces = new ArrayList<>(4);
        for (int faceIndex = 0; faceIndex < 4; faceIndex++) {
            BlockFace face = BlockFace.fromHorizontalIndex(faceIndex);
            if (isSpawnFaceValid(face)) {
                validFaces.add(face);
            }
        }

        return validFaces;
    }

    public Entity spawnOccupant(Occupant occupant) {
        return this.spawnOccupant(occupant, Collections.singletonList(BlockFace.UP));
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        if(!isSilkTouch) {
            Entity interactingEntity = getInteractingEntity();
            for(Occupant occupant : getOccupants()) {
                if(spawnOccupant(occupant) instanceof EntityBee bee && interactingEntity != null) {
                    if(getInteractingEntity() instanceof Player player) {
                        if(player.isSurvival() || player.isAdventure()) {
                            bee.setAngryOnTarget(player);
                        }
                    } else bee.setAngryOnTarget(interactingEntity);
                }
            }
        }
        super.onBreak(isSilkTouch);
    }

    public Entity spawnOccupant(Occupant occupant, List<BlockFace> validFaces) {
        if (validFaces != null && validFaces.isEmpty()) {
            return null;
        }
        String runtimeIdentifier =
                toRuntimeActorIdentifier(
                        occupant.getActorIdentifier()
                );

        if (!Identifier.isValid(runtimeIdentifier)) {
            log.warn("Invalid beehive occupant identifier: {}", occupant.getActorIdentifier());
            runtimeIdentifier = "minecraft:bee";
            occupant.setActorIdentifier("minecraft:bee<>");
        }

        CompoundTag saveData =
                occupant.saveData.copy();

        Position lookAt;
        Position spawnPosition;
        if (validFaces != null) {
            BlockFace face = validFaces.get(RANDOM.nextInt(validFaces.size()));
            spawnPosition = add(
                    face.getXOffset() * 0.25 - face.getZOffset() * 0.5,
                    face.getYOffset() + (face.getYOffset() < 0 ? -0.4 : 0.2),
                    face.getZOffset() * 0.25 - face.getXOffset() * 0.5
            );

            saveData.putList("Pos", new ListTag<FloatTag>()
                    .add(new FloatTag((float) spawnPosition.x))
                    .add(new FloatTag((float) spawnPosition.y))
                    .add(new FloatTag((float) spawnPosition.z))
            );

            saveData.putList("Motion", new ListTag<FloatTag>()
                    .add(new FloatTag(0))
                    .add(new FloatTag(0))
                    .add(new FloatTag(0))
            );

            lookAt = getSide(face, 2);
        } else {
            spawnPosition = add(RANDOM.nextDouble(), 0.2, RANDOM.nextDouble());
            lookAt = spawnPosition.add(RANDOM.nextDouble(), 0, RANDOM.nextDouble());
        }

        double dx = lookAt.getX() - spawnPosition.getX();
        double dz = lookAt.getZ() - spawnPosition.getZ();
        float yaw = 0;

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

        Entity entity = Entity.createEntity(runtimeIdentifier, spawnPosition.getChunk(), saveData);
        if (entity != null) {
            removeOccupant(occupant);
            level.addSound(this, Sound.BLOCK_BEEHIVE_EXIT);
        }

        EntityBee bee = entity instanceof EntityBee ? (EntityBee) entity : null;

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
                Entity entity = spawnOccupant(occupant, validFaces);
                if (entity instanceof EntityBee) {
                    EntityBee bee = (EntityBee) entity;
                    if (player != null) {
                        bee.setAngryOnTarget(player);
                    } else {
                        bee.setAngry(true);
                    }
                }
            }
        }
    }

    @Override
    public boolean onUpdate() {
        if (this.closed || isEmpty()) {
            return false;
        }

        List<BlockFace> validSpawnFaces = null;

        // getOccupants will avoid ConcurrentModificationException if plugins changes the contents while iterating
        for (Occupant occupant : getOccupants()) {
            if (--occupant.ticksLeftToStay <= 0 && !(getLevel().isRaining() || !getLevel().isDay())) {
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
    public boolean isBlockEntityValid() {
        String id = this.getBlock().getId();
        return id == Block.BEEHIVE || id == Block.BEE_NEST;
    }

    private static String toStorageActorIdentifier(String identifier) {

        if (identifier == null || identifier.isEmpty()) {
            return "minecraft:bee<>";
        }

        return identifier.endsWith("<>") ? identifier : identifier + "<>";
    }

    private static String toRuntimeActorIdentifier(String identifier) {
        if (identifier == null) return "";
        return identifier.endsWith("<>") ? identifier.substring(0, identifier.length() - 2) : identifier;
    }

    /**
     * Represents an actor persisted inside a beehive.
     *
     * @author Curse
     */
    public static final class Occupant implements Cloneable {
        public static final Occupant[] EMPTY_ARRAY = new Occupant[0];
        private int ticksLeftToStay;
        private String actorIdentifier;
        private CompoundTag saveData;
        private Sound workSound = Sound.BLOCK_BEEHIVE_WORK;
        private float workSoundPitch = 1;
        private boolean muted;


        /**
         * Creates a beehive occupant.
         *
         * @param ticksLeftToStay ticks remaining before the occupant can leave
         * @param actorIdentifier persisted actor identifier
         * @param saveData persisted actor data
         */
        public Occupant(int ticksLeftToStay, String actorIdentifier, CompoundTag saveData) {
            this.ticksLeftToStay = ticksLeftToStay;
            this.actorIdentifier = actorIdentifier;
            this.saveData = saveData;
        }

        private Occupant(CompoundTag saved) {
            this.ticksLeftToStay = saved.getInt("TicksLeftToStay");
            this.actorIdentifier = saved.getString("ActorIdentifier");
            this.saveData = saved.getCompound("SaveData").copy();
        }

        /**
         * Serializes this occupant to its persisted NBT representation.
         *
         * @return serialized occupant data
         */
        public CompoundTag saveNBT() {
            return new CompoundTag()
                    .putString("ActorIdentifier", actorIdentifier)
                    .putCompound("SaveData", saveData)
                    .putInt("TicksLeftToStay", ticksLeftToStay);
        }

        /**
         * Returns whether the persisted actor carries nectar.
         *
         * @return whether the actor has nectar
         */
        public boolean getHasNectar() {
            if (!saveData.containsCompound("properties")) return false;
            CompoundTag properties = saveData.getCompound("properties");
            return properties.getBoolean("minecraft:has_nectar");
        }

        /**
         * Updates the persisted nectar state.
         *
         * @param hasNectar whether the actor has nectar
         */
        public void setHasNectar(boolean hasNectar) {
            CompoundTag properties = saveData.containsCompound("properties" ) ? saveData.getCompound("properties") : new CompoundTag();
            properties.putByte("minecraft:has_nectar", hasNectar ? 1 : 0);
            saveData.putCompound("properties", properties);
        }

        /**
         * Returns the ticks remaining before this occupant can leave.
         *
         * @return ticks remaining
         */
        public int getTicksLeftToStay() {
            return ticksLeftToStay;
        }

        /**
         * Sets the ticks remaining before this occupant can leave.
         *
         * @param ticksLeftToStay ticks remaining
         */
        public void setTicksLeftToStay(int ticksLeftToStay) {
            this.ticksLeftToStay = ticksLeftToStay;
        }

        /**
         * Returns the persisted actor identifier.
         *
         * @return actor identifier
         */
        public String getActorIdentifier() {
            return actorIdentifier;
        }

        /**
         * Sets the persisted actor identifier.
         *
         * @param actorIdentifier actor identifier
         */
        public void setActorIdentifier(String actorIdentifier) {
            this.actorIdentifier = actorIdentifier;
        }

        /**
         * Returns a copy of the persisted actor data.
         *
         * @return actor save data
         */
        public CompoundTag getSaveData() {
            return saveData.copy();
        }

        /**
         * Replaces the persisted actor data with a copy of the supplied compound.
         *
         * @param saveData actor save data
         */
        public void setSaveData(CompoundTag saveData) {
            this.saveData = saveData.copy();
        }

        /**
         * Returns the sound played while this occupant works.
         *
         * @return work sound
         */
        public Sound getWorkSound() {
            return workSound;
        }

        /**
         * Sets the sound played while this occupant works.
         *
         * @param workSound work sound
         */
        public void setWorkSound(Sound workSound) {
            this.workSound = workSound;
        }

        /**
         * Returns the pitch used for the occupant work sound.
         *
         * @return work sound pitch
         */
        public float getWorkSoundPitch() {
            return workSoundPitch;
        }

        /**
         * Sets the pitch used for the occupant work sound.
         *
         * @param workSoundPitch work sound pitch
         */
        public void setWorkSoundPitch(float workSoundPitch) {
            this.workSoundPitch = workSoundPitch;
        }

        /**
         * Returns whether occupant sounds are muted.
         *
         * @return whether sounds are muted
         */
        public boolean isMuted() {
            return muted;
        }

        /**
         * Sets whether occupant sounds are muted.
         *
         * @param muted whether sounds are muted
         */
        public void setMuted(boolean muted) {
            this.muted = muted;
        }

        @Override
        public String toString() {
            return "Occupant{" +
                    "ticksLeftToStay=" + ticksLeftToStay +
                    ", actorIdentifier='" + actorIdentifier + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Occupant occupant = (Occupant) o;
            return ticksLeftToStay == occupant.ticksLeftToStay &&
                    Objects.equals(actorIdentifier, occupant.actorIdentifier) &&
                    Objects.equals(saveData, occupant.saveData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ticksLeftToStay, actorIdentifier, saveData);
        }

        @Override
        protected Occupant clone() {
            try {
                Occupant occupant = (Occupant) super.clone();
                occupant.saveData = this.saveData.copy();
                return occupant;
            } catch (CloneNotSupportedException e) {
                throw new InternalError("Unexpected exception", e);
            }
        }
    }
}