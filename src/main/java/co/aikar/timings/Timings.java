/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Daniel Ennis <http://aikar.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.aikar.timings;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.command.Command;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.plugin.EventExecutor;
import cn.nukkit.plugin.MethodEventExecutor;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.scheduler.TaskHandler;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static co.aikar.timings.TimingIdentifier.DEFAULT_GROUP;

@Log4j2
public final class Timings {
    private static final boolean timingsCompletelyClosed;
    private static boolean timingsEnabled = false;
    private static boolean verboseEnabled = false;
    private static boolean privacy = false;
    private static Set<String> ignoredConfigSections = new HashSet<>();

    private static final int MAX_HISTORY_FRAMES = 12;
    private static int historyInterval = -1;
    private static int historyLength = -1;
    //当timings完全关闭时代替使用的占位Timer
    //Placeholder Timer to use instead when timings are completely off
    public static final EmptyTiming emptyTimer;
    public static final FullServerTickTiming fullServerTickTimer;
    public static final Timing timingsTickTimer;
    public static final Timing pluginEventTimer;

    public static final Timing connectionTimer;
    public static final Timing schedulerTimer;
    public static final Timing schedulerAsyncTimer;
    public static final Timing schedulerSyncTimer;
    public static final Timing commandTimer;
    public static final Timing serverCommandTimer;
    public static final Timing levelSaveTimer;

    public static final Timing playerNetworkSendTimer;
    public static final Timing playerNetworkReceiveTimer;
    public static final Timing playerChunkOrderTimer;
    public static final Timing playerChunkSendTimer;
    public static final Timing playerCommandTimer;

    public static final Timing tickEntityTimer;
    public static final Timing tickBlockEntityTimer;
    public static final Timing entityMoveTimer;
    public static final Timing entityBaseTickTimer;
    public static final Timing livingEntityBaseTickTimer;

    public static final Timing generationTimer;
    public static final Timing populationTimer;
    public static final Timing generationCallbackTimer;

    public static final Timing permissibleCalculationTimer;
    public static final Timing permissionDefaultTimer;

    static {
        if (Server.getInstance() != null) {
            timingsCompletelyClosed = Server.getInstance().getConfig("timings.completely-close", true);
            if (!timingsCompletelyClosed) {
                setTimingsEnabled(Server.getInstance().getConfig("timings.enabled", false));
                setVerboseEnabled(Server.getInstance().getConfig("timings.verbose", false));
            } else {
                setTimingsEnabled(false);
                setVerboseEnabled(false);
            }
            setHistoryInterval(Server.getInstance().getConfig("timings.history-interval", 6000));
            setHistoryLength(Server.getInstance().getConfig("timings.history-length", 72000));
            privacy = Server.getInstance().getConfig("timings.privacy", false);
            ignoredConfigSections.addAll(Server.getInstance().getConfig().getStringList("timings.ignore"));
        } else timingsCompletelyClosed = true;

        if (!timingsCompletelyClosed) log.debug("Timings: \n" +
                        "Enabled - {}\n" +
                        "Verbose - {}\n" +
                        "History Interval - {}\n" +
                        "History Length - {}",
                isTimingsEnabled(), isVerboseEnabled(), getHistoryInterval(), getHistoryLength());

        emptyTimer = new EmptyTiming();
        fullServerTickTimer = new FullServerTickTiming();
        
        timingsTickTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming(DEFAULT_GROUP.name, "Timings Tick", fullServerTickTimer);
        pluginEventTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Plugin Events");

        connectionTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Connection Handler");
        schedulerTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Scheduler");
        schedulerAsyncTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("## Scheduler - Async Tasks");
        schedulerSyncTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("## Scheduler - Sync Tasks");
        commandTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Commands");
        serverCommandTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Server Command");
        levelSaveTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Level Save");

        playerNetworkSendTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Player Network Send");
        playerNetworkReceiveTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Player Network Receive");
        playerChunkOrderTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Player Order Chunks");
        playerChunkSendTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Player Send Chunks");
        playerCommandTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Player Command");

        tickEntityTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("## Entity Tick");
        tickBlockEntityTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("## BlockEntity Tick");
        entityMoveTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("## Entity Move");
        entityBaseTickTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("## Entity Base Tick");
        livingEntityBaseTickTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("## LivingEntity Base Tick");

        generationTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Level Generation");
        populationTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Level Population");
        generationCallbackTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Level Generation Callback");

        permissibleCalculationTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Permissible Calculation");
        permissionDefaultTimer = timingsCompletelyClosed ? emptyTimer : TimingsManager.getTiming("Default Permission Calculation");
    }

    /**
     * Initialize the static fields.
     */
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public static void init() {
        // code is already executed on <cinit>
    }

