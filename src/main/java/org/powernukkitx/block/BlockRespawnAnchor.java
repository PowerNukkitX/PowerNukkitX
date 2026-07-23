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

package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.event.block.BlockExplosionPrimeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.lang.TranslationContainer;
import org.powernukkitx.level.Explosion;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.TextFormat;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author joserobjr
 * @since 2020-10-06
 */

public class BlockRespawnAnchor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESPAWN_ANCHOR, CommonBlockProperties.RESPAWN_ANCHOR_CHARGE);
    public static final BlockDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .hardness(50)
            .resistance(1200)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_DIAMOND)
            .canBePushed(false)
            .canBePulled(false)
            .canBeActivated(true)
            .canSilkTouch(true)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRespawnAnchor() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockRespawnAnchor(BlockState blockState) {
        super(blockState, DEFINITION);
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
            if (this.level.getGameRules().getBoolean(GameRule.RESPAWN_BLOCKS_EXPLODE)) {
                explode(player);
            }
            return true;
        }

        if (Objects.equals(player.getSpawn().left(), this)) {
            return false;
        }
        player.setSpawn(this, Player.SpawnPointType.BLOCK);
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
    public Item[] getDrops(Item item) {
        if (canHarvest(item)) {
            return new Item[]{Item.get(getId())};
        }
        return Item.EMPTY_ARRAY;
    }
}
