package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.enums.StructureBlockType;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.StructBlockInventory;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.structure.Structure;
import cn.nukkit.level.structure.StructureAPI;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.StructureBlockUpdatePacket;
import cn.nukkit.network.protocol.types.StructureAnimationMode;
import cn.nukkit.network.protocol.types.StructureMirror;
import cn.nukkit.network.protocol.types.StructureRedstoneSaveMode;
import cn.nukkit.network.protocol.types.Rotation;
import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class BlockEntityStructBlock extends BlockEntitySpawnable implements IStructBlock, BlockEntityInventoryHolder {
    private StructureAnimationMode animationMode;
    private float animationSeconds;
    private StructureBlockType data;
    private String dataField;
    private boolean ignoreEntities;
    private boolean includePlayers;
    private float integrity;
    private boolean isPowered;
    private StructureMirror mirror;
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
        if (this.namedTag.contains(TAG_ANIMATION_MODE)) {
            this.animationMode = StructureAnimationMode.from(this.namedTag.getByte(TAG_ANIMATION_MODE));
        } else {
            this.animationMode = StructureAnimationMode.from(0);
        }
        if (this.namedTag.contains(TAG_ANIMATION_SECONDS)) {
            this.animationSeconds = this.namedTag.getFloat(TAG_ANIMATION_SECONDS);
        } else {
            this.animationSeconds = 0f;
        }
        if (this.namedTag.contains(TAG_DATA)) {
            this.data = StructureBlockType.from(this.namedTag.getByte(TAG_DATA));
        } else {
            this.data = StructureBlockType.from(1);
        }
        if (this.namedTag.contains(TAG_DATA_FIELD)) {
            this.dataField = this.namedTag.getString(TAG_DATA_FIELD);
        } else {
            this.dataField = "";
        }
        if (this.namedTag.contains(TAG_IGNORE_ENTITIES)) {
            this.ignoreEntities = this.namedTag.getBoolean(TAG_IGNORE_ENTITIES);
        } else {
            this.ignoreEntities = false;
        }
        if (this.namedTag.contains(TAG_INCLUDE_PLAYERS)) {
            this.includePlayers = this.namedTag.getBoolean(TAG_INCLUDE_PLAYERS);
        } else {
            this.includePlayers = false;
        }
        if (this.namedTag.contains(TAG_INTEGRITY)) {
            this.integrity = this.namedTag.getFloat(TAG_INTEGRITY);
        } else {
            this.integrity = 100f;
        }
        if (this.namedTag.contains(TAG_IS_POWERED)) {
            this.isPowered = this.namedTag.getBoolean(TAG_IS_POWERED);
        } else {
            this.isPowered = false;
        }
        if (this.namedTag.contains(TAG_MIRROR)) {
            this.mirror = StructureMirror.from(this.namedTag.getByte(TAG_MIRROR));
        } else {
            this.mirror = StructureMirror.from(0);
        }
        if (this.namedTag.contains(TAG_REDSTONE_SAVEMODE)) {
            this.redstoneSaveMode = StructureRedstoneSaveMode.from(this.namedTag.getByte(TAG_REDSTONE_SAVEMODE));
        } else {
            this.redstoneSaveMode = StructureRedstoneSaveMode.from(0);
        }
        if (this.namedTag.contains(TAG_REMOVE_BLOCKS)) {
            this.removeBlocks = this.namedTag.getBoolean(TAG_REMOVE_BLOCKS);
        } else {
            this.removeBlocks = false;
        }
        if (this.namedTag.contains(TAG_ROTATION)) {
            this.rotation = Rotation.from(this.namedTag.getByte(TAG_ROTATION));
        } else {
            this.rotation = Rotation.from(0);
        }
        if (this.namedTag.contains(TAG_SEED)) {
            this.seed = this.namedTag.getLong(TAG_SEED);
        } else {
            this.seed = 0L;
        }
        if (this.namedTag.contains(TAG_SHOW_BOUNDING_BOX)) {
            this.showBoundingBox = this.namedTag.getBoolean(TAG_SHOW_BOUNDING_BOX);
        } else {
            this.showBoundingBox = true;
        }
        if (this.namedTag.contains(TAG_STRUCTURE_NAME)) {
            this.structureName = this.namedTag.getString(TAG_STRUCTURE_NAME);
        } else {
            this.structureName = "";
        }
        if (this.namedTag.contains(TAG_X_STRUCTURE_OFFSET) && this.namedTag.contains(TAG_Y_STRUCTURE_OFFSET) && this.namedTag.contains(TAG_Z_STRUCTURE_OFFSET)) {
            this.offset = new BlockVector3(this.namedTag.getInt(TAG_X_STRUCTURE_OFFSET), this.namedTag.getInt(TAG_Y_STRUCTURE_OFFSET), this.namedTag.getInt(TAG_Z_STRUCTURE_OFFSET));
        } else {
            this.offset = new BlockVector3(0, -1, 0);
        }
        if (this.namedTag.contains(TAG_X_STRUCTURE_SIZE) && this.namedTag.contains(TAG_Y_STRUCTURE_SIZE) && this.namedTag.contains(TAG_Z_STRUCTURE_SIZE)) {
            this.size = new BlockVector3(this.namedTag.getInt(TAG_X_STRUCTURE_SIZE), this.namedTag.getInt(TAG_Y_STRUCTURE_SIZE), this.namedTag.getInt(TAG_Z_STRUCTURE_SIZE));
        } else {
            this.size = new BlockVector3(5, 5, 5);
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putByte(TAG_ANIMATION_MODE, this.animationMode.ordinal())
                .putFloat(TAG_ANIMATION_SECONDS, this.animationSeconds)
                .putInt(TAG_DATA, this.data.ordinal())
                .putString(TAG_DATA_FIELD, this.dataField)
                .putBoolean(TAG_IGNORE_ENTITIES, ignoreEntities)
                .putBoolean(TAG_INCLUDE_PLAYERS, includePlayers)
                .putFloat(TAG_INTEGRITY, integrity)
                .putBoolean(TAG_IS_POWERED, isPowered)
                .putByte(TAG_MIRROR, mirror.ordinal())
                .putByte(TAG_REDSTONE_SAVEMODE, redstoneSaveMode.ordinal())
                .putBoolean(TAG_REMOVE_BLOCKS, removeBlocks)
                .putByte(TAG_ROTATION, rotation.ordinal())
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
        this.namedTag.putByte(TAG_ANIMATION_MODE, this.animationMode.ordinal())
                .putFloat(TAG_ANIMATION_SECONDS, this.animationSeconds)
                .putInt(TAG_DATA, this.data.ordinal())
                .putString(TAG_DATA_FIELD, this.dataField)
                .putBoolean(TAG_IGNORE_ENTITIES, ignoreEntities)
                .putBoolean(TAG_INCLUDE_PLAYERS, includePlayers)
                .putFloat(TAG_INTEGRITY, integrity)
                .putBoolean(TAG_IS_POWERED, isPowered)
                .putByte(TAG_MIRROR, mirror.ordinal())
                .putByte(TAG_REDSTONE_SAVEMODE, redstoneSaveMode.ordinal())
                .putBoolean(TAG_REMOVE_BLOCKS, removeBlocks)
                .putByte(TAG_ROTATION, rotation.ordinal())
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
        return this.hasName() ? this.namedTag.getString(TAG_CUSTOM_NAME) : STRUCTURE_BLOCK;
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains(TAG_CUSTOM_NAME);
    }

    @Override
    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            this.namedTag.remove(TAG_CUSTOM_NAME);
        } else {
            this.namedTag.putString(TAG_CUSTOM_NAME, name);
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
        var editorData = packet.editorData;
        this.animationMode = editorData.getSettings().getAnimationMode();
        this.animationSeconds = editorData.getSettings().getAnimationSeconds();
        this.data = editorData.getType();
        this.dataField = editorData.getDataField();
        this.ignoreEntities = editorData.getSettings().isIgnoringEntities();
        this.includePlayers = editorData.isIncludingPlayers();
        this.integrity = editorData.getSettings().getIntegrityValue();
        this.mirror = editorData.getSettings().getMirror();
        this.redstoneSaveMode = editorData.getRedstoneSaveMode();
        this.removeBlocks = editorData.getSettings().isIgnoringBlocks();
        this.rotation = editorData.getSettings().getRotation();
        this.seed = editorData.getSettings().getIntegritySeed();
        this.showBoundingBox = editorData.isBoundingBoxVisible();
        this.structureName = editorData.getName();
        this.offset = editorData.getSettings().getOffset();
        this.size = editorData.getSettings().getSize();

        if(packet.powered) onPower();
    }

    public void onPower() {
        if(data == StructureBlockType.LOAD) placeStructure();
        else if(data == StructureBlockType.SAVE) saveStructure();
    }

    public void placeStructure() {
        if (structureName.isEmpty()) return;
        Structure structure = StructureAPI.load(structureName);

        if (structure == null) return;

        if(rotation != Rotation.NONE)
            structure = structure.rotate(rotation);

        if(mirror != StructureMirror.NONE)
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
