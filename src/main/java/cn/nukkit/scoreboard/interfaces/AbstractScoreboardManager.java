package cn.nukkit.scoreboard.interfaces;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractScoreboardManager {

    protected Map<String, Scoreboard> scoreboards;
    protected Map<DisplaySlot, String> display;
    protected ScoreboardStorage storage;
    @Getter
    protected boolean reading = true;

    public AbstractScoreboardManager(ScoreboardStorage storage) {
        this.storage = storage;
        scoreboards = storage.readScoreboard(this);
        display = storage.readDisplay();
        reading = false;
    }

    public ScoreboardStorage getStorage() {
        return storage;
    }

    public abstract void addScoreboard(Scoreboard scoreboard);

    public abstract void removeScoreboard(String name);

    public Map<String, Scoreboard> getScoreboards() {
        return new HashMap<>(scoreboards);
    }

    public Scoreboard getScoreboard(String name) {
        return scoreboards.get(name);
    }

    public boolean hasScoreboard(String name) {
        return scoreboards.containsKey(name);
    }

    public abstract void setDisplay(DisplaySlot slot, String name);

    public abstract void removeDisplay(DisplaySlot slot);

    public Map<DisplaySlot, String> getDisplays() {
        return new HashMap<>(display);
    }

    public String getDisplay(DisplaySlot slot) {
        return display.get(slot);
    }

    public DisplaySlot getDisplaySlot(String name) {
        for (DisplaySlot slot : display.keySet()) {
            if (display.get(slot).equals(name)) {
                return slot;
            }
        }
        return null;
    }

    public boolean isScoreboardOnDisplay(String name) {
        return display.containsValue(name);
    }

    public boolean isScoreboardOnDisplaySlot(String name, DisplaySlot slot) {
        return display.get(slot) != null && display.get(slot).equals(name);
    }

    public boolean hasScoreboardOnDisplaySlot(DisplaySlot slot) {
        return display.get(slot) != null;
    }

    public abstract void onPlayerJoin(Player player);

    public abstract void onPlayerQuit(Player player);

    public void updateAllScoreTag() {
        if (reading)
            return;
        if (hasScoreboardOnDisplaySlot(DisplaySlot.BELOW_NAME) && hasScoreboard(display.get(DisplaySlot.BELOW_NAME))) {
            for (Scorer scorer : scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getLines().keySet()) {
                if (scorer instanceof PlayerScorer playerScorer) {
                    String tag = scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getLines().get(scorer).getScore() + " " + scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getDisplayName();
                    Optional<Player> player = Server.getInstance().getPlayer(playerScorer.getUuid());
                    if (!player.isEmpty() && !player.get().getScoreTag().equals(tag)) {
                        player.get().setScoreTag(tag);
                    }
                }
            }
        } else {
            Server.getInstance().getOnlinePlayers().values().forEach(player -> {
                player.setScoreTag("");
            });
        }
    }

    public void updateScoreTag(Player player) {
        if (reading)
            return;
        if (hasScoreboardOnDisplaySlot(DisplaySlot.BELOW_NAME) && hasScoreboard(display.get(DisplaySlot.BELOW_NAME))) {
            for (Scorer scorer : scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getLines().keySet()) {
                if (scorer instanceof PlayerScorer playerScorer) {
                    if (playerScorer.getUuid().equals(player.getUniqueId())) {
                        String tag = scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getLines().get(scorer).getScore() + " " + scoreboards.get(display.get(DisplaySlot.BELOW_NAME)).getDisplayName();
                        if (!player.getScoreTag().equals(tag)) {
                            player.setScoreTag(tag);
                        }
                    }
                }
            }
        } else {
            player.setScoreTag("");
        }
    }
}
