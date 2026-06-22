package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

/**
 * @author xtypr
 * @since 2015/12/27
 */
public class PotionSplashParticle extends GenericParticle {

    public PotionSplashParticle(Vector3 pos) {
        this(pos, 0);
    }

    public PotionSplashParticle(Vector3 pos, int data) {
        super(pos, LevelEvent.PARTICLE_POTION_SPLASH, data);
    }

    public PotionSplashParticle(Vector3 pos, BlockColor blockColor) {
        //alpha is ignored
        this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue());
    }

    public PotionSplashParticle(Vector3 pos, int r, int g, int b) {
        this(pos, r, g, b, 0x00);
    }

    protected PotionSplashParticle(Vector3 pos, int r, int g, int b, int a) {
        this(pos, ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff));
    }
}