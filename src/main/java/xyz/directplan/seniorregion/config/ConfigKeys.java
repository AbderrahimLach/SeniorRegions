package xyz.directplan.seniorregion.config;

import lombok.Getter;
import lombok.Setter;
import xyz.directplan.seniorregion.lib.config.ConfigEntry;
import xyz.directplan.seniorregion.lib.config.replacement.ReplacementBoundary;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DirectPlan
 */
@Getter
public enum ConfigKeys implements ConfigEntry {

    STORAGE_HOST("storage.host", "localhost"),
    STORAGE_PORT("storage.port", 3306),
    STORAGE_USERNAME("storage.username", "username"),
    STORAGE_PASSWORD("storage.password", "password"),
    STORAGE_DATABASE("storage.database", "database"),
    STORAGE_MAXIMUM_POOL_SIZE("storage.maximum-pool-size",10),


    PAGINATED_GUI_NEXT_PAGE("misc.paginated-gui.next-page", "&bNext page &7(%next_page%/%total_pages%)"),
    PAGINATED_GUI_PREVIOUS_PAGE("misc.paginated-gui.previous-page", "&bPrevious page &7(%previous_page%/%total_pages%)"),
    PAGINATED_GUI_CONTENTS_PER_PAGE("misc.paginated-gui.contents-per-page", 18),
    PAGINATED_GUI_ROWS_PER_PAGE("misc.paginated-gui.rows-per-page", 3),


    ;
    private final String key;
    private final boolean forcedEntryDeclaration;
    @Setter
    private Object value;
    private final Map<String, ReplacementBoundary> replacementBoundaries = new HashMap<>();

    ConfigKeys(String key, Object defaultValue, boolean overwrite) {
        this.key = key;
        this.value = defaultValue;

        this.forcedEntryDeclaration = overwrite;
    }

    ConfigKeys(String key, Object value) {
        this(key, value, true);
    }
}
