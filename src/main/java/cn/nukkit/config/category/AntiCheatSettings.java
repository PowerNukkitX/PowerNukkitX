package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class AntiCheatSettings extends OkaeriConfig {
    @Comment("pnx.settings.anticheat.enabled")
    boolean enabled = true;

    @Comment("pnx.settings.anticheat.fly.enabled")
    boolean flyEnabled = true;
    @Comment("pnx.settings.anticheat.fly.threshold")
    int flyThreshold = 40;

    @Comment("pnx.settings.anticheat.speed.enabled")
    boolean speedEnabled = true;
    @Comment("pnx.settings.anticheat.speed.threshold")
    double speedThreshold = 0.8;

    @Comment("pnx.settings.anticheat.reach.enabled")
    boolean reachEnabled = true;
    @Comment("pnx.settings.anticheat.reach.distance")
    double reachDistance = 5.0;

    @Comment("pnx.settings.anticheat.water.enabled")
    boolean waterEnabled = true;
    @Comment("pnx.settings.anticheat.water.threshold")
    double waterThreshold = 0.4;

    @Comment("pnx.settings.anticheat.noclip.enabled")
    boolean noclipEnabled = true;

    @Comment("pnx.settings.anticheat.fastbreak.enabled")
    boolean fastbreakEnabled = true;

    @Comment("pnx.settings.anticheat.timer.enabled")
    boolean timerEnabled = true;

    @Comment("pnx.settings.anticheat.autoclicker.enabled")
    boolean autoclickerEnabled = true;
    @Comment("pnx.settings.anticheat.autoclicker.max_cps")
    int autoclickerMaxCps = 20;

    @Comment("pnx.settings.anticheat.phase.enabled")
    boolean phaseEnabled = true;

    @Comment("pnx.settings.anticheat.ghosthand.enabled")
    boolean ghosthandEnabled = true;
}
