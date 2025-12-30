package cn.nukkit.wizard;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration holder for PowerNukkitX setup wizard settings
 *
 * @author AzaleeX
 * @author xRookieFight
 *
 * @since 17/12/2025
 */
@Getter
@Setter
public class WizardConfig {

    private String language = "eng";

    private boolean licenseAccepted = false;

    private int port = 19132;

    private String motd = "PowerNukkitX Server";

    private int gamemode = 0;

    private int maxPlayers = 20;

    private boolean whitelistEnabled = false;

    private List<String> whitelistedPlayers = new ArrayList<>();

    private List<String> operators = new ArrayList<>();

    private boolean queryEnabled = true;

}