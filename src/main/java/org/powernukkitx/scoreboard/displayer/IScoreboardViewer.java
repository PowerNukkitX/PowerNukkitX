package org.powernukkitx.scoreboard.displayer;

import org.powernukkitx.scoreboard.data.DisplaySlot;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;

/**
 * A scoreboard viewer (eg: Player)
 * This interface abstracts the protocol-layer methods from server to client.
 */


public interface IScoreboardViewer {
    /**
     * Displays the scoreboard in the specified slot
     * @param scoreboard the target scoreboard
     * @param slot the target slot
     */
    void display(IScoreboard scoreboard, DisplaySlot slot);

    /**
     * Clears the display content of the specified slot
     * @param slot the target slot
     */
    void hide(DisplaySlot slot);

    /**
     * Notifies the viewer that the scoreboard has been removed (if the scoreboard is in any display slot, that slot is cleared as well)
     * @param scoreboard the target scoreboard
     */
    void removeScoreboard(IScoreboard scoreboard);

    /**
     * Notifies the viewer that the specified line on the specified scoreboard has been removed
     * @param line the target line
     */
    void removeLine(IScoreboardLine line);

    /**
     * Sends the viewer the new score for the specified line
     * @param line the target line
     */
    void updateScore(IScoreboardLine line);
}
