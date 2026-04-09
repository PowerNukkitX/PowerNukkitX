package cn.nukkit.utils.firework;

import cn.nukkit.item.ItemFireworkRocket.FireworkExplosion.ExplosionType;
import cn.nukkit.utils.DyeColor;
import org.cloudburstmc.nbt.NbtMap;

/**
 * Builder for firework explosions supporting multiple colors and face colors per explosion.
 * <p>
 *
 * @author herby2212
 * @since 2025/04/08
 */
public class ExplosionBuilder {
    private NbtMap explosionTag;

    public ExplosionBuilder() {
        this.explosionTag = NbtMap.EMPTY;
    }

    public ExplosionBuilder setColors(DyeColor... colors) {
        byte[] colorBytes = new byte[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colorBytes[i] = (byte) colors[i].getDyeData();
        }
        this.explosionTag = this.explosionTag.toBuilder().putByteArray("FireworkColor", colorBytes).build();
        return this;
    }

    public ExplosionBuilder setFadeColors(DyeColor... fadeColors) {
        byte[] fadeColorBytes = new byte[fadeColors.length];
        for (int i = 0; i < fadeColors.length; i++) {
            fadeColorBytes[i] = (byte) fadeColors[i].getDyeData();
        }
        this.explosionTag = this.explosionTag.toBuilder().putByteArray("FireworkFade", fadeColorBytes).build();
        return this;
    }

    public ExplosionBuilder setFlicker(boolean flicker) {
        this.explosionTag = this.explosionTag.toBuilder().putBoolean("FireworkFlicker", flicker).build();
        return this;
    }

    public ExplosionBuilder setTrail(boolean trail) {
        this.explosionTag = this.explosionTag.toBuilder().putBoolean("FireworkTrail", trail).build();
        return this;
    }

    public ExplosionBuilder setType(ExplosionType type) {
        this.explosionTag = this.explosionTag.toBuilder().putByte("FireworkType", (byte) type.ordinal()).build();
        return this;
    }

    public NbtMap build() {
        return this.explosionTag;
    }
}