    /**
     * Timings是否被完全关闭了
     * @return boolean
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static boolean isTimingsCloseCompletely() {
        return timingsCompletelyClosed;
    }

    public static boolean isTimingsEnabled() {
        return timingsEnabled;
    }

    public static void setTimingsEnabled(boolean enabled) {
        if (timingsCompletelyClosed) return;
        timingsEnabled = enabled;
        TimingsManager.reset();
    }

    public static boolean isVerboseEnabled() {
        return verboseEnabled;
    }

    public static void setVerboseEnabled(boolean enabled) {
        verboseEnabled = enabled;
        TimingsManager.needsRecheckEnabled = true;
    }

    public static boolean isPrivacy() {
        return privacy;
    }

    public static Set<String> getIgnoredConfigSections() {
        return ignoredConfigSections;
    }

    public static int getHistoryInterval() {
        return historyInterval;
    }

    public static void setHistoryInterval(int interval) {
        historyInterval = Math.max(20 * 60, interval);
        //Recheck the history length with the new Interval
        if (historyLength != -1) {
            setHistoryLength(historyLength);
        }
    }

    public static int getHistoryLength() {
        return historyLength;
    }

    public static void setHistoryLength(int length) {
        //Cap at 12 History Frames, 1 hour at 5 minute frames.
        int maxLength = historyInterval * MAX_HISTORY_FRAMES;
        //For special cases of servers with special permission to bypass the max.
        //This max helps keep data file sizes reasonable for processing on Aikar's Timing parser side.
        //Setting this will not help you bypass the max unless Aikar has added an exception on the API side.
        if (Server.getInstance().getConfig().getBoolean("timings.bypass-max", false)) {
            maxLength = Integer.MAX_VALUE;
        }

        historyLength = Math.max(Math.min(maxLength, length), historyInterval);

        Queue<TimingsHistory> oldQueue = TimingsManager.HISTORY;
        int frames = (getHistoryLength() / getHistoryInterval());
        if (length > maxLength) {
            log.warn(
                    "Timings Length too high. Requested {}, max is {}"
                            + ". To get longer history, you must increase your interval. Set Interval to {}"
                            + " to achieve this length.",
                    length, maxLength, Math.ceil((float) length / MAX_HISTORY_FRAMES));
        }

        TimingsManager.HISTORY = new TimingsManager.BoundedQueue<>(frames);
        TimingsManager.HISTORY.addAll(oldQueue);
    }

    public static void reset() {
        TimingsManager.reset();
    }


    public static Timing getCommandTiming(Command command) {
        if (Timings.isTimingsCloseCompletely()) return Timings.emptyTimer;
        return TimingsManager.getTiming(DEFAULT_GROUP.name, "Command: " + command.getLabel(), commandTimer);
    }

    public static Timing getTaskTiming(TaskHandler handler, long period) {
        if (Timings.isTimingsCloseCompletely()) return Timings.emptyTimer;
        String repeating = " ";
        if (period > 0) {
            repeating += "(interval:" + period + ")";
        } else {
            repeating += "(Single)";
        }

        if (handler.getTask() instanceof PluginTask) {
            String owner = ((PluginTask) handler.getTask()).getOwner().getName();
            return TimingsManager.getTiming(owner, "PluginTask: " + handler.getTaskId() + repeating, schedulerSyncTimer, false);
        } else if (!handler.isAsynchronous()) {
            return TimingsManager.getTiming(DEFAULT_GROUP.name, "Task: " + handler.getTaskId() + repeating, schedulerSyncTimer, false);
        } else {
            return null;
        }
    }

    public static Timing getPluginEventTiming(Class<? extends Event> event, Listener listener, EventExecutor executor, Plugin plugin) {
        if (Timings.isTimingsCloseCompletely()) return Timings.emptyTimer;
        Timing group = TimingsManager.getTiming(plugin.getName(), "Combined Total", pluginEventTimer);

        return TimingsManager.getTiming(plugin.getName(), "Event: " + listener.getClass().getName() + "."
                + (executor instanceof MethodEventExecutor ? ((MethodEventExecutor) executor).getMethod().getName() : "???")
                + " (" + event.getSimpleName() + ")", group);
    }

    public static Timing getEntityTiming(Entity entity) {
        if (Timings.isTimingsCloseCompletely()) return Timings.emptyTimer;
        return TimingsManager.getTiming(DEFAULT_GROUP.name, "## Entity Tick: " + entity.getClass().getSimpleName(), tickEntityTimer);
    }

    public static Timing getBlockEntityTiming(BlockEntity blockEntity) {
        if (Timings.isTimingsCloseCompletely()) return Timings.emptyTimer;
        return TimingsManager.getTiming(DEFAULT_GROUP.name, "## BlockEntity Tick: " + blockEntity.getClass().getSimpleName(), tickBlockEntityTimer);
    }

    public static Timing getReceiveDataPacketTiming(DataPacket pk) {
        if (Timings.isTimingsCloseCompletely()) return Timings.emptyTimer;
        return TimingsManager.getTiming(DEFAULT_GROUP.name, "## Receive Packet: " + pk.getClass().getSimpleName(), playerNetworkReceiveTimer);
    }

    public static Timing getSendDataPacketTiming(DataPacket pk) {
        if (Timings.isTimingsCloseCompletely()) return Timings.emptyTimer;
        return TimingsManager.getTiming(DEFAULT_GROUP.name, "## Send Packet: " + pk.getClass().getSimpleName(), playerNetworkSendTimer);
    }

    public static void stopServer() {
        setTimingsEnabled(false);
        TimingsManager.recheckEnabled();
    }
}
