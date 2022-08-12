package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;

public class TpsCommand extends VanillaCommand {
    public TpsCommand(String name) {
        super(name, "get server tps");
        this.setPermission("nukkit.tps.status");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("count", true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        int count = 1;
        if (args.length > 0) {
            count = Integer.parseInt(args[0]);
        }

        if (count == 1) {
            float currentTps = Server.getInstance().getTicksPerSecond();
            sender.sendMessage(getTpsColor(currentTps) + " Current TPS: " + currentTps);
        } else {
            Server.getInstance().getScheduler().scheduleRepeatingTask(new TpsTestTask(sender, count), 20);
        }
        return true;
    }

    private TextFormat getTpsColor(float tps) {
        TextFormat tpsColor = TextFormat.GREEN;
        if (tps < 17) {
            tpsColor = TextFormat.GOLD;
        } else if (tps < 12) {
            tpsColor = TextFormat.RED;
        }
        return tpsColor;
    }

    private class TpsTestTask extends Task {

        private CommandSender sender;
        private int count;
        private int currentCount = 0;
        private float tpsSum = 0;

        public TpsTestTask(CommandSender sender, int count) {
            this.sender = sender;
            this.count = count;
        }

        @Override
        public void onRun(int currentTick) {
            currentCount++;
            float currentTps = Server.getInstance().getTicksPerSecond();

            sender.sendMessage("[" + currentCount + "]" + getTpsColor(currentTps) + " Current TPS: " + currentTps);
            tpsSum += currentTps;
            if (currentCount >= count) {
                sender.sendMessage("Average TPS: " + (tpsSum / count));
                this.cancel();
            }
        }
    }
}
