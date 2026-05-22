package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;

import java.util.ArrayList;
import java.util.List;


public class BlockEntityBell extends BlockEntitySpawnable {
    private boolean ringing;
    private int direction;
    private int ticks;
    public final List<Player> spawnExceptions = new ArrayList<>(2);


    public BlockEntityBell(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!nbt.containsKey("Ringing") || !(nbt.get("Ringing") instanceof Byte)) {
            ringing = false;
        } else {
            ringing = getNbt().getBoolean("Ringing");
        }

        if (!nbt.containsKey("Direction") || !(nbt.get("Direction") instanceof Integer)) {
            direction = 255;
        } else {
            direction = getNbt().getInt("Direction");
        }

        if (!nbt.containsKey("Ticks") || !(nbt.get("Ticks") instanceof Integer)) {
            ticks = 0;
        } else {
            ticks = getNbt().getInt("Ticks");
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putBoolean("Ringing", ringing)
                .putInt("Direction", direction)
                .putInt("Ticks", ticks)
                .build();
    }

    @Override
    public boolean onUpdate() {
        if (ringing) {
            if (ticks == 0) {
                level.addSound(this, Sound.BLOCK_BELL_HIT);
                spawnToAllWithExceptions();
                spawnExceptions.clear();
            } else if (ticks >= 50) {
                ringing = false;
                ticks = 0;
                spawnToAllWithExceptions();
                spawnExceptions.clear();
                return false;
            }
            //spawnToAll();
            ticks++;
            return true;
        } else if (ticks > 0) {
            ticks = 0;
            spawnToAllWithExceptions();
            spawnExceptions.clear();
        }

        return false;
    }

    private void spawnToAllWithExceptions() {
        if (this.closed) {
            return;
        }

        for (Player player : this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.spawned && !spawnExceptions.contains(player)) {
                this.spawnTo(player);
            }
        }
    }

    public boolean isRinging() {
        return ringing;
    }

    public void setRinging(boolean ringing) {
        if (this.level != null && this.ringing != ringing) {
            this.ringing = ringing;
            scheduleUpdate();
        }
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Ringing", this.ringing)
                .putInt("Direction", this.direction)
                .putInt("Ticks", this.ticks)
                .build();
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.BELL;
    }
}
