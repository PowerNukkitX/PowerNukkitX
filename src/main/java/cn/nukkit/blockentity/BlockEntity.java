package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.ChunkException;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

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
    public NbtMap namedTag;
    protected Server server;

    public static BlockEntity createBlockEntity(String type, Position position, Object... args) {
        return createBlockEntity(type, position, BlockEntity.getDefaultCompound(position, type), args);
    }


    public static BlockEntity createBlockEntity(String type, Position pos, NbtMap nbt, Object... args) {
        return createBlockEntity(type, pos.getLevel().getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt, args);
    }

    public static BlockEntity createBlockEntity(String type, IChunk chunk, NbtMap nbt, Object... args) {
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

    public BlockEntity(IChunk chunk, NbtMap nbt) {
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

        if (namedTag.containsKey("isMovable")) {
            this.movable = this.namedTag.getBoolean("isMovable");
        } else {
            this.movable = true;
            this.namedTag = this.namedTag.toBuilder()
                    .putBoolean("isMovable", true)
                    .build();
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
        this.namedTag = this.namedTag.toBuilder()
                .putString("id", this.getSaveId())
                .putInt("x", (int) this.getX())
                .putInt("y", (int) this.getY())
                .putInt("z", (int) this.getZ())
                .putBoolean("isMovable", this.movable)
                .build();
    }

    public final String getSaveId() {
        return Registries.BLOCKENTITY.getSaveId(getClass());
    }

    public long getId() {
        return id;
    }

    public NbtMap getCleanedNBT() {
        this.saveNBT();
        NbtMapBuilder builder = this.namedTag.toBuilder();
        builder.remove("x");
        builder.remove("y");
        builder.remove("z");
        builder.remove("id");
        if (!builder.isEmpty()) {
            return builder.build();
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
        this.level.scheduleBlockEntityUpdate(this);
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

    public static NbtMap getDefaultCompound(Vector3 pos, String id) {
        return NbtMap.builder()
                .putString("id", id)
                .putInt("x", pos.getFloorX())
                .putInt("y", pos.getFloorY())
                .putInt("z", pos.getFloorZ())
                .build();
    }

    @Nullable
    @Override
    public final BlockEntity getLevelBlockEntity() {
        return super.getLevelBlockEntity();
    }
}
