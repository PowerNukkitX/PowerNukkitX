package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockVector3;
import lombok.ToString;


@ToString
public class AnvilDamagePacket extends DataPacket {


    @Override
    public byte pid() {
        return ProtocolInfo.ANVIL_DAMAGE_PACKET;
    }

    @Override
    public void decode() {
        this.damage = this.getByte();
        BlockVector3 vec = this.getBlockVector3();
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    @Override
    public void encode() {

    }
}
