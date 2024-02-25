package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.DoubleChestInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashSet;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntityChest extends BlockEntitySpawnableContainer {
    protected DoubleChestInventory doubleInventory = null;

    public BlockEntityChest(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    protected ContainerInventory requireContainerInventory() {
        if (this.inventory == null) {
            return new ChestInventory(this);
        } else return inventory;
    }

    @Override
    public void close() {
        if (!closed) {
            unpair();

            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }

            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getRealInventory());
            }
            super.close();
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        String blockID = this.getBlock().getId();
        return blockID == Block.CHEST || blockID == Block.TRAPPED_CHEST;
    }

    public int getSize() {
        return 27;
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
                pair.createPair(this);
                pair.checkPairing();
            }

            if (pair.doubleInventory != null) {
                this.doubleInventory = pair.doubleInventory;
                this.namedTag.putBoolean("pairlead", false);
            } else if (this.doubleInventory == null) {
                this.namedTag.putBoolean("pairlead", true);
                if ((pair.x + ((int) pair.z << 15)) > (this.x + ((int) this.z << 15))) { //Order them correctly
                    this.doubleInventory = new DoubleChestInventory(pair, this);
                } else {
                    this.doubleInventory = new DoubleChestInventory(this, pair);
                }
            }
        } else {
            if (level.isChunkLoaded(this.namedTag.getInt("pairx") >> 4, this.namedTag.getInt("pairz") >> 4)) {
                this.doubleInventory = null;
                this.namedTag.remove("pairx");
                this.namedTag.remove("pairz");
                this.namedTag.remove("pairlead");
            }
        }
    }

    public boolean isPaired() {
        return this.namedTag.contains("pairx") && this.namedTag.contains("pairz");
    }

    public BlockEntityChest getPair() {
        if (this.isPaired()) {
            BlockEntity blockEntity = this.getLevel().getBlockEntityIfLoaded(new Vector3(this.namedTag.getInt("pairx"), this.y, this.namedTag.getInt("pairz")));
            if (blockEntity instanceof BlockEntityChest) {
                return (BlockEntityChest) blockEntity;
            }
        }

        return null;
    }

    public boolean pairWith(BlockEntityChest chest) {
        if (this.isPaired() || chest.isPaired() || this.getBlock().getId() != chest.getBlock().getId()) {
            return false;
        }

        this.createPair(chest);
        this.checkPairing();

        chest.spawnToAll();
        this.spawnToAll();

        return true;
    }

    public void createPair(BlockEntityChest chest) {
        this.namedTag.putInt("pairx", (int) chest.x);
        this.namedTag.putInt("pairz", (int) chest.z);
        chest.namedTag.putInt("pairx", (int) this.x);
        chest.namedTag.putInt("pairz", (int) this.z);
    }

    public boolean unpair() {
        if (!this.isPaired()) {
            return false;
        }

        BlockEntityChest chest = this.getPair();

        this.doubleInventory = null;
        this.namedTag.remove("pairx");
        this.namedTag.remove("pairz");

        this.spawnToAll();

        if (chest != null) {
            chest.namedTag.remove("pairx");
            chest.namedTag.remove("pairz");
            chest.doubleInventory = null;
            chest.checkPairing();
            chest.spawnToAll();
        }
        this.checkPairing();

        return true;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag spawnCompound = super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable());
        if (this.isPaired()) {
            spawnCompound.putBoolean("pairlead", this.namedTag.getBoolean("pairlead"))
                    .putInt("pairx", this.namedTag.getInt("pairx"))
                    .putInt("pairz", this.namedTag.getInt("pairz"));
        }
        if (this.hasName()) {
            spawnCompound.put("CustomName", this.namedTag.get("CustomName"));
        }
        return spawnCompound;
    }

    @Override
    public CompoundTag getCleanedNBT() {
        return super.getCleanedNBT().remove("pairx").remove("pairz");
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Chest";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        unpair();
        super.onBreak(isSilkTouch);
    }
}
