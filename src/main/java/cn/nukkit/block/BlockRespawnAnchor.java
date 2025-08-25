/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.BlockExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.types.SpawnPointType;
import cn.nukkit.utils.TextFormat;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author joserobjr
 * @since 2020-10-06
 */

public class BlockRespawnAnchor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESPAWN_ANCHOR, CommonBlockProperties.RESPAWN_ANCHOR_CHARGE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRespawnAnchor() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockRespawnAnchor(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Respawn Anchor";
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        int charge = getCharge();
        if (item.getBlockId().equals(BlockID.GLOWSTONE) && charge < CommonBlockProperties.RESPAWN_ANCHOR_CHARGE.getMax()) {
            if (player == null || !player.isCreative()) {
                item.count--;
            }

            setCharge(charge + 1);
            getLevel().setBlock(this, this);
            getLevel().addSound(this, Sound.RESPAWN_ANCHOR_CHARGE);
            return true;
        }

        if (player == null) {
            return false;
        }

        if (charge > 0) {
            return attemptToSetSpawn(player);
        } else {
            return false;
        }
    }

    protected boolean attemptToSetSpawn(@NotNull Player player) {
        if (this.level.getDimension() != Level.DIMENSION_NETHER) {
            if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                explode(player);
            }
            return true;
        }

        if (Objects.equals(player.getSpawn().left(), this)) {
            return false;
        }
        player.setSpawn(this, SpawnPointType.BLOCK);
        getLevel().addSound(this, Sound.RESPAWN_ANCHOR_SET_SPAWN);
        player.sendMessage(new TranslationContainer(TextFormat.GRAY + "%tile.respawn_anchor.respawnSet"));
        return true;
    }

    public void explode(Player player) {
        BlockExplosionPrimeEvent event = new BlockExplosionPrimeEvent(this, player, 5);
        event.setIncendiary(true);
        if (event.isCancelled()) {
            return;
        }

        level.setBlock(this, get(AIR));
        Explosion explosion = new Explosion(this, event.getForce(), this);
        explosion.setFireChance(event.getFireChance());
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }

    public int getCharge() {
        return getPropertyValue(CommonBlockProperties.RESPAWN_ANCHOR_CHARGE);
    }

    public void setCharge(int charge) {
        setPropertyValue(CommonBlockProperties.RESPAWN_ANCHOR_CHARGE, charge);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public double getResistance() {
        return 1200;
    }

    @Override
    public double getHardness() {
        return 50;
    }

    @Override
    public int getLightLevel() {
        return switch (getCharge()) {
            case 0 -> 0;
            case 1 -> 3;
            case 2 -> 7;
            default -> 15;
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            getLevel().addSound(this, Sound.RESPAWN_ANCHOR_DEPLETE);
            return type;
        }
        return super.onUpdate(type);
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (canHarvest(item)) {
            return new Item[]{Item.get(getId())};
        }
        return Item.EMPTY_ARRAY;
    }
}
