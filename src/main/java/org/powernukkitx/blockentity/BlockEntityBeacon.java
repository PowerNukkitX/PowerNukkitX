package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.inventory.BeaconInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.nbt.NbtMap;

import java.util.Map;

/**
 * @author Rover656
 */
public class BlockEntityBeacon extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    protected BeaconInventory inventory;

    private int powerLevel;
    private int primaryPower;
    private int secondaryPower;

    public BlockEntityBeacon(IChunk chunk, CompoundTag  nbt) {
        super(chunk, nbt);
        inventory = new BeaconInventory(this);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();

        this.powerLevel = 0;

        if (!this.nbt.containsInt("primary")) {
            this.nbt.putInt("primary", 0);
        }

        if (!this.nbt.containsInt("secondary")) {
            this.nbt.putInt("secondary", 0);
        }

        this.primaryPower = this.nbt.getInt("primary");
        this.secondaryPower = this.nbt.getInt("secondary");
    }

    @Override
    public boolean isBlockEntityValid() {
        String blockID = getBlock().getId();
        return blockID.equals(Block.BEACON);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putInt("Levels", this.powerLevel)
                .putInt("primary", this.primaryPower)
                .putInt("secondary", this.secondaryPower);
    }

    private long currentTick = 0;

    @Override
    public boolean onUpdate() {
        if (!isBlockEntityValid()) {
            this.close();
        }
        if (closed) return true;
        //Only apply effects every 4 secs
        if (currentTick++ % 80 != 0) {
            return true;
        }

        int oldPowerLevel = this.getPowerLevel();
        //Get the power level based on the pyramid
        setPowerLevel(calculatePowerLevel());
        int newPowerLevel = this.getPowerLevel();

        //Skip beacons that do not have a pyramid or sky access
        if (newPowerLevel < 1 || !hasSkyAccess()) {
            if (oldPowerLevel > 0) {
                this.getLevel().addSound(this, Sound.BEACON_DEACTIVATE);
            }
            return true;
        } else if (oldPowerLevel < 1) {
            this.getLevel().addSound(this, Sound.BEACON_ACTIVATE);
        } else {
            this.getLevel().addSound(this, Sound.BEACON_AMBIENT);
        }

        //Get all players in game
        Map<Long, Player> players = this.level.getPlayers();

        //Calculate vars for beacon power
        int range = 10 + getPowerLevel() * 10;
        int duration = 9 + getPowerLevel() * 2;

        if (!isPrimaryAllowed(getPrimaryPower(), getPowerLevel())) {
            return true;
        }

        for (Map.Entry<Long, Player> entry : players.entrySet()) {
            Player p = entry.getValue();

            //If the player is in range
            if (p.distance(this) < range) {
                Effect e;

                if (getPrimaryPower() != 0) {
                    //Apply the primary power
                    e = Effect.get(getPrimaryPower());

                    //Set duration
                    e.setDuration(duration * 20);

                    //If secondary is selected as the primary too, apply 2 amplification
                    if (getPowerLevel() == POWER_LEVEL_MAX && getSecondaryPower() == getPrimaryPower()) {
                        e.setAmplifier(1);
                    }

                    //Add the effect
                    p.addEffect(e);
                }

                //If we have a secondary power as regen, apply it
                if (getPowerLevel() == POWER_LEVEL_MAX && getSecondaryPower() == EffectType.REGENERATION.id()) {
                    //Get the regen effect
                    e = Effect.get(EffectType.REGENERATION);

                    //Set duration
                    e.setDuration(duration * 20);

                    //Add effect
                    p.addEffect(e);
                }
            }
        }

        return true;
    }

    private static final int POWER_LEVEL_MAX = 4;

    private boolean hasSkyAccess() {
        int tileX = getFloorX();
        int tileY = getFloorY();
        int tileZ = getFloorZ();

        //Check every block from our y coord to the top of the world
        for (int y = tileY + 1; y < level.getMaxHeight(); y++) {
            Block test = level.getBlock(tileX, y, tileZ);
            if (test.getLightFilter() > 1 && !test.getId().equals(Block.BEDROCK)) {
                //There is no sky access
                return false;
            }
        }

        return true;
    }

    private int calculatePowerLevel() {
        int tileX = getFloorX();
        int tileY = getFloorY();
        int tileZ = getFloorZ();

        //The power level that we're testing for
        for (int powerLevel = 1; powerLevel <= POWER_LEVEL_MAX; powerLevel++) {
            int queryY = tileY - powerLevel; //Layer below the beacon block

            for (int queryX = tileX - powerLevel; queryX <= tileX + powerLevel; queryX++) {
                for (int queryZ = tileZ - powerLevel; queryZ <= tileZ + powerLevel; queryZ++) {

                    String testBlockId = level.getBlockIdAt(queryX, queryY, queryZ);
                    if (
                            testBlockId != Block.IRON_BLOCK &&
                                    testBlockId != Block.GOLD_BLOCK &&
                                    testBlockId != Block.EMERALD_BLOCK &&
                                    testBlockId != Block.DIAMOND_BLOCK &&
                                    testBlockId != Block.NETHERITE_BLOCK
                    ) {
                        return powerLevel - 1;
                    }

                }
            }
        }

        return POWER_LEVEL_MAX;
    }

    public int getPowerLevel() {
        return this.powerLevel;
    }

    public void setPowerLevel(int level) {
        if (level != this.powerLevel) {
            this.powerLevel = level;
            this.spawnToAll();
        }
    }

    public int getPrimaryPower() {
        return this.primaryPower;
    }

    public void setPrimaryPower(int power) {
        if (power != this.primaryPower) {
            this.primaryPower = power;
            this.nbt.putInt("primary", power);
            setDirty();
            this.spawnToAll();
        }
    }

    public int getSecondaryPower() {
        return this.secondaryPower;
    }

    public void setSecondaryPower(int power) {
        if (power != this.secondaryPower) {
            this.secondaryPower = power;
            this.nbt.putInt("secondary", power);
            setDirty();
            this.spawnToAll();
        }
    }

    @Override
    public boolean updateCompoundTag(NbtMap nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.BEACON)) {
            return false;
        }

        int primary = nbt.getInt("primary");
        if (!isPrimaryAllowed(primary, this.getPowerLevel())) {
            return false;
        }

        int secondary = nbt.getInt("secondary");
        if (secondary != 0 && secondary != primary && secondary != EffectType.REGENERATION.id()) {
            return false;
        }

        this.setPrimaryPower(primary);
        this.setSecondaryPower(secondary);

        this.getLevel().addSound(this, Sound.BEACON_POWER);

        BeaconInventory inv = getInventory();

        inv.setItem(0, Item.AIR);
        return true;
    }

    public static boolean isPrimaryAllowed(int primary, int powerLevel) {
        return ((primary == EffectType.SPEED.id() || primary == EffectType.HASTE.id()) && powerLevel >= 1) ||
                ((primary == EffectType.RESISTANCE.id() || primary == EffectType.JUMP_BOOST.id()) && powerLevel >= 2) ||
                (primary == EffectType.STRENGTH.id() && powerLevel >= 3);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Beacon";
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.nbt.remove("CustomName");
            return;
        }

        this.nbt.putString("CustomName", name);
    }

    @Override
    public BeaconInventory getInventory() {
        return inventory;
    }
}
