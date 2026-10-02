package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockChest;
import org.powernukkitx.block.copper.chest.BlockCopperChest;
import org.powernukkitx.inventory.BaseInventory;
import org.powernukkitx.inventory.ChestInventory;
import org.powernukkitx.inventory.ContainerInventory;
import org.powernukkitx.inventory.DoubleChestInventory;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket;

import java.util.HashSet;
import java.util.Objects;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntityChest extends BlockEntitySpawnableContainer {
    protected DoubleChestInventory doubleInventory = null;

    public BlockEntityChest(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putByte("Findable", this.nbt.getByte("Findable"));
    }

    @Override
    protected ContainerInventory requireContainerInventory() {
        return Objects.requireNonNullElseGet(this.inventory, () -> new ChestInventory(this));
    }

    @Override
    public void close() {
        if (!closed) {
            DoubleChestInventory dblInv = this.doubleInventory;
            if (dblInv != null) {
                for (Player player : new HashSet<>(dblInv.getViewers())) {
                    player.removeWindow(dblInv);
                }
            }
            this.clearDoubleInventory();
            ChestInventory realInv = this.getRealInventory();
            for (Player player : new HashSet<>(realInv.getViewers())) {
                player.removeWindow(realInv);
            }

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

    private void clearDoubleInventory() {
        this.doubleInventory = null;
        this.getRealInventory().setDoubleInventory(null);

        BlockEntityChest pair = this.getPair();
        if (pair != null) {
            pair.doubleInventory = null;
            pair.getRealInventory().setDoubleInventory(null);
        }
    }

    private BlockEntityChest getPairLead() {
        if (!this.isPaired() || this.nbt.getBoolean("pairlead")) {
            return this;
        }
        BlockEntityChest pair = this.getPair();
        return pair != null && pair.nbt.getBoolean("pairlead") ? pair : this;
    }

    protected void checkPairing() {
        BlockEntityChest pair = this.getPair();

        if (pair != null) {
            if (!pair.isPaired()) {
                pair.nbt.putBoolean("pairlead", !this.nbt.getBoolean("pairlead"))
                        .putInt("pairx", (int) this.x)
                        .putInt("pairz", (int) this.z);
                pair.nbt.remove("forceunpair");
                pair.setDirty();
            }

            if (pair.doubleInventory != null) {
                this.doubleInventory = pair.doubleInventory;
            } else if (this.doubleInventory == null) {
                if (this.nbt.getBoolean("pairlead")) {
                    pair.nbt.putBoolean("pairlead", false);
                    this.doubleInventory = new DoubleChestInventory(this, pair);
                } else if (pair.nbt.getBoolean("pairlead")) {
                    this.doubleInventory = new DoubleChestInventory(pair, this);
                }
                pair.doubleInventory = this.doubleInventory;
            }
        } else {
            int pairChunkX = this.nbt.getInt("pairx") >> 4;
            int pairChunkZ = this.nbt.getInt("pairz") >> 4;
            IChunk pairChunk = level.getChunkIfLoaded(pairChunkX, pairChunkZ);
            if (pairChunk != null && pairChunk.isInitiated()) {
                this.clearDoubleInventory();
                this.nbt.remove("pairx", "pairz", "pairlead");
                this.setDirty();
            }
        }
    }

    public boolean isPaired() {
        return this.nbt.contains("pairx") && this.nbt.contains("pairz");
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

        final CompoundTag nbtMap = getNbt();
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

        if (!this.isPaired() && !chest.isPaired()) {
            this.createPair(chest);
        } else if (this.isPaired() != chest.isPaired()) {
            return false;
        }
        this.checkPairing();

        chest.spawnToAll();
        this.spawnToAll();

        return true;
    }

    public void createPair(BlockEntityChest chest) {
        this.nbt.putBoolean("pairlead", true)
                .putInt("pairx", (int) chest.x)
                .putInt("pairz", (int) chest.z);
        chest.nbt.putBoolean("pairlead", false)
                .putInt("pairx", (int) this.x)
                .putInt("pairz", (int) this.z);
        this.nbt.remove("forceunpair");
        chest.nbt.remove("forceunpair");
        this.setDirty();
        chest.setDirty();
    }

    public boolean unpair() {
        if (!this.isPaired()) {
            return false;
        }
        BlockEntityChest chest = this.getPair();
        DoubleChestInventory dblInv = this.doubleInventory != null ? this.doubleInventory : chest != null ? chest.doubleInventory : null;

        if (dblInv != null) {
            for (Player player : new HashSet<>(dblInv.getViewers())) {
                player.removeWindow(dblInv);
            }
        }

        this.clearDoubleInventory();
        this.nbt.remove("pairx", "pairz", "pairlead", "forceunpair");
        if (chest != null) {
            this.nbt.putBoolean("forceunpair", true);
        }
        this.setDirty();
        this.spawnToAll();

        if (chest != null) {
            chest.nbt.remove("pairx", "pairz", "pairlead", "forceunpair");
            chest.nbt.putBoolean("forceunpair", true);
            chest.setDirty();
            chest.spawnToAll();
        }

        return true;
    }

    public void prepareForPistonMove() {
        if (this.isPaired()) {
            this.unpair();
        }
    }

    /**
     * Broadcasts the chest lid state and sound.
     *
     * @param open whether the lid is open
     */
    public void broadcastLidState(boolean open) {
        BlockEntityChest lead = this.getPairLead();
        var level = lead.getLevel();
        if (level == null) {
            return;
        }

        final BlockEventPacket packet = new BlockEventPacket();
        packet.setBlockPosition(Vector3i.from(lead.x, lead.y, lead.z));
        packet.setEventType(1);
        packet.setEventValue(open ? 1 : 0);

        BlockEntityChest pair = lead.getPair();
        Vector3 soundPosition = pair != null
                ? new Vector3((lead.x + pair.x) * 0.5 + 0.5, lead.y + 0.5, (lead.z + pair.z) * 0.5 + 0.5)
                : lead.add(0.5, 0.5, 0.5);
        level.addSound(soundPosition, open ? Sound.RANDOM_CHESTOPEN : Sound.RANDOM_CHESTCLOSED);
        level.addChunkPacket((int) lead.x >> 4, (int) lead.z >> 4, packet);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag spawnCompound = super.getSpawnCompound();
        if (this.isPaired()) {
            spawnCompound.putBoolean("pairlead", this.getNbt().getBoolean("pairlead"))
                    .putInt("pairx", this.getNbt().getInt("pairx"))
                    .putInt("pairz", this.getNbt().getInt("pairz"));
        }
        if (this.hasName()) {
            spawnCompound.putString("CustomName", this.getNbt().getString("CustomName"));
        }
        return spawnCompound;
    }

    @Override
    public CompoundTag getCleanedNBT() {
        final CompoundTag cleaned = super.getCleanedNBT();
        if (cleaned != null) {
            cleaned.remove("pairx", "pairz");
        }
        return cleaned;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Chest";
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains("CustomName");
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
