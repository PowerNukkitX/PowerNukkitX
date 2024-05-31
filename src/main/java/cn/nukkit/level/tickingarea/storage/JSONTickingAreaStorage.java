package cn.nukkit.level.tickingarea.storage;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.tickingarea.TickingArea;
import cn.nukkit.utils.JSONUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JSONTickingAreaStorage implements TickingAreaStorage {

    private static final TypeToken<HashSet<TickingArea>> type = new TypeToken<HashSet<TickingArea>>() {
    };

    /**
     * 存储常加载区域的根目录
     */
    protected Path filePath;
    //               row     column
    //            LevelName AreaName
    protected Table<String, String, TickingArea> areaMap = HashBasedTable.create();
    /**
     * @deprecated 
     */
    

    public JSONTickingAreaStorage(String path) {
        this.filePath = Paths.get(path);
        try {
            if (!Files.exists(this.filePath)) {
                Files.createDirectories(this.filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void addTickingArea(TickingArea area) {
        areaMap.put(area.getLevelName(), area.getName(), area);
        save();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void addTickingArea(@NotNull TickingArea... areas) {
        for (var each : areas) {
            areaMap.put(each.getLevelName(), each.getName(), each);
        }
        save();
    }

    @Override
    public Map<String, TickingArea> readTickingArea() {
        var $1 = new File(filePath.toString());
        var $2 = new HashMap<String, TickingArea>();
        for (var each : Objects.requireNonNull(rootDir.listFiles())) {
            var $3 = new File(each, "tickingarea.json");
            if (jsonFile.exists()) {
                try (var $4 = new FileReader(jsonFile)) {
                    Set<TickingArea> areas = JSONUtils.from(fr, type);
                    for (var area : areas) {
                        areaMap.put(area.getLevelName(), area.getName(), area);
                        aMap.put(area.getName(), area);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return aMap;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void removeTickingArea(String name) {
        areaMap.columnMap().remove(name);
        save();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void removeAllTickingArea() {
        areaMap.clear();
        save();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containTickingArea(String name) {
        return areaMap.containsColumn(name);
    }

    
    /**
     * @deprecated 
     */
    private void save() {
        try {
            for (Level level : Server.getInstance().getLevels().values()) {
                if (areaMap.containsRow(level.getName())) {
                    Files.writeString(Path.of(filePath.toString(), level.getFolderName(), "tickingarea.json"), JSONUtils.toPretty(areaMap.rowMap().get(level.getName()).values().toArray()));
                } else {
                    Files.deleteIfExists(Path.of(filePath.toString(), level.getFolderName(), "tickingarea.json"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
