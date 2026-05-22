package cn.nukkit.blockentity;

import cn.nukkit.block.BlockChest;
import cn.nukkit.block.copper.chest.BlockCopperChest;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.DoubleChestInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

import java.util.Objects;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntityChest extends BlockEntitySpawnableContainer {
    protected DoubleChestInventory doubleInventory = null;

    public BlockEntityChest(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    protected ContainerInventory requireContainerInventory() {
        return Objects.requireNonNullElseGet(this.inventory, () -> new ChestInventory(this));
    }

    @Override
    public void close() {
        if (!closed) {
            unpair();
            this.getInventory().getViewers().forEach(p -> p.removeWindow(this.getInventory()));
            this.getRealInventory().getViewers().forEach(p -> p.removeWindow(this.getRealInventory()));

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

    @Override
    public boolean isBlockEntityValid() {
        return getBlock() instanceof BlockChest;
    }

    public int getSize() {
        return this.doubleInventory != null ? this.doubleInventory.getSize() : this.inventory.getSize();
    }

    @Override
    public BaseInventory getInventory() {
        if (this.doubleInventory == null && this.isPaired()) {
            this.checkPairing();
        }

        return this.doubleInventory != null ? this.doubleInventory : this.inventory;
    }

    public ChestInventory getRealInventory() {
        return (ChestInventory) inventory;
    }

    protected void checkPairing() {
        BlockEntityChest pair = this.getPair();

        if (pair != null) {
            if (!pair.isPaired()) {
                pair.pairWith(this);
                this.pairWith(pair);
            }

            if (pair.doubleInventory != null) {
                this.doubleInventory = pair.doubleInventory;
                this.nbt.putBoolean("pairlead", false);
            } else if (this.doubleInventory == null) {
                this.nbt.putBoolean("pairlead", true);
                if ((pair.x + ((int) pair.z << 15)) > (this.x + ((int) this.z << 15))) { //Order them correctly
                    this.doubleInventory = new DoubleChestInventory(pair, this);
                } else {
                    this.doubleInventory = new DoubleChestInventory(this, pair);
                }
            }
        } else {
            if (level.isChunkLoaded(this.getNbt().getInt("pairx") >> 4, this.getNbt().getInt("pairz") >> 4)) {
                this.doubleInventory = null;
                this.nbt.remove("pairx");
                this.nbt.remove("pairz");
                this.nbt.remove("pairlead");
            }
        }
    }

    public boolean isPaired() {
        return this.nbt.containsKey("pairx") && this.nbt.containsKey("pairz");
    }

    public BlockEntityChest getPair() {
        if (this.isPaired()) {
            BlockEntity blockEntity = this.getLevel().getBlockEntityIfLoaded(new Vector3(this.getNbt().getInt("pairx"), this.y, this.getNbt().getInt("pairz")));
            if (blockEntity instanceof BlockEntityChest) {
                return (BlockEntityChest) blockEntity;
            }
        }

        return null;
    }

    public boolean pairWith(BlockEntityChest chest) {
        if ((this.getBlock() instanceof BlockCopperChest) != (chest.getBlock() instanceof BlockCopperChest)) {
            return false;
        }

        final NbtMap nbtMap = getNbt();
        if (this.isPaired()) {
            int x1 = nbtMap.getInt("pairx");
            int z1 = nbtMap.getInt("pairz");
            if (!(chest.x == x1 && chest.z == z1)) {
                return false;
            }
        }

        if (chest.isPaired()) {
            int x2 = chest.getNbt().getInt("pairx");
            int z2 = chest.getNbt().getInt("pairz");
            if (!(this.x == x2 && this.z == z2)) {
                return false;
            }
        }

        this.createPair(chest);
        this.checkPairing();

        chest.spawnToAll();
        this.spawnToAll();

        return true;
    }

    public void createPair(BlockEntityChest chest) {
        this.nbt.putInt("pairx", (int) chest.x)
                .putInt("pairz", (int) chest.z);
        chest.nbt.putInt("pairx", (int) this.x)
                .putInt("pairz", (int) this.z);
    }

    public boolean unpair() {
        if (!this.isPaired()) {
            return false;
        }
        BlockEntityChest chest = this.getPair();

        this.doubleInventory = null;
        this.nbt.remove("pairx");
        this.nbt.remove("pairz");

        this.spawnToAll();

        if (chest != null) {
            chest.nbt.remove("pairx");
            chest.nbt.remove("pairz");
            chest.doubleInventory = null;
            chest.checkPairing();
            chest.spawnToAll();
        }
        this.checkPairing();

        return true;
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder spawnCompound = super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", this.isMovable());
        if (this.isPaired()) {
            spawnCompound.putBoolean("pairlead", this.getNbt().getBoolean("pairlead"))
                    .putInt("pairx", this.getNbt().getInt("pairx"))
                    .putInt("pairz", this.getNbt().getInt("pairz"));
        }
        if (this.hasName()) {
            spawnCompound.putString("CustomName", this.getNbt().getString("CustomName"));
        }
        return spawnCompound.build();
    }

    @Override
    public NbtMap getCleanedNBT() {
        final NbtMapBuilder builder = super.getCleanedNBT().toBuilder();
        builder.remove("pairx");
        builder.remove("pairz");
        return builder.build();
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Chest";
    }

    @Override
    public boolean hasName() {
        return this.nbt.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.nbt.remove("CustomName");
            return;
        }
        this.nbt.putString("CustomName", name);
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        unpair();
        super.onBreak(isSilkTouch);
    }
}
