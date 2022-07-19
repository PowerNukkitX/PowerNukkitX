package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.TextFormat;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class VersionCommand extends VanillaCommand {

    public VersionCommand(String name) {
        super(name,
                "%nukkit.command.version.description",
                "%nukkit.command.version.usage",
                new String[]{"ver", "about"}
        );
        this.setPermission("nukkit.command.version");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("pluginName", true, CommandParamType.STRING)
        });
    }

    private record Query(CommandSender sender,CompletableFuture<JsonArray> jsonArrayFuture){};

    private List<Query> queryQueue = new LinkedList<>();

    {
        Server.getInstance().getScheduler().scheduleRepeatingTask(() -> {
            try {
                for (Query query : queryQueue.toArray(new Query[queryQueue.size()])) {
                    if (query.jsonArrayFuture.isDone()){
                        JsonArray cores = query.jsonArrayFuture.get();
                        String localCommitInfo = Server.getInstance().getGitCommit();
                        localCommitInfo = localCommitInfo.substring(4);
                        int versionMissed = -1;
                        for (int i = 0, len = cores.size(); i < len; i++) {
                            var entry = cores.get(i).getAsJsonObject();
                            var remoteCommitInfo = entry.get("name").getAsString().split("-")[1];
                            if (remoteCommitInfo.equals(localCommitInfo)){
                                versionMissed = i;
                                break;
                            }
                        }
                        if (versionMissed == 0)
                            query.sender.sendMessage(TextFormat.GREEN + "You are using the latest version of PowerNukkitX!");
                        else if(versionMissed > 0){
                            query.sender.sendMessage(TextFormat.YELLOW + "You are using an outdated version of PowerNukkitX!, " + versionMissed + " versions behind!");
                            query.sender.sendMessage(TextFormat.YELLOW + "The latest version is " + cores.get(0).getAsJsonObject().get("name").getAsString());
                        } else {
                            query.sender.sendMessage(TextFormat.RED + "Failed to check the version of PowerNukkitX!");
                        }
                        queryQueue.remove(query);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        },15);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("nukkit.server.info.extended", sender.getServer().getName(),
                    sender.getServer().getNukkitVersion() + " ("+sender.getServer().getGitCommit()+")",
                    sender.getServer().getCodename(),
                    sender.getServer().getApiVersion(),
                    sender.getServer().getVersion(),
                    String.valueOf(ProtocolInfo.CURRENT_PROTOCOL)));
            if (sender.isOp()) {
                sender.sendMessage("Checking version, please wait...");
                queryQueue.add(new Query(sender, listVersion()));
            }
        } else {
            StringBuilder pluginName = new StringBuilder();
            for (String arg : args) pluginName.append(arg).append(" ");
            pluginName = new StringBuilder(pluginName.toString().trim());
            final boolean[] found = {false};
            final Plugin[] exactPlugin = {sender.getServer().getPluginManager().getPlugin(pluginName.toString())};

            if (exactPlugin[0] == null) {
                pluginName = new StringBuilder(pluginName.toString().toLowerCase());
                final String finalPluginName = pluginName.toString();
                sender.getServer().getPluginManager().getPlugins().forEach((s, p) -> {
                    if (s.toLowerCase().contains(finalPluginName)) {
                        exactPlugin[0] = p;
                        found[0] = true;
                    }
                });
            } else {
                found[0] = true;
            }

            if (found[0]) {
                PluginDescription desc = exactPlugin[0].getDescription();
                sender.sendMessage(TextFormat.DARK_GREEN + desc.getName() + TextFormat.WHITE + " version " + TextFormat.DARK_GREEN + desc.getVersion());
                if (desc.getDescription() != null) {
                    sender.sendMessage(desc.getDescription());
                }
                if (desc.getWebsite() != null) {
                    sender.sendMessage("Website: " + desc.getWebsite());
                }
                List<String> authors = desc.getAuthors();
                final String[] authorsString = {""};
                authors.forEach((s) -> authorsString[0] += s);
                if (authors.size() == 1) {
                    sender.sendMessage("Author: " + authorsString[0]);
                } else if (authors.size() >= 2) {
                    sender.sendMessage("Authors: " + authorsString[0]);
                }
            } else {
                sender.sendMessage(new TranslationContainer("nukkit.command.version.noSuchPlugin"));
            }
        }
        return true;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    private CompletableFuture<JsonArray> listVersion() {
        return CompletableFuture.supplyAsync(() -> {
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(URI.create("https://api.powernukkitx.cn/get-core-manifest")).GET().build();
            try {
                var result = JsonParser.parseString(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
                if (result.isJsonArray()) {
                    return result.getAsJsonArray();
                }
                return new JsonArray();
            } catch (IOException | InterruptedException e) {
                return new JsonArray();
            }
        });
    }
}
