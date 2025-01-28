package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.ChunkException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a block entity.
 * Provides methods for creating and managing block entities.
 * 
 * @autor MagicDroidX
 */
@Slf4j
public abstract class BlockEntity extends Position implements BlockEntityID {
    public static long count = 1;
    public IChunk chunk;
    public String name;
    public long id;
    public boolean movable;
    public boolean closed = false;
    public CompoundTag namedTag;
    protected Server server;

    /**
     * Creates a block entity with default compound tag.
     * 
     * @param type The type of block entity.
     * @param position The position of the block entity.
     * @param args Additional arguments.
     * @return The created block entity.
     */
    public static BlockEntity createBlockEntity(String type, Position position, Object... args) {
        return createBlockEntity(type, position, BlockEntity.getDefaultCompound(position, type), args);
    }

    /**
     * Creates a block entity with specified compound tag.
     * 
     * @param type The type of block entity.
     * @param pos The position of the block entity.
     * @param nbt The compound tag.
     * @param args Additional arguments.
     * @return The created block entity.
     */
    public static BlockEntity createBlockEntity(String type, Position pos, CompoundTag nbt, Object... args) {
        return createBlockEntity(type, pos.getLevel().getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt, args);
    }

    /**
     * Creates a block entity with specified chunk and compound tag.
     * 
     * @param type The type of block entity.
     * @param chunk The chunk of the block entity.
     * @param nbt The compound tag.
     * @param args Additional arguments.
     * @return The created block entity.
     */
    public static BlockEntity createBlockEntity(String type, IChunk chunk, CompoundTag nbt, Object... args) {
        BlockEntity blockEntity = null;

        Class<? extends BlockEntity> clazz = Registries.BLOCKENTITY.get(type);
        if (clazz != null) {
            List<Exception> exceptions = null;

            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (blockEntity != null) {
                    break;
                }

                if (constructor.getParameterCount() != (args == null ? 2 : args.length + 2)) {
                    continue;
                }

                try {
                    if (args == null || args.length == 0) {
                        blockEntity = (BlockEntity) constructor.newInstance(chunk, nbt);
                    } else {
                        Object[] objects = new Object[args.length + 2];

                        objects[0] = chunk;
                        objects[1] = nbt;
                        System.arraycopy(args, 0, objects, 2, args.length);
                        blockEntity = (BlockEntity) constructor.newInstance(objects);

                    }
                } catch (Exception e) {
                    if (exceptions == null) {
                        exceptions = new ArrayList<>();
                    }
                    exceptions.add(e);
                }

            }
            if (blockEntity == null) {
                Exception cause = new IllegalArgumentException("Could not create a block entity of type " + type, exceptions != null && exceptions.size() > 0 ? exceptions.get(0) : null);
                if (exceptions != null && exceptions.size() > 1) {
                    for (int i = 1; i < exceptions.size(); i++) {
                        cause.addSuppressed(exceptions.get(i));
                    }
                }
                log.error("Could not create a block entity of type {} with {} args", type, args == null ? 0 : args.length, cause);
            }
        } else {
            log.debug("Block entity type {} is unknown", type);
        }

