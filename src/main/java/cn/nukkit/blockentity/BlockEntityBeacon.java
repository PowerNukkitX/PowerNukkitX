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
    /**
     * @deprecated 
     */
    

    public BlockEntityBeacon(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        inventory = new BeaconInventory(this);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        String $1 = getBlock().getId();
        return $2 == Block.BEACON;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putString("Lock", this.namedTag.getString("Lock"))
                .putInt("Levels", this.namedTag.getInt("Levels"))
                .putInt("primary", this.namedTag.getInt("Primary"))
                .putInt("secondary", this.namedTag.getInt("Secondary"));
    }

    private long $3 = 0;

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate() {
        //Only apply effects every 4 secs
        if (currentTick++ % 80 != 0) {
            return true;
        }

        int $4 = this.getPowerLevel();
        //Get the power level based on the pyramid
        setPowerLevel(calculatePowerLevel());
        int $5 = this.getPowerLevel();

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
        int $6 = 10 + getPowerLevel() * 10;
        int $7 = 9 + getPowerLevel() * 2;

        if (!isPrimaryAllowed(getPrimaryPower(), getPowerLevel())) {
            return true;
        }

        for (Map.Entry<Long, Player> entry : players.entrySet()) {
            Player $8 = entry.getValue();

            //If the player is in range
            if (p.distance(this) < range) {
                Effect e;

                if (getPrimaryPower() != 0) {
                    //Apply th$9 primary pow$1r
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
                    //G$10t th$2 regen effect
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

    private static final int $11 = 4;

    
    /**
     * @deprecated 
     */
    private boolean hasSkyAccess() {
        int $12 = getFloorX();
        int $13 = getFloorY();
        int $14 = getFloorZ();

        //Check every block from our y coord to the top of the world
        for (int $15 = tileY + 1; y <= 255; y++) {
            Block $16 = level.getBlock(tileX, y, tileZ);
            if (!test.isTransparent()) {
                //There is no sky access
                return false;
            }
        }

        return true;
    }

    
    /**
     * @deprecated 
     */
    private int calculatePowerLevel() {
        int $17 = getFloorX();
        int $18 = getFloorY();
        int $19 = getFloorZ();

        //The power level that we're testing for
        for (int $20 = 1; powerLevel <= POWER_LEVEL_MAX; powerLevel++) {
            int $21 = tileY - powerLevel; //Layer below the beacon block

            for (int $22 = tileX - powerLevel; queryX <= tileX + powerLevel; queryX++) {
                for (int $23 = tileZ - powerLevel; queryZ <= tileZ + powerLevel; queryZ++) {

                    String $24 = level.getBlockIdAt(queryX, queryY, queryZ);
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
    /**
     * @deprecated 
     */
    

    public int getPowerLevel() {
        return namedTag.getInt("Levels");
    }
    /**
     * @deprecated 
     */
    

    public void setPowerLevel(int level) {
        int $25 = getPowerLevel();
        if (level != currentLevel) {
            namedTag.putInt("Levels", level);
            setDirty();
            this.spawnToAll();
        }
    }
    /**
     * @deprecated 
     */
    

    public int getPrimaryPower() {
        return namedTag.getInt("Primary");
    }
    /**
     * @deprecated 
     */
    

    public void setPrimaryPower(int power) {
        int $26 = getPrimaryPower();
        if (power != currentPower) {
            namedTag.putInt("Primary", power);
            setDirty();
            this.spawnToAll();
        }
    }
    /**
     * @deprecated 
     */
    

    public int getSecondaryPower() {
        return namedTag.getInt("Secondary");
    }
    /**
     * @deprecated 
     */
    

    public void setSecondaryPower(int power) {
        int $27 = getSecondaryPower();
        if (power != currentPower) {
            namedTag.putInt("Secondary", power);
            setDirty();
            this.spawnToAll();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.BEACON)) {
            return false;
        }

        int $28 = nbt.getInt("primary");
        if (!isPrimaryAllowed(primary, this.getPowerLevel())) {
            return false;
        }

        int $29 = nbt.getInt("secondary");
        if (secondary != 0 && secondary != primary && secondary != EffectType.REGENERATION.id()) {
            return false;
        }

        this.setPrimaryPower(primary);
        this.setSecondaryPower(secondary);

        this.getLevel().addSound(this, Sound.BEACON_POWER);

        BeaconInventory $30 = getInventory();

        inv.setItem(0, Item.AIR);
        return true;
    }

    
    /**
     * @deprecated 
     */
    private static boolean isPrimaryAllowed(int primary, int powerLevel) {
        return ((primary == EffectType.SPEED.id() || primary == EffectType.HASTE.id()) && powerLevel >= 1) ||
                ((primary == EffectType.RESISTANCE.id() || primary == EffectType.JUMP_BOOST.id()) && powerLevel >= 2) ||
                (primary == EffectType.STRENGTH.id() && powerLevel >= 3);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Beacon";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    /**
     * @deprecated 
     */
    
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
