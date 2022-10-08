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
public enum MessageConfigKeys implements ConfigEntry {

    NO_POSITIONS_SELECTED("no-positions-selected", "&cPlease select two region positions using /region wand"),
    REGION_ALREADY_EXIST("region-already-exist", "&cThis region already exists."),
    REGION_CREATED("region-created", "&aRegion '%name%' has been created!"),
    REGION_DELETED("region-deleted", "&cRegion '%name%' has been deleted!"),
    REGION_PERMISSION_DENIED("region-permission-denied", "&cYou're not the owner of this region."),
    REGION_INTERACTION_NOT_ALLOWED("region-interaction-not-allowed", "&cYou cannot interact with this region."),
    REGION_WHITELIST_ADDED("region-whitelist-added", "&aYou've whitelisted %player% to %region% region!"),
    REGION_WHITELIST_REMOVED("region-whitelist-removed", "&aYou've un-whitelisted %player% from %region% region!"),
    PLAYER_ALREADY_WHITELISTED("player-already-whitelisted", "&cThis player is already whitelisted."),
    PLAYER_NOT_WHITELISTED("player-not-whitelisted", "&cThis player is not whitelisted."),
    REGION_WAND_FIRST_POSITION_SET("region-want-first-position-set", "&aFirst position set!"),
    REGION_WAND_SECOND_POSITION_SET("region-want-second-position-set", "&aSecond position set!"),
    REGION_DOES_NOT_EXIST("region-not-found", "&cThis region does not exist."),


    ;
    private final String key;
    private final boolean forcedEntryDeclaration;
    @Setter
    private Object value;
    private final Map<String, ReplacementBoundary> replacementBoundaries = new HashMap<>();

    MessageConfigKeys(String key, Object defaultValue, boolean overwrite) {
        this.key = key;
        this.value = defaultValue;

        this.forcedEntryDeclaration = overwrite;
    }

    MessageConfigKeys(String key, Object value) {
        this(key, value, true);
    }

}
