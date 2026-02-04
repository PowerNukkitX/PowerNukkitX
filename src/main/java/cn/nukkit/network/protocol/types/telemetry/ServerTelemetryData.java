package cn.nukkit.network.protocol.types.telemetry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerTelemetryData {

    // TODO: Add proper telemetry data

    private final String serverId = "";
    private final String scenarioId = "";
    private final String worldId = "";
    private final String ownerId = "";

}