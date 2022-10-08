package xyz.directplan.seniorregion.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author DirectPlan
 */
@RequiredArgsConstructor
@Getter
public class Region {

    private final UUID owner;
    private final String name;

    private final PairedPositions regionPositions;
    private final List<UUID> whitelistedPlayers = new ArrayList<>();

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
