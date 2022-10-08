package xyz.directplan.seniorregion.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.directplan.seniorregion.region.RegionPositions;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.procedure.RegionProcedure;
import xyz.directplan.seniorregion.utility.PluginUtility;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author DirectPlan
 */
@Data
@Getter
@Setter
public class User {

    private final UUID uuid;
    private String name;
    private Player player;
    private boolean online;

    private final RegionPositions wandSelection = new RegionPositions();
    private final Set<Region> ownedRegions = new HashSet<>();

    private Region currentRegion;
    private RegionProcedure currentProcedure;

    public String getName() {
        if(player != null) return player.getName();
        return name;
    }

    public void sendMessage(String message) {
        if(player == null) return;
        player.sendMessage(PluginUtility.translateMessage(message));
    }

    public boolean isRegionOwner(Region region) {
        return ownedRegions.contains(region);
    }

    public boolean isWandSelectionSet() {
        return wandSelection.isSet();
    }

    public void resetWandSelection() {
        wandSelection.setFirstPosition(null);
        wandSelection.setSecondPosition(null);
    }

    public void setFirstWandSelection(Location location) {
        wandSelection.setFirstPosition(location);
    }

    public void setSecondWandSelection(Location location) {
        wandSelection.setSecondPosition(location);
    }
}
