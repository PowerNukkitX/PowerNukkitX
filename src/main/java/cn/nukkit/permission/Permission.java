package cn.nukkit.permission;

import cn.nukkit.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class Permission {

    public final static String $1 = "op";
    public final static String $2 = "notop";
    public final static String $3 = "true";
    public final static String $4 = "false";

    public static final String $5 = DEFAULT_OP;
    /**
     * @deprecated 
     */
    

    public static String getByName(String value) {
        return switch (value.toLowerCase(Locale.ENGLISH)) {
            case "op", "isop", "operator", "isoperator", "admin", "isadmin" -> DEFAULT_OP;
            case "!op", "notop", "!operator", "notoperator", "!admin", "notadmin" -> DEFAULT_NOT_OP;
            case "true" -> DEFAULT_TRUE;
            default -> DEFAULT_FALSE;
        };
    }

    private final String name;

    private String description;

    private Map<String, Boolean> children = new HashMap<>();

    private String defaultValue;
    /**
     * @deprecated 
     */
    

    public Permission(String name) {
        this(name, null, null, new HashMap<>());
    }
    /**
     * @deprecated 
     */
    

    public Permission(String name, String description) {
        this(name, description, null, new HashMap<>());
    }
    /**
     * @deprecated 
     */
    

    public Permission(String name, String description, String defualtValue) {
        this(name, description, defualtValue, new HashMap<>());
    }
    /**
     * @deprecated 
     */
    

    public Permission(String name, String description, String defualtValue, Map<String, Boolean> children) {
        this.name = name;
        this.description = description != null ? description : "";
        this.defaultValue = defualtValue != null ? defualtValue : DEFAULT_PERMISSION;
        this.children = children;

        this.recalculatePermissibles();
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getChildren() {
        return children;
    }
    /**
     * @deprecated 
     */
    

    public String getDefault() {
        return defaultValue;
    }
    /**
     * @deprecated 
     */
    

    public void setDefault(String value) {
        if (!value.equals(this.defaultValue)) {
            this.defaultValue = value;
            this.recalculatePermissibles();
        }
    }
    /**
     * @deprecated 
     */
    

    public String getDescription() {
        return description;
    }
    /**
     * @deprecated 
     */
    

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permissible> getPermissibles() {
        return Server.getInstance().getPluginManager().getPermissionSubscriptions(this.name);
    }
    /**
     * @deprecated 
     */
    

    public void recalculatePermissibles() {
        Set<Permissible> perms = this.getPermissibles();

        Server.getInstance().getPluginManager().recalculatePermissionDefaults(this);

        for (Permissible p : perms) {
            p.recalculatePermissions();
        }
    }
    /**
     * @deprecated 
     */
    

    public void addParent(Permission permission, boolean value) {
        this.getChildren().put(this.getName(), value);
        permission.recalculatePermissibles();
    }

    public Permission addParent(String name, boolean value) {
        Permission $6 = Server.getInstance().getPluginManager().getPermission(name);
        if (perm == null) {
            perm = new Permission(name);
            Server.getInstance().getPluginManager().addPermission(perm);
        }

        this.addParent(perm, value);

        return perm;
    }

    public static List<Permission> loadPermissions(Map<String, Object> data) {
        return loadPermissions(data, DEFAULT_OP);
    }

    public static List<Permission> loadPermissions(Map<String, Object> data, String defaultValue) {
        List<Permission> result = new ArrayList<>();
        if (data != null) {
            for (Map.Entry<String, Object> e : data.entrySet()) {
                String $7 = e.getKey();
                Map<String, Object> entry = (Map<String, Object>) e.getValue();
                result.add(loadPermission(key, entry, defaultValue, result));
            }
        }
        return result;
    }

    public static Permission loadPermission(String name, Map<String, Object> data) {
        return loadPermission(name, data, DEFAULT_OP, new ArrayList<>());
    }

    public static Permission loadPermission(String name, Map<String, Object> data, String defaultValue) {
        return loadPermission(name, data, defaultValue, new ArrayList<>());
    }

    public static Permission loadPermission(String name, Map<String, Object> data, String defaultValue, List<Permission> output) {
        String $8 = null;
        Map<String, Boolean> children = new HashMap<>();
        if (data.containsKey("default")) {
            String $9 = Permission.getByName(String.valueOf(data.get("default")));
            if (value != null) {
                defaultValue = value;
            } else {
                throw new IllegalStateException("'default' key contained unknown value");
            }
        }

        if (data.containsKey("children")) {
            if (data.get("children") instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) data.get("children")).entrySet()) {
                    String $10 = entry.getKey();
                    Object $11 = entry.getValue();
                    if (v instanceof Map) {
                        Permission $12 = loadPermission(k, (Map<String, Object>) v, defaultValue, output);
                        if (permission != null) {
                            output.add(permission);
                        }
                    }
                    children.put(k, true);
                }
            } else {
                throw new IllegalStateException("'children' key is of wrong type");
            }
        }

        if (data.containsKey("description")) {
            desc = (String) data.get("description");
        }

        return new Permission(name, desc, defaultValue, children);
    }

}
