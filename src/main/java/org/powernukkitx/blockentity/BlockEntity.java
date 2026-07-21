package org.powernukkitx.blockentity;

import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.scheduler.Task;
import org.powernukkitx.utils.ChunkException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MagicDroidX
 */
@Slf4j
public abstract class BlockEntity extends Position implements BlockEntityID {
    public static long count = 1;
    public IChunk chunk;
    public String name;
    public long id;
    public boolean movable;
    public boolean closed = false;
    protected CompoundTag nbt;
    public volatile CompoundTag serializationSnapshot;
    protected Server server;

    public static BlockEntity createBlockEntity(String type, Position position, Object... args) {
        return createBlockEntity(type, position, BlockEntity.getDefaultCompound(position, type), args);
    }


    public static BlockEntity createBlockEntity(String type, Position pos, CompoundTag nbt, Object... args) {
        return createBlockEntity(type, pos.getLevel().getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt, args);
    }

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

    public BlockEntity(IChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null || chunk.getProvider().getLevel() == null) {
            throw new ChunkException("Invalid garbage Chunk given to Block Entity");
        }

        this.server = chunk.getProvider().getLevel().getServer();
        this.chunk = chunk;
        this.nbt = nbt;
        this.setLevel(chunk.getProvider().getLevel());
        if (nbt.getString("id") == null || nbt.getString("id").isEmpty()) {
            log.warn("Tried to create a block entity with an invalid id, {}", this.getClass().getSimpleName());
        }
        this.name = nbt.getString("id");
        this.id = BlockEntity.count++;
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.z = nbt.getInt("z");

        if (this.nbt.contains("isMovable")) {
            this.movable = nbt.getBoolean("isMovable");
        } else {
            this.movable = true;
            this.nbt.putBoolean("isMovable", true);
        }

        this.initBlockEntity();

        if (closed) {
            throw new IllegalStateException("Could not create the entity " + getClass().getName() + ", the initializer closed it on construction.");
        }

        this.chunk.addBlockEntity(this);
        this.getLevel().addBlockEntity(this);
    }

    protected void initBlockEntity() {
        loadNBT();
    }

    public void loadNBT() {
    }

    public void saveNBT() {
        this.nbt.putString("id", this.getSaveId())
                .putInt("x", (int) this.getX())
                .putInt("y", (int) this.getY())
                .putInt("z", (int) this.getZ())
                .putBoolean("isMovable", this.movable);
    }

    public final String getSaveId() {
        return Registries.BLOCKENTITY.getSaveId(getClass());
    }

    public long getId() {
        return id;
    }

    /**
     * Returns a position-independent snapshot of this block entity's NBT, suitable for copying
     * the block entity elsewhere (item block data, block picking, structure saving, equality checks).
     * <p>
     * The returned tag is a defensive deep copy: the live {@link #nbt} is never modified, and callers
     * are free to mutate the result. The {@code id}, {@code x}, {@code y} and {@code z} tags are
     * stripped since they only describe this block entity's current identity/location. Subclasses may
     * override to additionally strip location-bound data (e.g. {@link BlockEntityChest} removes its
     * {@code pairx}/{@code pairz} pairing coordinates).
     *
     * @return a cleaned copy of the NBT, or {@code null} if nothing meaningful remains after cleaning
     */
    public CompoundTag getCleanedNBT() {
        this.saveNBT();
        final CompoundTag cleaned = this.nbt.copy();
        cleaned.remove("id", "x", "y", "z");
        if (!cleaned.isEmpty()) {
            return cleaned;
        } else {
            return null;
        }
    }

    public Block getBlock() {
        return this.getLevelBlock();
    }

    public abstract boolean isBlockEntityValid();

    public boolean onUpdate() {
        if (!isBlockEntityValid()) {
            close();
        }
        return false;
    }

    public final void scheduleUpdate() {
        Level level = this.level;
        if (level == null || this.closed) {
            return;
        }
        level.scheduleBlockEntityUpdate(this);
    }

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

    public void onBreak(boolean isSilkTouch) {
    }

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
     * Indicates if an observer blocks that are looking at this block should blink when {@link #setDirty()} is called.
     */
    public boolean isObservable() {
        return true;
    }

    public String getName() {
        return name;
    }

    public boolean isMovable() {
        return movable;
    }

    public static CompoundTag getDefaultCompound(Vector3 pos, String id) {
        return new CompoundTag()
                .putString("id", id)
                .putInt("x", pos.getFloorX())
                .putInt("y", pos.getFloorY())
                .putInt("z", pos.getFloorZ());
    }

    @Nullable
    @Override
    public final BlockEntity getLevelBlockEntity() {
        return super.getLevelBlockEntity();
    }

    public CompoundTag getNbt() {
        return this.nbt;
    }

}
