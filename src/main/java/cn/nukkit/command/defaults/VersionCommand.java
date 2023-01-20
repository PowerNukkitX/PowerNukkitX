package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class VersionCommand extends Command implements CoreCommand {

    private List<Query> queryQueue = new LinkedList<>();
    private int lastUpdateTick = 0;
    private JsonArray listVersionCache = null;

    {
        Server.getInstance().getScheduler().scheduleRepeatingTask(() -> {
            try {
                for (Query query : queryQueue.toArray(new Query[queryQueue.size()])) {
                    if (query.jsonArrayFuture.isDone()) {
                        JsonArray cores = query.jsonArrayFuture.get();
                        String localCommitInfo = Server.getInstance().getGitCommit();
                        localCommitInfo = localCommitInfo.substring(4);
                        int versionMissed = -1;
                        query.sender.sendMessage("####################");
                        var matched = false;
                        for (int i = 0, len = cores.size(); i < len; i++) {
                            var entry = cores.get(i).getAsJsonObject();
                            var remoteCommitInfo = entry.get("name").getAsString().split("-")[1];
                            matched = remoteCommitInfo.equals(localCommitInfo);

                            var infoBuilder = new StringBuilder();
                            infoBuilder.append("[").append(i + 1).append("] ");
                            if (i == 0)
                                infoBuilder.append("Name: §e").append(entry.get("name").getAsString()).append("§f, Time: §e").append(utcToLocal(entry.get("lastModified").getAsString())).append(" §e(LATEST)").append(matched ? " §b(CURRENT)" : "");
                            else if (matched)
                                infoBuilder.append("Name: §b").append(entry.get("name").getAsString()).append("§f, Time: §b").append(utcToLocal(entry.get("lastModified").getAsString())).append(" §b(CURRENT)");
                            else
                                infoBuilder.append("Name: §a").append(entry.get("name").getAsString()).append("§f, Time: §a").append(utcToLocal(entry.get("lastModified").getAsString()));
                            //打印相关信息
                            query.sender.sendMessage(infoBuilder.toString());

                            if (matched) {
                                versionMissed = i;
                                break;
                            }
                        }
                        //too old
                        if (!matched) {
                            query.sender.sendMessage("....................");
                            var localInfoBuilder = new StringBuilder();
                            localInfoBuilder.append("[???] ").append("Name: §c").append(localCommitInfo).append("§f, Time: §c???").append(" §c(CURRENT)");
                            query.sender.sendMessage(localInfoBuilder.toString());
                        }
                        query.sender.sendMessage("####################");
                        if (versionMissed == 0)
                            query.sender.sendMessage("§aYou are using the latest version of PowerNukkitX!");
                        else if (versionMissed > 0) {
                            query.sender.sendMessage("§cYou are using an outdated version of PowerNukkitX!, §f" + versionMissed + " §aversions behind!");
                        } else {
                            query.sender.sendMessage("§cCouldn't match your version number: §f" + localCommitInfo + "§c, maybe you are using a custom build or your version is too old!");
                        }
                        if (versionMissed != 0) {
                            query.sender.sendMessage("Download the latest version at §a" + cores.get(0).getAsJsonObject().get("url").getAsString());
                            query.sender.sendMessage("You can enter command §a \"pnx server update\"§f to automatically update your server if you are using PNX-CLI");
                            query.sender.sendMessage("Download PNX-CLI at: §a" + "https://github.com/PowerNukkitX/PNX-CLI/releases");
                        }
                        queryQueue.remove(query);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }, 15);
    }

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

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("nukkit.server.info.extended", sender.getServer().getName(),
                    sender.getServer().getNukkitVersion() + " (" + sender.getServer().getGitCommit() + ")",
                    sender.getServer().getCodename(),
                    sender.getServer().getApiVersion(),
                    sender.getServer().getVersion(),
                    String.valueOf(ProtocolInfo.CURRENT_PROTOCOL)));
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
            if (this.listVersionCache != null) {
                if (Server.getInstance().getTick() - this.lastUpdateTick < 7200) {//20 * 60 * 60 一小时
                    return this.listVersionCache;
                }
            }
            var client = HttpClient.newHttpClient();
            var builder = HttpRequest.newBuilder(URI.create("https://api.powernukkitx.cn/get-core-manifest?max=100")).GET();
            builder.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
            var request = builder.build();
            try {
                var result = JsonParser.parseString(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
                if (result.isJsonArray()) {
                    this.lastUpdateTick = Server.getInstance().getTick();
                    this.listVersionCache = result.getAsJsonArray();
                    return this.listVersionCache;
                }
                return new JsonArray();
            } catch (IOException | InterruptedException e) {
                return new JsonArray();
            }
        });
    }

    protected String utcToLocal(String utcTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        try {
            utcDate = sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df.format(utcDate);
    }

    private record Query(CommandSender sender, CompletableFuture<JsonArray> jsonArrayFuture) {
    }
}
