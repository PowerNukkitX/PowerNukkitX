package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.StructBlockInventory;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.structure.Structure;
import cn.nukkit.level.structure.StructureAPI;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.base.Strings;
import org.cloudburstmc.protocol.bedrock.data.structure.AnimationMode;
import org.cloudburstmc.protocol.bedrock.data.structure.Mirror;
import org.cloudburstmc.protocol.bedrock.data.structure.Rotation;
import org.cloudburstmc.protocol.bedrock.data.structure.StructureBlockType;
import org.cloudburstmc.protocol.bedrock.data.structure.StructureRedstoneSaveMode;
import org.cloudburstmc.protocol.bedrock.packet.StructureBlockUpdatePacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class BlockEntityStructBlock extends BlockEntitySpawnable implements IStructBlock, BlockEntityInventoryHolder {
    private AnimationMode animationMode;
    private float animationSeconds;
    private StructureBlockType data;
    private String dataField;
    private boolean ignoreEntities;
    private boolean includePlayers;
    private float integrity;
    private boolean isPowered;
    private Mirror mirror;
    private StructureRedstoneSaveMode redstoneSaveMode;
    private boolean removeBlocks;
    private Rotation rotation;
    private long seed;
    private boolean showBoundingBox;
    private String structureName;
    private BlockVector3 size;
    private BlockVector3 offset;
    private StructBlockInventory structBlockInventory;


    public BlockEntityStructBlock(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        structBlockInventory = new StructBlockInventory(this);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        final CompoundTag nbtMap = getNbt();
        if (this.nbt.contains(TAG_ANIMATION_MODE)) {
            this.animationMode = AnimationMode.from(nbtMap.getByte(TAG_ANIMATION_MODE));
        } else {
            this.animationMode = AnimationMode.from(0);
        }
        if (this.nbt.contains(TAG_ANIMATION_SECONDS)) {
            this.animationSeconds = nbtMap.getFloat(TAG_ANIMATION_SECONDS);
        } else {
            this.animationSeconds = 0f;
        }
        if (this.nbt.contains(TAG_DATA)) {
            this.data = StructureBlockType.from(nbtMap.getByte(TAG_DATA));
        } else {
            this.data = StructureBlockType.from(1);
        }
        if (this.nbt.contains(TAG_DATA_FIELD)) {
            this.dataField = nbtMap.getString(TAG_DATA_FIELD);
        } else {
            this.dataField = "";
        }
        if (this.nbt.contains(TAG_IGNORE_ENTITIES)) {
            this.ignoreEntities = nbtMap.getBoolean(TAG_IGNORE_ENTITIES);
        } else {
            this.ignoreEntities = false;
        }
        if (this.nbt.contains(TAG_INCLUDE_PLAYERS)) {
            this.includePlayers = nbtMap.getBoolean(TAG_INCLUDE_PLAYERS);
        } else {
            this.includePlayers = false;
        }
        if (this.nbt.contains(TAG_INTEGRITY)) {
            this.integrity = nbtMap.getFloat(TAG_INTEGRITY);
        } else {
            this.integrity = 100f;
        }
        if (this.nbt.contains(TAG_IS_POWERED)) {
            this.isPowered = nbtMap.getBoolean(TAG_IS_POWERED);
        } else {
            this.isPowered = false;
        }
        if (this.nbt.contains(TAG_MIRROR)) {
            this.mirror = Mirror.from(nbtMap.getByte(TAG_MIRROR));
        } else {
            this.mirror = Mirror.from(0);
        }
        if (this.nbt.contains(TAG_REDSTONE_SAVEMODE)) {
            this.redstoneSaveMode = StructureRedstoneSaveMode.from(nbtMap.getByte(TAG_REDSTONE_SAVEMODE));
        } else {
            this.redstoneSaveMode = StructureRedstoneSaveMode.from(0);
        }
        if (this.nbt.contains(TAG_REMOVE_BLOCKS)) {
            this.removeBlocks = nbtMap.getBoolean(TAG_REMOVE_BLOCKS);
        } else {
            this.removeBlocks = false;
        }
        if (this.nbt.contains(TAG_ROTATION)) {
            this.rotation = Rotation.from(nbtMap.getByte(TAG_ROTATION));
        } else {
            this.rotation = Rotation.from(0);
        }
        if (this.nbt.contains(TAG_SEED)) {
            this.seed = nbtMap.getLong(TAG_SEED);
        } else {
            this.seed = 0L;
        }
        if (this.nbt.contains(TAG_SHOW_BOUNDING_BOX)) {
            this.showBoundingBox = nbtMap.getBoolean(TAG_SHOW_BOUNDING_BOX);
        } else {
            this.showBoundingBox = true;
        }
        if (this.nbt.contains(TAG_STRUCTURE_NAME)) {
            this.structureName = nbtMap.getString(TAG_STRUCTURE_NAME);
        } else {
            this.structureName = "";
        }
        if (this.nbt.contains(TAG_X_STRUCTURE_OFFSET) && this.nbt.contains(TAG_Y_STRUCTURE_OFFSET) && this.nbt.contains(TAG_Z_STRUCTURE_OFFSET)) {
            this.offset = new BlockVector3(nbtMap.getInt(TAG_X_STRUCTURE_OFFSET), nbtMap.getInt(TAG_Y_STRUCTURE_OFFSET), nbtMap.getInt(TAG_Z_STRUCTURE_OFFSET));
        } else {
            this.offset = new BlockVector3(0, -1, 0);
        }
        if (this.nbt.contains(TAG_X_STRUCTURE_SIZE) && this.nbt.contains(TAG_Y_STRUCTURE_SIZE) && this.nbt.contains(TAG_Z_STRUCTURE_SIZE)) {
            this.size = new BlockVector3(nbtMap.getInt(TAG_X_STRUCTURE_SIZE), nbtMap.getInt(TAG_Y_STRUCTURE_SIZE), nbtMap.getInt(TAG_Z_STRUCTURE_SIZE));
        } else {
            this.size = new BlockVector3(5, 5, 5);
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putByte(TAG_ANIMATION_MODE, (byte) this.animationMode.ordinal())
                .putFloat(TAG_ANIMATION_SECONDS, this.animationSeconds)
                .putInt(TAG_DATA, this.data.ordinal())
                .putString(TAG_DATA_FIELD, this.dataField)
                .putBoolean(TAG_IGNORE_ENTITIES, ignoreEntities)
                .putBoolean(TAG_INCLUDE_PLAYERS, includePlayers)
                .putFloat(TAG_INTEGRITY, integrity)
                .putBoolean(TAG_IS_POWERED, isPowered)
                .putByte(TAG_MIRROR, (byte) mirror.ordinal())
                .putByte(TAG_REDSTONE_SAVEMODE, (byte) redstoneSaveMode.ordinal())
                .putBoolean(TAG_REMOVE_BLOCKS, removeBlocks)
                .putByte(TAG_ROTATION, (byte) rotation.ordinal())
                .putLong(TAG_SEED, seed)
                .putBoolean(TAG_SHOW_BOUNDING_BOX, showBoundingBox)
                .putString(TAG_STRUCTURE_NAME, structureName)
                .putInt(TAG_X_STRUCTURE_OFFSET, offset.x)
                .putInt(TAG_Y_STRUCTURE_OFFSET, offset.y)
                .putInt(TAG_Z_STRUCTURE_OFFSET, offset.z)
                .putInt(TAG_X_STRUCTURE_SIZE, size.x)
                .putInt(TAG_Y_STRUCTURE_SIZE, size.y)
                .putInt(TAG_Z_STRUCTURE_SIZE, size.z);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putByte(TAG_ANIMATION_MODE, (byte) this.animationMode.ordinal())
                .putFloat(TAG_ANIMATION_SECONDS, this.animationSeconds)
                .putInt(TAG_DATA, this.data.ordinal())
                .putString(TAG_DATA_FIELD, this.dataField)
                .putBoolean(TAG_IGNORE_ENTITIES, ignoreEntities)
                .putBoolean(TAG_INCLUDE_PLAYERS, includePlayers)
                .putFloat(TAG_INTEGRITY, integrity)
                .putBoolean(TAG_IS_POWERED, isPowered)
                .putByte(TAG_MIRROR, (byte) mirror.ordinal())
                .putByte(TAG_REDSTONE_SAVEMODE, (byte) redstoneSaveMode.ordinal())
                .putBoolean(TAG_REMOVE_BLOCKS, removeBlocks)
                .putByte(TAG_ROTATION, (byte) rotation.ordinal())
                .putLong(TAG_SEED, seed)
                .putBoolean(TAG_SHOW_BOUNDING_BOX, showBoundingBox)
                .putString(TAG_STRUCTURE_NAME, structureName)
                .putInt(TAG_X_STRUCTURE_OFFSET, offset.x)
                .putInt(TAG_Y_STRUCTURE_OFFSET, offset.y)
                .putInt(TAG_Z_STRUCTURE_OFFSET, offset.z)
                .putInt(TAG_X_STRUCTURE_SIZE, size.x)
                .putInt(TAG_Y_STRUCTURE_SIZE, size.y)
                .putInt(TAG_Z_STRUCTURE_SIZE, size.z);
    }

    @Override
    public boolean isBlockEntityValid() {
        String blockId = this.getLevelBlock().getId();
        return blockId == BlockID.STRUCTURE_BLOCK;
    }

    @NotNull
    @Override
    public String getName() {
        return this.hasName() ? getNbt().getString(TAG_CUSTOM_NAME) : STRUCTURE_BLOCK;
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains(TAG_CUSTOM_NAME);
    }

    @Override
    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            this.nbt.remove(TAG_CUSTOM_NAME);
        } else {
            this.nbt.putString(TAG_CUSTOM_NAME, name);
        }
    }

    @Override
    public Inventory getInventory() {
        return structBlockInventory;
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }

    public void updateSetting(StructureBlockUpdatePacket packet) {
        var editorData = packet.getStructureData();
        this.animationMode = editorData.getStructureSettings().getAnimationMode();
        this.animationSeconds = editorData.getStructureSettings().getAnimationSeconds();
        this.data = editorData.getStructureBlockType();
        this.dataField = editorData.getDataField();
        this.ignoreEntities = editorData.getStructureSettings().isShouldIgnoreEntities();
        this.includePlayers = editorData.isShouldPlayersBeIncluded();
        this.integrity = editorData.getStructureSettings().getIntegrityValue();
        this.mirror = editorData.getStructureSettings().getMirror();
        this.redstoneSaveMode = editorData.getRedstoneSaveMode();
        this.removeBlocks = editorData.getStructureSettings().isShouldIgnoreBlocks();
        this.rotation = editorData.getStructureSettings().getRotation();
        this.seed = editorData.getStructureSettings().getIntegritySeed();
        this.showBoundingBox = editorData.isShouldShowBoundingBox();
        this.structureName = editorData.getStructureName();
        this.offset = BlockVector3.fromNetwork(editorData.getStructureSettings().getStructureOffset());
        this.size = BlockVector3.fromNetwork(editorData.getStructureSettings().getStructureSize());

        if (packet.isTrigger()) onPower();
    }

    public void onPower() {
        if (data == StructureBlockType.LOAD) placeStructure();
        else if (data == StructureBlockType.SAVE) saveStructure();
    }

    public void placeStructure() {
        if (structureName.isEmpty()) return;
        Structure structure = StructureAPI.load(structureName);

        if (structure == null) return;

        if (rotation != Rotation.NONE)
            structure = structure.rotate(rotation);

        if (mirror != Mirror.NONE)
            structure = structure.mirror(mirror);

        BlockVector3 pos = this.getBlock().asBlockVector3().add(offset);
        structure.place(Position.fromObject(pos.asVector3(), this.getLevel()), !ignoreEntities);
    }

    public void saveStructure() {
        int structureStartX = (int) this.getBlock().getX() + offset.x;
        int structureStartY = (int) this.getBlock().getY() + offset.y;
        int structureStartZ = (int) this.getBlock().getZ() + offset.z;

        int sizeX = size.x;
        int sizeY = size.y;
        int sizeZ = size.z;

        Structure structure = Structure.create(getLevel(),
                structureStartX, structureStartY, structureStartZ,
                sizeX, sizeY, sizeZ,
                !ignoreEntities
        );

        StructureAPI.save(structure, structureName);
    }
}
