package xyz.directplan.seniorregion.region;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author DirectPlan
 */
@Getter
public class Region {

    private final UUID id, owner;
    @Setter private String name;

    @Setter private RegionPositions regionPositions;
    private final List<UUID> whitelistedPlayers = new ArrayList<>();

    @Setter private boolean removed;

    public Region(UUID id, UUID owner, String name, RegionPositions regionPositions) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.regionPositions = regionPositions;
    }

    public Region(UUID owner, String name, RegionPositions regionPositions) {
        this(UUID.randomUUID(), owner, name, regionPositions);
    }

    public void addWhitelist(UUID uuid) {
        whitelistedPlayers.add(uuid);
    }

    public void removeWhitelist(UUID uuid) {
        whitelistedPlayers.remove(uuid);
    }

    public boolean isWhitelisted(UUID uuid) {
        return whitelistedPlayers.contains(uuid);
    }

    public Location getFirstPosition() {
        return regionPositions.getFirstPosition();
    }

    public Location getSecondPosition() {
        return regionPositions.getSecondPosition();
    }
}
