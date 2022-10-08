package xyz.directplan.seniorregion.lib.storage;

import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.user.User;

import java.util.Map;
import java.util.UUID;

/**
 * @author DirectPlan
 */
public interface StorageRepository extends StorageConnection {

    User loadUser(UUID uuid);

    void saveUser(User user);

    Map<String, Region> loadRegions();

    void saveRegions(Map<String, Region> regions);
}
