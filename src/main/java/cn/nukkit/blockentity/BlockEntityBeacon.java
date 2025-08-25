package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Map;

/**
 * @author Rover656
 */
public class BlockEntityBeacon extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    protected BeaconInventory inventory;

    public BlockEntityBeacon(IChunk chunk, CompoundTag nbt) {
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
        if (!namedTag.contains("Lock")) {
            namedTag.putString("Lock", "");
        }

        if (!namedTag.contains("Levels")) {
            namedTag.putInt("Levels", 0);
        }

        if (!namedTag.contains("Primary")) {
            namedTag.putInt("Primary", 0);
        }

        if (!namedTag.contains("Secondary")) {
            namedTag.putInt("Secondary", 0);
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        String blockID = getBlock().getId();
        return blockID == Block.BEACON;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putString("Lock", this.namedTag.getString("Lock"))
                .putInt("Levels", this.namedTag.getInt("Levels"))
                .putInt("primary", this.namedTag.getInt("Primary"))
                .putInt("secondary", this.namedTag.getInt("Secondary"));
    }

    private long currentTick = 0;

    @Override
    public boolean onUpdate() {
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
        for (int y = tileY + 1; y <= 255; y++) {
            Block test = level.getBlock(tileX, y, tileZ);
            if (!test.isTransparent()) {
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
        return namedTag.getInt("Levels");
    }

    public void setPowerLevel(int level) {
        int currentLevel = getPowerLevel();
        if (level != currentLevel) {
            namedTag.putInt("Levels", level);
            setDirty();
            this.spawnToAll();
        }
    }

    public int getPrimaryPower() {
        return namedTag.getInt("Primary");
    }

    public void setPrimaryPower(int power) {
        int currentPower = getPrimaryPower();
        if (power != currentPower) {
            namedTag.putInt("Primary", power);
            setDirty();
            this.spawnToAll();
        }
    }

    public int getSecondaryPower() {
        return namedTag.getInt("Secondary");
    }

    public void setSecondaryPower(int power) {
        int currentPower = getSecondaryPower();
        if (power != currentPower) {
            namedTag.putInt("Secondary", power);
            setDirty();
            this.spawnToAll();
        }
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
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

    private static boolean isPrimaryAllowed(int primary, int powerLevel) {
        return ((primary == EffectType.SPEED.id() || primary == EffectType.HASTE.id()) && powerLevel >= 1) ||
                ((primary == EffectType.RESISTANCE.id() || primary == EffectType.JUMP_BOOST.id()) && powerLevel >= 2) ||
                (primary == EffectType.STRENGTH.id() && powerLevel >= 3);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Beacon";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    @Override
    public BeaconInventory getInventory() {
        return inventory;
    }
}
