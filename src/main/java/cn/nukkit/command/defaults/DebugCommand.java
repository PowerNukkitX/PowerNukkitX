package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.item.ItemFilledMap;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class DebugCommand extends TestCommand implements CoreCommand {
    public DebugCommand(String name) {
        super(name, "commands.debug.description");
        this.setPermission("nukkit.command.debug");
        this.commandParameters.clear();
        //生物AI debug模式开关
        this.commandParameters.put("entity", new CommandParameter[]{
                CommandParameter.newEnum("entity", new String[]{"entity"}),
                CommandParameter.newEnum("option", Arrays.stream(EntityAI.DebugOption.values()).map(option -> option.name().toLowerCase(Locale.ENGLISH)).toList().toArray(new String[0])),
                CommandParameter.newEnum("value", false, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("rendermap", new CommandParameter[]{
                CommandParameter.newEnum("rendermap", new String[]{"rendermap"}),
                CommandParameter.newType("zoom", CommandParamType.INT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "entity" -> {
                String str = list.getResult(1);
                var option = EntityAI.DebugOption.valueOf(str.toUpperCase(Locale.ENGLISH));
                boolean value = list.getResult(2);
                EntityAI.setDebugOption(option, value);
                log.addSuccess("Entity AI framework " + option.name() + " debug mode have been set to: " + EntityAI.checkDebugOption(option)).output();
                return 1;
            }
            case "rendermap" -> {
                if (!sender.isPlayer())
                    return 0;
                int zoom = list.getResult(1);
                if (zoom < 1) {
                    log.addError("Zoom must bigger than one").output();
                    return 0;
                }
                var player = sender.asPlayer();
                if (player.getInventory().getItemInHand() instanceof ItemFilledMap itemFilledMap) {
                    Server.getInstance().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new AsyncTask() {
                        @Override
                        public void onRun() {
                            itemFilledMap.renderMap(player.getLevel(), player.getFloorX() - 64, player.getFloorZ() - 64, zoom);
                            player.getInventory().setItemInHand(itemFilledMap);
                            itemFilledMap.sendImage(player);
                            player.sendMessage("Successfully rendered the map in your hand");
                        }
                    });
                    log.addSuccess("Start rendering the map in your hand. Zoom: " + zoom).output();
                    return 1;
                }
                return 0;
            }
            default -> {
                return 0;
            }
        }
    }
}
