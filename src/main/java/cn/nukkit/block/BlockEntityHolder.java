package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.LevelException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public interface BlockEntityHolder<E extends BlockEntity> {

    @Nullable
    default E getBlockEntity() {
        Level level = getLevel();
        if (level == null) {
            throw new LevelException("Undefined Level reference");
        }
        BlockEntity blockEntity;
        if (this instanceof Vector3) {
            blockEntity = level.getBlockEntity((Vector3) this);
        } else if (this instanceof BlockVector3) {
            blockEntity = level.getBlockEntity((BlockVector3) this);
        } else {
            blockEntity = level.getBlockEntity(new BlockVector3(getFloorX(), getFloorY(), getFloorZ()));
        }

        Class<? extends E> blockEntityClass = getBlockEntityClass();
        if (blockEntityClass.isInstance(blockEntity)) {
            return blockEntityClass.cast(blockEntity);
        }
        return null;
    }

    default @NotNull E createBlockEntity() {
        return createBlockEntity(null);
    }

    default @NotNull E createBlockEntity(@Nullable CompoundTag initialData, @Nullable Object... args) {
        String typeName = getBlockEntityType();
        IChunk chunk = getChunk();
        if (chunk == null) {
            throw new LevelException("Undefined Level or chunk reference");
        }
        if (initialData == null) {
            initialData = new CompoundTag();
        } else {
            initialData = initialData.copy();
        }
        BlockEntity created = BlockEntity.createBlockEntity(typeName, chunk,
                initialData
                        .putString("id", typeName)
                        .putInt("x", getFloorX())
                        .putInt("y", getFloorY())
                        .putInt("z", getFloorZ()),
                args);

        Class<? extends E> entityClass = getBlockEntityClass();

        if (!entityClass.isInstance(created)) {
            String error = "Failed to create the block entity " + typeName + " of class " + entityClass + " at " + getLocation() + ", " +
                    "the created type is not an instance of the requested class. Created: " + created;
            if (created != null) {
                created.close();
            }
            throw new IllegalStateException(error);
        }
        return entityClass.cast(created);
    }

    default @NotNull E getOrCreateBlockEntity() {
        E blockEntity = getBlockEntity();
        if (blockEntity != null) {
            return blockEntity;
        }
        return createBlockEntity();
    }

    @NotNull
    Class<? extends E> getBlockEntityClass();

    @NotNull
    String getBlockEntityType();

    @Nullable
    IChunk getChunk();

    int getFloorX();

    int getFloorY();

    int getFloorZ();

    @NotNull
    Location getLocation();

    Level getLevel();

    @Nullable
    static <E extends BlockEntity, H extends BlockEntityHolder<E>> E setBlockAndCreateEntity(@NotNull H holder) {
        return setBlockAndCreateEntity(holder, true, true);
    }

    @Nullable
    static <E extends BlockEntity, H extends BlockEntityHolder<E>> E setBlockAndCreateEntity(
            @NotNull H holder, boolean direct, boolean update) {
        return setBlockAndCreateEntity(holder, direct, update, null);
    }

    @Nullable
    static <E extends BlockEntity, H extends BlockEntityHolder<E>> E setBlockAndCreateEntity(
            @NotNull H holder, boolean direct, boolean update, @Nullable CompoundTag initialData,
            @Nullable Object... args) {
        Block block = holder.getBlock();
        Level level = block.getLevel();
        Block layer0 = level.getBlock(block, 0);
        Block layer1 = level.getBlock(block, 1);
        if (level.setBlock(block, block, direct, update)) {
            try {
                return holder.createBlockEntity(initialData, args);
            } catch (Exception e) {
                Loggers.logBlocKEntityHolder.warn("Failed to create block entity {} at {} at ", holder.getBlockEntityType(), holder.getLocation(), e);
                level.setBlock(layer0, 0, layer0, direct, update);
                level.setBlock(layer1, 1, layer1, direct, update);
                throw e;
            }
        }

        return null;
    }

    default Block getBlock() {
        if (this instanceof Block block) {
            return block;
        } else if (this instanceof Position position) {
            return position.getLevelBlock();
        } else if (this instanceof Vector3 vector3) {
            return getLevel().getBlock(vector3);
        } else {
            return getLevel().getBlock(getFloorX(), getFloorY(), getFloorZ());
        }
    }
}