        return blockEntity;
    }

    /**
     * Constructor for BlockEntity.
     * 
     * @param chunk The chunk of the block entity.
     * @param nbt The compound tag.
     */
    public BlockEntity(IChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null || chunk.getProvider().getLevel() == null) {
            throw new ChunkException("Invalid garbage Chunk given to Block Entity");
        }

        this.server = chunk.getProvider().getLevel().getServer();
        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.namedTag = nbt;
        this.name = "";
        this.id = BlockEntity.count++;
        this.x = this.namedTag.getInt("x");
        this.y = this.namedTag.getInt("y");
        this.z = this.namedTag.getInt("z");

        if (namedTag.contains("isMovable")) {
            this.movable = this.namedTag.getBoolean("isMovable");
        } else {
            this.movable = true;
            namedTag.putBoolean("isMovable", true);
        }

        this.initBlockEntity();

        if (closed) {
            throw new IllegalStateException("Could not create the entity " + getClass().getName() + ", the initializer closed it on construction.");
        }

        this.chunk.addBlockEntity(this);
        this.getLevel().addBlockEntity(this);
    }

    /**
     * Initializes the block entity.
     */
    protected void initBlockEntity() {
        loadNBT();
    }

    /**
     * Reads data from the block entity's named tag.
     */
    public void loadNBT() {
    }

    /**
     * Stores block entity data to the named tag.
     */
    public void saveNBT() {
        this.namedTag.putString("id", this.getSaveId());
        this.namedTag.putInt("x", (int) this.getX());
        this.namedTag.putInt("y", (int) this.getY());
        this.namedTag.putInt("z", (int) this.getZ());
        this.namedTag.putBoolean("isMovable", this.movable);
    }

    /**
     * Returns the save ID of the block entity.
     * 
     * @return The save ID.
     */
    public final String getSaveId() {
        return Registries.BLOCKENTITY.getSaveId(getClass());
    }

    /**
     * Returns the ID of the block entity.
     * 
     * @return The ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns a cleaned version of the named tag.
     * 
     * @return The cleaned named tag.
     */
    public CompoundTag getCleanedNBT() {
        this.saveNBT();
        CompoundTag tag = this.namedTag.copy();
        tag.remove("x").remove("y").remove("z").remove("id");
        if (!tag.getTags().isEmpty()) {
            return tag;
        } else {
            return null;
        }
    }

    /**
     * Returns the block associated with this block entity.
     * 
     * @return The block.
     */
    public Block getBlock() {
        return this.getLevelBlock();
    }

    /**
     * Checks if the block entity is valid.
     * 
     * @return True if valid, false otherwise.
     */
    public abstract boolean isBlockEntityValid();

    /**
     * Called on update.
     * 
     * @return True if updated, false otherwise.
     */
    public boolean onUpdate() {
        return false;
    }

    /**
     * Schedules an update for the block entity.
     */
    public final void scheduleUpdate() {
        this.level.scheduleBlockEntityUpdate(this);
    }

    /**
     * Closes the block entity.
     */
    public void close() {
        if (!this.closed) {
            this.closed = true;
            if (this.chunk != null) {
                this.chunk.removeBlockEntity(this);
            }
            if (this.level != null) {
                this.level.removeBlockEntity(this);
            }
            this.level = null;
        }
    }

    /**
     * Called when the block is broken.
     * 
     * @param isSilkTouch True if broken with silk touch, false otherwise.
     */
    public void onBreak(boolean isSilkTouch) {
    }

    /**
     * Marks the block entity as dirty.
     */
    public void setDirty() {
        chunk.setChanged();

        if (!this.getLevelBlock().isAir()) {
            getLevel().getScheduler().scheduleTask(new Task() {
                @Override
                public void onRun(int currentTick) {
                    if (isValid() && isBlockEntityValid()) {
                        getLevel().updateComparatorOutputLevelSelective(BlockEntity.this, isObservable());
                    }
                }
            });
        }
    }

    /**
     * Indicates if observer blocks that are looking at this block should blink when {@link #setDirty()} is called.
     * 
     * @return True if observable, false otherwise.
     */
    public boolean isObservable() {
        return true;
    }

    /**
     * Returns the name of the block entity.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns if the block entity is movable.
     * 
     * @return True if movable, false otherwise.
     */
    public boolean isMovable() {
        return movable;
    }

    /**
     * Returns the default compound tag for the block entity.
     * 
     * @param pos The position of the block entity.
     * @param id The ID of the block entity.
     * @return The default compound tag.
     */
    public static CompoundTag getDefaultCompound(Vector3 pos, String id) {
        return new CompoundTag()
                .putString("id", id)
                .putInt("x", pos.getFloorX())
                .putInt("y", pos.getFloorY())
                .putInt("z", pos.getFloorZ());
    }

    /**
     * Returns the block entity at the level.
     * 
     * @return The block entity.
     */
    @Nullable
    @Override
    public final BlockEntity getLevelBlockEntity() {
        return super.getLevelBlockEntity();
    }
}