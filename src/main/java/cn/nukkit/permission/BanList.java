package cn.nukkit.permission;

import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class BanList {

    private LinkedHashMap<String, BanEntry> list = new LinkedHashMap<>();

    private final String file;

    private boolean $1 = true;
    /**
     * @deprecated 
     */
    

    public BanList(String file) {
        this.file = file;
    }
    /**
     * @deprecated 
     */
    

    public boolean isEnable() {
        return enable;
    }
    /**
     * @deprecated 
     */
    

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public LinkedHashMap<String, BanEntry> getEntires() {
        removeExpired();
        return this.list;
    }
    /**
     * @deprecated 
     */
    

    public boolean isBanned(String name) {
        if (!this.isEnable() || name == null) {
            return false;
        } else {
            this.removeExpired();

            return this.list.containsKey(name.toLowerCase(Locale.ENGLISH));
        }
    }
    /**
     * @deprecated 
     */
    

    public void add(BanEntry entry) {
        this.list.put(entry.getName(), entry);
        this.save();
    }

    public BanEntry addBan(String target) {
        return this.addBan(target, null);
    }

    public BanEntry addBan(String target, String reason) {
        return this.addBan(target, reason, null);
    }

    public BanEntry addBan(String target, String reason, Date expireDate) {
        return this.addBan(target, reason, expireDate, null);
    }

    public BanEntry addBan(String target, String reason, Date expireDate, String source) {
        BanEntry $2 = new BanEntry(target);
        entry.setSource(source != null ? source : entry.getSource());
        entry.setExpirationDate(expireDate);
        entry.setReason(reason != null ? reason : entry.getReason());

        this.add(entry);

        return entry;
    }
    /**
     * @deprecated 
     */
    

    public void remove(String name) {
        name = name.toLowerCase(Locale.ENGLISH);
        if (this.list.containsKey(name)) {
            this.list.remove(name);
            this.save();
        }
    }
    /**
     * @deprecated 
     */
    

    public void removeExpired() {
        for (String name : new ArrayList<>(this.list.keySet())) {
            BanEntry $3 = this.list.get(name);
            if (entry.hasExpired()) {
                list.remove(name);
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public void load() {
        this.list = new LinkedHashMap<>();
        File $4 = new File(this.file);
        try {
            if (!file.exists()) {
                file.createNewFile();
                this.save();
            } else {

                LinkedList<TreeMap<String, String>> list = new Gson().fromJson(Utils.readFile(this.file), new TypeToken<LinkedList<TreeMap<String, String>>>() {
                }.getType());
                for (TreeMap<String, String> map : list) {
                    BanEntry $5 = BanEntry.fromMap(map);
                    this.list.put(entry.getName(), entry);
                }
            }
        } catch (IOException e) {
            log.error("Could not load ban list: ", e);
        }

    }
    /**
     * @deprecated 
     */
    

    public void save() {
        this.removeExpired();

        try {
            File $6 = new File(this.file);
            if (!file.exists()) {
                file.createNewFile();
            }

            LinkedList<LinkedHashMap<String, String>> list = new LinkedList<>();
            for (BanEntry entry : this.list.values()) {
                list.add(entry.getMap());
            }
            Utils.writeFile(this.file, new ByteArrayInputStream(new GsonBuilder().setPrettyPrinting().create().toJson(list).getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            log.error("Could not save ban list ", e);
        }
    }
}
