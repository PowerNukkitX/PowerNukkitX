package org.powernukkitx.scoreboard.manager;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.scoreboard.data.DisplaySlot;
import org.powernukkitx.scoreboard.displayer.IScoreboardViewer;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.storage.IScoreboardStorage;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Manages and stores a group of scoreboards. <br>
 * This interface targets the /scoreboard command; if you only want to display information, operate on the scoreboard object directly.
 */


public interface IScoreboardManager {
    /**
     * Adds a scoreboard.
     * @param scoreboard the target scoreboard
     * @return whether it was added successfully (returns false if the scoreboard already exists or the event was cancelled)
     */
    boolean addScoreboard(IScoreboard scoreboard);

    /**
     * Removes a scoreboard.
     * @param scoreboard the target scoreboard
     * @return whether it was removed successfully (returns false if the scoreboard does not exist or the event was cancelled)
     */
    boolean removeScoreboard(IScoreboard scoreboard);

    /**
     * Removes a scoreboard.
     * @param objectiveName the target scoreboard's identifier name
     * @return whether it was removed successfully (returns false if the scoreboard does not exist or the event was cancelled)
     */
    boolean removeScoreboard(String objectiveName);

    /**
     * Gets a scoreboard object (if it exists).
     * @param objectiveName the target scoreboard's identifier name
     * @return the scoreboard object
     */
    @Nullable IScoreboard getScoreboard(String objectiveName);

    /**
     * Gets all scoreboard objects.
     * @return all scoreboard objects
     */
    Map<String, IScoreboard> getScoreboards();

    /**
     * Checks whether the specified scoreboard exists.
     * @param scoreboard the specified scoreboard
     * @return whether it exists
     */
    boolean containScoreboard(IScoreboard scoreboard);

    /**
     * Checks whether the specified scoreboard exists.
     * @param name the specified scoreboard's identifier name
     * @return whether it exists
     */
    boolean containScoreboard(String name);

    /**
     * Gets the display slot information.
     * @return the display slot information
     */
    Map<DisplaySlot, IScoreboard> getDisplay();

    /**
     * Gets the scoreboard in the specified display slot (if it exists).
     * @param slot the specified slot
     * @return the scoreboard object
     */
    @Nullable IScoreboard getDisplaySlot(DisplaySlot slot);

    /**
     * Sets the scoreboard displayed in the specified slot.
     * If the scoreboard parameter is null, clears the content of the specified slot.
     * @param slot the specified slot
     * @param scoreboard the scoreboard object
     */
    void setDisplay(DisplaySlot slot, @Nullable IScoreboard scoreboard);

    /**
     * Gets all viewers.
     * @return all viewers
     */
    Set<IScoreboardViewer> getViewers();

    /**
     * Adds a viewer.
     * @param viewer the target viewer
     * @return whether it was added successfully
     */
    boolean addViewer(IScoreboardViewer viewer);

    /**
     * Removes a viewer (if it exists).
     * @param viewer the target viewer
     * @return whether it was removed successfully
     */
    boolean removeViewer(IScoreboardViewer viewer);

    /**
     * Server-internal method.
     */
    void onPlayerJoin(Player player);

    /**
     * Server-internal method.
     */
    void beforePlayerQuit(Player player);

    /**
     * Server-internal method.
     */
    void onEntityDead(EntityLiving entity);

    /**
     * Gets the scoreboard storage instance.
     * @return the storage instance
     */
    IScoreboardStorage getStorage();

    /**
     * Saves the scoreboard information through the storage.
     */
    void save();

    /**
     * Re-reads the scoreboard information from the storage.
     */
    void read();
}
