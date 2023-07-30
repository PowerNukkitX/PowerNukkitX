package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.player.AdventureSettings;
import cn.nukkit.player.GameMode;
import cn.nukkit.player.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerGameModeChangeEvent extends PlayerEvent implements Cancellable {

    protected final GameMode newGameMode;

    protected AdventureSettings newAdventureSettings;

    public PlayerGameModeChangeEvent(Player player, GameMode newGameMode, AdventureSettings newAdventureSettings) {
        this.player = player;
        this.newGameMode = newGameMode;
        this.newAdventureSettings = newAdventureSettings;
    }
}
