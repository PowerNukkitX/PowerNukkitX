package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.RiderJumpPacket;
import org.jetbrains.annotations.NotNull;


public class RiderJumpProcessor extends DataPacketProcessor<RiderJumpPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RiderJumpPacket pk) {
        if (playerHandle.player.riding instanceof EntityHorse horse && horse.isAlive() && !horse.getJumping().get()) {
            float maxMotionY = horse.getClientMaxJumpHeight();
            double motion = maxMotionY * (Math.max(1, pk.unknown) / 100.0);
            horse.getJumping().set(true);
            horse.move(0, motion, 0);
            //避免onGround不更新
            horse.motionX = 0;
            horse.motionY = 0;
            horse.motionZ = 0;
            horse.setDataFlag(EntityFlag.STANDING);
        }
    }

    @Override
    public int getPacketId() {
        return RiderJumpPacket.NETWORK_ID;
    }
}
