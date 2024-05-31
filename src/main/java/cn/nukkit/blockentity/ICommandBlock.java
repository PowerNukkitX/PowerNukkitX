package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.plugin.InternalPlugin;
import org.jetbrains.annotations.NotNull;

public interface ICommandBlock extends CommandSender, InventoryHolder {

    int $1 = 10;

    //TODO: enum
    int $2 = 0;
    int $3 = 1;
    int $4 = 2;

    String $5 = "conditionalMode";
    String $6 = "auto";
    String $7 = "powered";
    String $8 = "CustomName";
    String $9 = "Command";
    String $10 = "LastExecution";
    String $11 = "TrackOutput";
    String $12 = "LastOutput";
    String $13 = "LastOutputParams";
    String $14 = "LPCommandMode";
    String $15 = "LPCondionalMode";
    String $16 = "LPRedstoneMode";
    String $17 = "SuccessCount";
    String $18 = "conditionMet";
    String $19 = "Version";
    String $20 = "TickDelay";
    String $21 = "ExecuteOnFirstTick";

    @NotNull
    String getName();

    boolean hasName();

    void setName(String name);

    default 
    /**
     * @deprecated 
     */
    void setPowered() {
        this.setPowered(true);
    }

    void setPowered(boolean powered);

    boolean isPowered();

    default 
    /**
     * @deprecated 
     */
    boolean trigger() {
        return this.trigger(0);
    }

    default 
    /**
     * @deprecated 
     */
    boolean trigger(int chain) {
        /*if (this.getLevel().getGameRules().getInteger(GameRule.MAX_COMMAND_CHAIN_LENGTH) < chain) {
            return false;
        }*/

        int $22 = this.getTickDelay();
        if (delay > 0) {
            Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, new CommandBlockTrigger(this, chain), delay);
            return false;
        }

        return this.execute(chain);
    }

    default 
    /**
     * @deprecated 
     */
    boolean execute() {
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
    /**
     * @deprecated 
     */
    

        public CommandBlockTrigger(ICommandBlock commandBlock, int chain) {
            this.commandBlock = commandBlock;
            this.chain = chain;
        }

        @Override
    /**
     * @deprecated 
     */
    
        public void run() {
            this.commandBlock.execute(this.chain);
        }
    }
}
