package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import org.jetbrains.annotations.NotNull;

public interface ICommandBlock extends CommandSender, InventoryHolder {

    int CURRENT_VERSION = 10;

    //TODO: enum
    int MODE_NORMAL = 0;
    int MODE_REPEATING = 1;
    int MODE_CHAIN = 2;

    String TAG_CONDITIONAL_MODE = "conditionalMode";
    String TAG_AUTO = "auto";
    String TAG_POWERED = "powered";
    String TAG_CUSTOM_NAME = "CustomName";
    String TAG_COMMAND = "Command";
    String TAG_LAST_EXECUTION = "LastExecution";
    String TAG_TRACK_OUTPUT = "TrackOutput";
    String TAG_LAST_OUTPUT = "LastOutput";
    String TAG_LAST_OUTPUT_PARAMS = "LastOutputParams";
    String TAG_LP_COMMAND_MODE = "LPCommandMode";
    String TAG_LP_CONDIONAL_MODE = "LPCondionalMode";
    String TAG_LP_REDSTONE_MODE = "LPRedstoneMode";
    String TAG_SUCCESS_COUNT = "SuccessCount";
    String TAG_CONDITION_MET = "conditionMet";
    String TAG_VERSION = "Version";
    String TAG_TICK_DELAY = "TickDelay";
    String TAG_EXECUTE_ON_FIRST_TICK = "ExecuteOnFirstTick";

    @NotNull
    String getName();

    boolean hasName();

    void setName(String name);

    default void setPowered() {
        this.setPowered(true);
    }

    void setPowered(boolean powered);

    boolean isPowered();

    default boolean trigger() {
        return this.trigger(0);
    }

    default boolean trigger(int chain) {
        /*if (this.getLevel().getGameRules().getInteger(GameRule.MAX_COMMAND_CHAIN_LENGTH) < chain) {
            return false;
        }*/

        int delay = this.getTickDelay();
        if (delay > 0) {
            Server.getInstance().getScheduler().scheduleDelayedTask(new CommandBlockTrigger(this, chain), delay);
            return false;
        }

        return this.execute(chain);
    }

    default boolean execute() {
        return this.execute(0);
    }

    boolean execute(int chain);

    int getMode();

    String getCommand();

    void setCommand(String command);

    boolean isAuto();

    void setAuto(boolean auto);

    boolean isConditional();

    void setConditional(boolean conditionalMode);

    boolean isConditionMet();

    boolean setConditionMet();

    int getSuccessCount();

    void setSuccessCount(int count);

    long getLastExecution();

    void setLastExecution(long time);

    boolean isTrackingOutput();

    void setTrackOutput(boolean track);

    String getLastOutput();

    void setLastOutput(String output);

    int getLastOutputCommandMode();

    void setLastOutputCommandMode(int mode);

    boolean isLastOutputCondionalMode();

    void setLastOutputCondionalMode(boolean condionalMode);

    boolean isLastOutputRedstoneMode();

    void setLastOutputRedstoneMode(boolean redstoneMode);

    void setLastOutputParams(ListTag<StringTag> params);

    int getTickDelay();

    void setTickDelay(int tickDelay);

    boolean isExecutingOnFirstTick();

    void setExecutingOnFirstTick(boolean executingOnFirstTick);

    Level getLevel();

    public class CommandBlockTrigger implements Runnable {

        private final ICommandBlock commandBlock;
        private final int chain;

        public CommandBlockTrigger(ICommandBlock commandBlock, int chain) {
            this.commandBlock = commandBlock;
            this.chain = chain;
        }

        @Override
        public void run() {
            this.commandBlock.execute(this.chain);
        }
    }
}
