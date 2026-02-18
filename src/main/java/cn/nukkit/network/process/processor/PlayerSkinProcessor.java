package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.player.PlayerChangeSkinEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.PlayerSkinPacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Slf4j
public class PlayerSkinProcessor extends DataPacketProcessor<PlayerSkinPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerSkinPacket pk) {
        Player player = playerHandle.player;
        var serializedSkin = pk.getSkin();
        Skin skin = new Skin();
        skin.setSkinId(serializedSkin.getSkinId());
        skin.setPlayFabId(serializedSkin.getPlayFabId());
        skin.setSkinResourcePatch(serializedSkin.getSkinResourcePatch());
        skin.setGeometryData(serializedSkin.getGeometryData());
        skin.setAnimationData(serializedSkin.getAnimationData());
        skin.setPremium(serializedSkin.isPremium());
        skin.setPersona(serializedSkin.isPersona());
        skin.setCapeOnClassic(serializedSkin.isCapeOnClassic());
        skin.setCapeId(serializedSkin.getCapeId());
        skin.setFullSkinId(serializedSkin.getFullSkinId());
        skin.setArmSize(serializedSkin.getArmSize());
        skin.setSkinColor(serializedSkin.getSkinColor());
        skin.setTrusted(pk.isTrustedSkin());
        var skinData = serializedSkin.getSkinData();
        skin.setSkinData(new cn.nukkit.utils.SerializedImage(skinData.getWidth(), skinData.getHeight(), skinData.getImage()));
        var capeData = serializedSkin.getCapeData();
        skin.setCapeData(new cn.nukkit.utils.SerializedImage(capeData.getWidth(), capeData.getHeight(), capeData.getImage()));

        if (!skin.isValid()) {
            log.warn("{}: PlayerSkinPacket with invalid skin", playerHandle.getUsername());
            return;
        }

        if (player.getServer().getSettings().playerSettings().forceSkinTrusted()) {
            skin.setTrusted(true);
        }

        PlayerChangeSkinEvent playerChangeSkinEvent = new PlayerChangeSkinEvent(player, skin);
        var tooQuick = TimeUnit.SECONDS.toMillis(player.getServer().getSettings().playerSettings().skinChangeCooldown()) > System.currentTimeMillis() - player.lastSkinChange;
        if (tooQuick) {
            playerChangeSkinEvent.setCancelled(true);
            log.warn("Player {} change skin too quick!", playerHandle.getUsername());
        }
        player.getServer().getPluginManager().callEvent(playerChangeSkinEvent);
        if (!playerChangeSkinEvent.isCancelled()) {
            player.lastSkinChange = System.currentTimeMillis();
            player.setSkin(skin);
        }
    }

    @Override
    public Class<PlayerSkinPacket> getPacketClass() {
        return PlayerSkinPacket.class;
    }
}
