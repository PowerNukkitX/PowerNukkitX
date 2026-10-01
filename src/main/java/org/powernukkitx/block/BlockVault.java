package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.block.property.enums.VaultState;
import org.powernukkitx.blockentity.BlockEntityID;
import org.powernukkitx.blockentity.BlockEntityVault;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

public class BlockVault extends Block implements BlockEntityHolder<BlockEntityVault>, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(VAULT, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OMINOUS, CommonBlockProperties.VAULT_STATE);
    public static final BlockDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .hardness(50)
            .resistance(50)
            .canBePushed(false)
            .canBePulled(false)
            .canBeActivated(true)
            .build();

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        this.getLevel().setBlock(this, this, false);
        this.getOrCreateBlockEntity().scheduleUpdate();
        return true;
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null) {
            return false;
        }

        BlockEntityVault vault = getOrCreateBlockEntity();
        boolean ominous = getPropertyValue(CommonBlockProperties.OMINOUS);
        if (!vault.canPlayerOpen(player)) {
            this.getLevel().addLevelSoundEvent(this, SoundEvent.VAULT_REJECT_REWARDED_PLAYER);
            return false;
        }
        if (!vault.matchesKey(item, ominous)) {
            this.getLevel().addLevelSoundEvent(this, SoundEvent.VAULT_INSERT_ITEM_FAIL);
            return false;
        }

        RandomSourceProvider random = RandomSourceProvider.create(getLevel().getSeed() ^ player.getUniqueId().getLeastSignificantBits() ^ getLevel().getTick());
        if (!vault.populateItemsToEject(random, ominous)) {
            return false;
        }

        if (!player.isCreative()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }

        vault.addRewardedPlayer(player.getUniqueId().toString());
        vault.spawnToAll();

        setVaultState(VaultState.UNLOCKING);

        vault.spawnToAll();
        vault.scheduleUpdate();
        return true;
    }

    public void setVaultState(VaultState state) {
        setPropertyValue(CommonBlockProperties.VAULT_STATE, state);
        getLevel().setBlock(this, this, false, false);
        this.getLevel().addLevelSoundEvent(this, switch (state) {
            case ACTIVE -> SoundEvent.VAULT_ACTIVATE;
            case INACTIVE -> SoundEvent.VAULT_DEACTIVATE;
            case EJECTING -> SoundEvent.VAULT_OPEN_SHUTTER;
            case UNLOCKING -> SoundEvent.VAULT_CLOSE_SHUTTER;
        });
    }

    public VaultState getVaultState() {
        return this.getProperties().getPropertyValue(this.getBlockState().specialValue(), CommonBlockProperties.VAULT_STATE);
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockVault(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public @NotNull Class<? extends BlockEntityVault> getBlockEntityClass() {
        return BlockEntityVault.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntityID.VAULT;
    }
}
