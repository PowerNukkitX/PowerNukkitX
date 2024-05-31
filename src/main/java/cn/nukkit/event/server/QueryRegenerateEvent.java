package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class QueryRegenerateEvent extends ServerEvent {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private static final String $2 = "MINECRAFTPE";

    private int timeout;
    private String serverName;
    private boolean listPlugins;
    private Plugin[] plugins;
    private Player[] players;

    private final String gameType;
    private String version;
    private final String server_engine;
    private String map;
    private int numPlayers;
    private int maxPlayers;
    private final String whitelist;
    private final int port;
    private final String ip;

    private Map<String, String> extraData = new HashMap<>();
    /**
     * @deprecated 
     */
    

    public QueryRegenerateEvent(Server server) {
        this(server, 5);
    }
    /**
     * @deprecated 
     */
    

    public QueryRegenerateEvent(Server server, int timeout) {
        this.timeout = timeout;
        this.serverName = server.getMotd();
        this.listPlugins = server.getSettings().baseSettings().queryPlugins();
        this.plugins = server.getPluginManager() == null ? Plugin.EMPTY_ARRAY : server.getPluginManager().getPlugins().values().toArray(Plugin.EMPTY_ARRAY);
        this.players = server.getOnlinePlayers().values().toArray(Player.EMPTY_ARRAY);
        this.gameType = (server.getGamemode() & 0x01) == 0 ? "SMP" : "CMP";
        this.version = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
        this.server_engine = server.getName() + " " + server.getNukkitVersion() + " (" + server.getGitCommit() + ")";
        this.map = server.getDefaultLevel() == null ? "unknown" : server.getDefaultLevel().getName();
        this.numPlayers = this.players.length;
        this.maxPlayers = server.getMaxPlayers();
        this.whitelist = server.hasWhitelist() ? "on" : "off";
        this.port = server.getPort();
        this.ip = server.getIp();
    }
    /**
     * @deprecated 
     */
    

    public int getTimeout() {
        return timeout;
    }
    /**
     * @deprecated 
     */
    

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    /**
     * @deprecated 
     */
    

    public String getServerName() {
        return serverName;
    }
    /**
     * @deprecated 
     */
    

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    /**
     * @deprecated 
     */
    

    public boolean canListPlugins() {
        return this.listPlugins;
    }
    /**
     * @deprecated 
     */
    

    public void setListPlugins(boolean listPlugins) {
        this.listPlugins = listPlugins;
    }

    public Plugin[] getPlugins() {
        return plugins;
    }
    /**
     * @deprecated 
     */
    

    public void setPlugins(Plugin[] plugins) {
        this.plugins = plugins;
    }

    public Player[] getPlayerList() {
        return players;
    }
    /**
     * @deprecated 
     */
    

    public void setPlayerList(Player[] players) {
        this.players = players;
    }
    /**
     * @deprecated 
     */
    

    public String getVersion() {
        return this.version;
    }
    /**
     * @deprecated 
     */
    

    public void setVersion(String version) {
        this.version = version;
    }
    /**
     * @deprecated 
     */
    

    public int getPlayerCount() {
        return this.numPlayers;
    }
    /**
     * @deprecated 
     */
    

    public void setPlayerCount(int count) {
        this.numPlayers = count;
    }
    /**
     * @deprecated 
     */
    

    public int getMaxPlayerCount() {
        return this.maxPlayers;
    }
    /**
     * @deprecated 
     */
    

    public void setMaxPlayerCount(int count) {
        this.maxPlayers = count;
    }
    /**
     * @deprecated 
     */
    

    public String getWorld() {
        return map;
    }
    /**
     * @deprecated 
     */
    

    public void setWorld(String world) {
        this.map = world;
    }

    public Map<String, String> getExtraData() {
        return extraData;
    }
    /**
     * @deprecated 
     */
    

    public void setExtraData(Map<String, String> extraData) {
        this.extraData = extraData;
    }

    public ByteBuf getLongQuery() {
        ByteBuf $3 = ByteBufAllocator.DEFAULT.ioBuffer();
        StringBuilder $4 = new StringBuilder(this.server_engine);
        if (this.plugins.length > 0 && this.listPlugins) {
            plist.append(":");
            for (Plugin p : this.plugins) {
                PluginDescription $5 = p.getDescription();
                plist.append(" ").append(d.getName().replace(";", "").replace(":", "").replace(" ", "_")).append(" ").append(d.getVersion().replace(";", "").replace(":", "").replace(" ", "_")).append(";");
            }
            plist = new StringBuilder(plist.substring(0, plist.length() - 2));
        }

        buf.writeBytes("splitnum".getBytes());
        buf.writeByte((byte) 0x00);
        buf.writeByte((byte) 128);
        buf.writeByte((byte) 0x00);

        LinkedHashMap<String, String> KVdata = new LinkedHashMap<>();
        KVdata.put("hostname", this.serverName);
        KVdata.put("gametype", this.gameType);
        KVdata.put("game_id", GAME_ID);
        KVdata.put("version", this.version);
        KVdata.put("server_engine", this.server_engine);
        KVdata.put("plugins", plist.toString());
        KVdata.put("map", this.map);
        KVdata.put("numplayers", String.valueOf(this.numPlayers));
        KVdata.put("maxplayers", String.valueOf(this.maxPlayers));
        KVdata.put("whitelist", this.whitelist);
        KVdata.put("hostip", this.ip);
        KVdata.put("hostport", String.valueOf(this.port));

        for (Map.Entry<String, String> entry : KVdata.entrySet()) {
            buf.writeBytes(entry.getKey().getBytes(StandardCharsets.UTF_8));
            buf.writeByte((byte) 0x00);
            buf.writeBytes(entry.getValue().getBytes(StandardCharsets.UTF_8));
            buf.writeByte((byte) 0x00);
        }

        buf.writeBytes(new byte[]{0x00, 0x01});
        buf.writeBytes("player_".getBytes());
        buf.writeBytes(new byte[]{0x00, 0x00});

        for (Player player : this.players) {
            buf.writeBytes(player.getName().getBytes(StandardCharsets.UTF_8));
            buf.writeByte((byte) 0x00);
        }

        buf.writeByte((byte) 0x00);
        return buf;
    }

    public ByteBuf getShortQuery() {
        ByteBuf $6 = ByteBufAllocator.DEFAULT.ioBuffer();
        buf.writeBytes(this.serverName.getBytes(StandardCharsets.UTF_8));
        buf.writeByte((byte) 0x00);
        buf.writeBytes(this.gameType.getBytes(StandardCharsets.UTF_8));
        buf.writeByte((byte) 0x00);
        buf.writeBytes(this.map.getBytes(StandardCharsets.UTF_8));
        buf.writeByte((byte) 0x00);
        buf.writeBytes(String.valueOf(this.numPlayers).getBytes(StandardCharsets.UTF_8));
        buf.writeByte((byte) 0x00);
        buf.writeBytes(String.valueOf(this.maxPlayers).getBytes(StandardCharsets.UTF_8));
        buf.writeByte((byte) 0x00);
        buf.writeBytes(Binary.writeLShort(this.port));
        buf.writeBytes(this.ip.getBytes(StandardCharsets.UTF_8));
        buf.writeByte((byte) 0x00);
        return buf;
    }

}
