package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class Network {
    private final Server server;
    private final Set<SourceInterface> interfaces = new HashSet<>();
    private final LinkedList<NetWorkStatisticData> netWorkStatisticDataList = new LinkedList<>();
    private String name;
    private String subName;
    @Nullable
    private final List<NetworkIF> hardWareNetworkInterfaces;

    public Network(Server server) {
        this.server = server;
        List<NetworkIF> tmpIfs = null;
        try {
            tmpIfs = new SystemInfo().getHardware().getNetworkIFs();
        } catch (Throwable t) {
            log.warn(Server.getInstance().getLanguage().get("nukkit.start.hardwareMonitorDisabled"));
        } finally {
            this.hardWareNetworkInterfaces = tmpIfs;
        }
    }

    record NetWorkStatisticData(long upload, long download) {
    }

    public double getUpload() {
        return netWorkStatisticDataList.get(1).upload - netWorkStatisticDataList.get(0).upload;
    }

    public double getDownload() {
        return netWorkStatisticDataList.get(1).download - netWorkStatisticDataList.get(0).download;
    }

    public void resetStatistics() {
        long upload = 0;
        long download = 0;
        if (netWorkStatisticDataList.size() > 1) {
            netWorkStatisticDataList.removeFirst();
        }
        if (this.hardWareNetworkInterfaces != null) {
            for (var networkIF : this.hardWareNetworkInterfaces) {
                networkIF.updateAttributes();
                upload += networkIF.getBytesSent();
                download += networkIF.getBytesRecv();
            }
        }
        netWorkStatisticDataList.add(new NetWorkStatisticData(upload, download));
    }

    public Set<SourceInterface> getInterfaces() {
        return interfaces;
    }

    public void processInterfaces() {
        for (SourceInterface interfaz : this.interfaces) {
            try {
                interfaz.process();
            } catch (Exception e) {
                log.error(this.server.getLanguage().tr("nukkit.server.networkError", interfaz.getClass().getName(), Utils.getExceptionMessage(e)), e);
                interfaz.shutdown();
                this.unregisterInterface(interfaz);
            }
        }
    }

    public void registerInterface(SourceInterface interfaz) {
        this.interfaces.add(interfaz);
        interfaz.setNetwork(this);
        interfaz.setName(this.name + "!@#" + this.subName);
    }

    public void unregisterInterface(SourceInterface sourceInterface) {
        this.interfaces.remove(sourceInterface);
    }

    public void setName(String name) {
        this.name = name;
        this.updateName();
    }

    public String getName() {
        return name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public void updateName() {
        for (SourceInterface interfaz : this.interfaces) {
            interfaz.setName(this.name + "!@#" + this.subName);
        }
    }

    public Server getServer() {
        return server;
    }

    public @Nullable List<NetworkIF> getHardWareNetworkInterfaces() {
        return hardWareNetworkInterfaces;
    }


    /**
     * Process packets obtained from batch packets
     * Required to perform additional analyses and filter unnecessary packets
     *
     * @param packets
     */
    public void processPackets(Player player, List<DataPacket> packets) {
        if (packets.isEmpty()) return;
        packets.forEach(p -> {
            try {
                player.handleDataPacket(p);
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Error whilst processing the packet {}:{} for {} (full data: {})",
                            p.pid(), p.getClass().getSimpleName(),
                            player.getName(), p.toString(),
                            e
                    );
                }
            }
        });
    }

    public void blockAddress(InetAddress address) {
        for (SourceInterface sourceInterface : this.interfaces) {
            sourceInterface.blockAddress(address);
        }
    }

    public void blockAddress(InetAddress address, int timeout) {
        for (SourceInterface sourceInterface : this.interfaces) {
            sourceInterface.blockAddress(address, timeout);
        }
    }

    public void unblockAddress(InetAddress address) {
        for (SourceInterface sourceInterface : this.interfaces) {
            sourceInterface.unblockAddress(address);
        }
    }
}
