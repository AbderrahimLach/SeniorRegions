package xyz.directplan.seniorregion.lib.storage;

import xyz.directplan.seniorregion.config.ConfigKeys;
import xyz.directplan.seniorregion.SeniorRegion;
import xyz.directplan.seniorregion.lib.storage.impl.SQLStorage;
import xyz.directplan.seniorregion.lib.storage.misc.ConnectionData;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.user.User;

import java.util.Map;
import java.util.UUID;

/**
 * @author DirectPlan
 */

public class Storage {

    private final StorageRepository repository;

    public Storage(SeniorRegion plugin) {

        String host = ConfigKeys.STORAGE_HOST.getStringValue();
        int port = ConfigKeys.STORAGE_PORT.getInteger();
        String username = ConfigKeys.STORAGE_USERNAME.getStringValue();
        String password = ConfigKeys.STORAGE_PASSWORD.getStringValue();
        String database = ConfigKeys.STORAGE_DATABASE.getStringValue();
        int maximumPoolSize = ConfigKeys.STORAGE_MAXIMUM_POOL_SIZE.getInteger();
        ConnectionData credentials = new ConnectionData(host, username, password, database, port, maximumPoolSize);

        repository = new SQLStorage(plugin, credentials);
        plugin.getLogger().info("Using " + repository.getName() + " for data storage!");
    }

    public String getName() {
        return repository.getName();
    }

    public void connect() {
        repository.connect();
    }

    public User loadPlayer(UUID uuid) {
        return repository.loadUser(uuid);
    }

    public void saveUser(User user) { // Make this an async completable future
        repository.saveUser(user);
    }

    public Map<String, Region> loadRegions() {
        return repository.loadRegions();
    }

    public void saveRegions(Map<String, Region> regions) {
        repository.saveRegions(regions);
    }

    public void close() {
        repository.close();
    }
}