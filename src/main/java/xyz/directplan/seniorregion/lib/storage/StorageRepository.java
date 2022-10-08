package xyz.directplan.seniorregion.lib.storage;

import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.RegionManager;

import java.util.Map;

/**
 * @author DirectPlan
 */
public interface StorageRepository extends StorageConnection {

    Map<String, Region> loadRegions();

    void saveRegions(Map<String, Region> regions, RegionManager regionManager);
}
