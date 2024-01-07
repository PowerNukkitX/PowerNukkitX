package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashSet;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntityChest extends BlockEntitySpawnableContainer implements BlockEntityNameable {

    public BlockEntityChest(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected ContainerInventory requireContainerInventory() {
        return new ChestInventory(this);
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
       /* if (this.doubleInventory == null && this.isPaired()) {
            this.checkPairing();
        }

        return this.doubleInventory != null ? this.doubleInventory : this.inventory;*/
        return null;
    }

    public ChestInventory getRealInventory() {
        return (ChestInventory) inventory;
    }

    protected void checkPairing() {
        /*BlockEntityChest pair = this.getPair();

        if (pair != null) {
            if (!pair.isPaired()) {
                pair.createPair(this);
                pair.checkPairing();
            }

            if (pair.doubleInventory != null) {
                this.doubleInventory = pair.doubleInventory;
                this.namedTag.putBoolean("pairlead",false);
            } else if (this.doubleInventory == null) {
                this.namedTag.putBoolean("pairlead",true);
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
        }*/
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

        this.namedTag.remove("pairx");
        this.namedTag.remove("pairz");

        this.spawnToAll();

        if (chest != null) {
            chest.namedTag.remove("pairx");
            chest.namedTag.remove("pairz");
            chest.checkPairing();
            chest.spawnToAll();
        }
        this.checkPairing();

        return true;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c;
        if (this.isPaired()) {
            c = new CompoundTag()
                    .putString("id", BlockEntity.CHEST)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z)
                    .putBoolean("pairlead", this.namedTag.getBoolean("pairlead"))
                    .putInt("pairx", this.namedTag.getInt("pairx"))
                    .putInt("pairz", this.namedTag.getInt("pairz"));
        } else {
            c = new CompoundTag()
                    .putString("id", BlockEntity.CHEST)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z);
        }

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
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
    public void onBreak() {
        unpair();
        super.onBreak();
    }
}
