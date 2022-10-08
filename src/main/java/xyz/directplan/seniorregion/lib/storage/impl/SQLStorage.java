package xyz.directplan.seniorregion.lib.storage.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.bukkit.Location;
import xyz.directplan.seniorregion.SeniorRegion;
import xyz.directplan.seniorregion.lib.storage.StorageRepository;
import xyz.directplan.seniorregion.lib.storage.misc.ConnectionData;
import xyz.directplan.seniorregion.region.RegionPositions;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.RegionManager;
import xyz.directplan.seniorregion.utility.CustomLocation;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author DirectPlan
 */
@Data
public class SQLStorage implements StorageRepository {


    private final SeniorRegion plugin;
    private final ConnectionData connectionData;
    private HikariDataSource dataSource;
    private final String name = "MySQL";

    private final String REGIONS_TABLE = "regions";
    private final String USERS_REGIONS_TABLE = "users_region";

    @Override
    public void connect() {

        HikariConfig config = new HikariConfig();

        config.setPoolName("SeniorRegions by DirectPlan - MySQL Connection Pool");

        config.setMaximumPoolSize(connectionData.getMaximumPoolSize());

        config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");

        Properties properties = new Properties();
        properties.put("serverName", connectionData.getHost());
        properties.put("port", connectionData.getPort());
        properties.put("databaseName", connectionData.getDatabase());
        properties.put("user", connectionData.getUsername());
        properties.put("password", connectionData.getPassword());
        config.setDataSourceProperties(properties);

        this.dataSource = new HikariDataSource(config);

        plugin.getLogger().info("MySQL Connection has been established!");
        initTables();
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public Map<String, Region> loadRegions() {
        Map<String, Region> regions = new HashMap<>();
        requestConnection(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + REGIONS_TABLE)) {
                try(ResultSet result = ps.executeQuery()) {
                    while(result.next()) {
                        UUID id = UUID.fromString(result.getString(1));
                        String name = result.getString(2);
                        UUID owner = UUID.fromString(result.getString(3));
                        String firstPositionString = result.getString(4);
                        String secondPositionString = result.getString(5);


                        CustomLocation firstPosition = CustomLocation.stringToLocation(firstPositionString);
                        CustomLocation secondPosition = CustomLocation.stringToLocation(secondPositionString);

                        RegionPositions positions = new RegionPositions(firstPosition.toBukkitLocation(), secondPosition.toBukkitLocation());
                        Region region = new Region(id, owner, name, positions);
                        regions.put(name, region);
                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + USERS_REGIONS_TABLE)) {
                try (ResultSet result = ps.executeQuery()) {
                    while(result.next()) {
                        String regionName = result.getString(2);
                        String whitelistedPlayerString = result.getString(3);

                        UUID whitelistedPlayer = UUID.fromString(whitelistedPlayerString);
                        Region region = regions.get(regionName);
                        region.addWhitelist(whitelistedPlayer);
                    }
                }
            }catch (SQLException e ) {
                e.printStackTrace();
            }
        });
        return regions;
    }

    @Override
    public void saveRegions(Map<String, Region> regions, RegionManager regionManager) {
        requestConnection(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO " + REGIONS_TABLE + "(id, name, owner, first_pos, second_pos) VALUES (?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE name = ?, first_pos = ?, second_pos = ?")) {

                for(Region region : regions.values()) {
                    UUID id = region.getId();
                    String name = region.getName();
                    UUID owner = region.getOwner();

                    Location firstPosition = region.getFirstPosition();
                    Location secondPosition = region.getSecondPosition();
                    String firstPositionString = CustomLocation.locationToString(CustomLocation.fromBukkitLocation(firstPosition));
                    String secondPositionString = CustomLocation.locationToString(CustomLocation.fromBukkitLocation(secondPosition));

                    ps.setString(1, id.toString());
                    ps.setString(2, name);
                    ps.setString(3, owner.toString());
                    ps.setString(4, firstPositionString);
                    ps.setString(5, secondPositionString);

                    // updates
                    ps.setString(6, name);
                    ps.setString(7, firstPositionString);
                    ps.setString(8, secondPositionString);

                    ps.addBatch();
                }
                ps.executeBatch();
            }catch (SQLException e) {
                e.printStackTrace();
            }
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO  " + USERS_REGIONS_TABLE + "(id, whitelisted_player) VALUES (?,?) " +
                    "ON DUPLICATE KEY UPDATE whitelisted_player = whitelisted_player")) {

                for(Region region : regions.values()) {
                    UUID id = region.getId();
                    ps.setString(1, id.toString());
                    for(UUID whitelistedPlayer : region.getWhitelistedPlayers()) {
                        ps.setString(2, whitelistedPlayer.toString());
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }catch (SQLException e) {
                e.printStackTrace();
            }

            // CLEANING GARBAGE
            cleanGarbage(connection, regionManager.getDeletedRegions(), regionManager.getRemovedWhitelists());
        });
    }

    private void cleanGarbage(Connection connection, Collection<UUID> deletedRegions, Collection<UUID> removedWhitelists) {
        if(!deletedRegions.isEmpty()) {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM " + REGIONS_TABLE + " WHERE id = ?")) {
                for(UUID deletedRegion : deletedRegions) {
                    ps.setString(1, deletedRegion.toString());
                    ps.addBatch();
                }
                ps.executeBatch();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(!removedWhitelists.isEmpty()) {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM " + USERS_REGIONS_TABLE + " WHERE whitelisted_player = ?")) {
                for(UUID whitelistedPlayer : removedWhitelists) {
                    ps.setString(1, whitelistedPlayer.toString());
                    ps.addBatch();
                }
                ps.executeBatch();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestConnection(Consumer<Connection> consumer) {
        try (Connection connection = dataSource.getConnection()) {
            consumer.accept(connection);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initTables() {
        requestConnection(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + REGIONS_TABLE +
                        "(id varchar(36), name varchar(30), owner varchar(36), first_pos TEXT, second_pos TEXT, CONSTRAINT players_pk PRIMARY KEY (id));");

                statement.addBatch("CREATE TABLE IF NOT EXISTS " + USERS_REGIONS_TABLE +
                        "(id varchar(36), whitelisted_player varchar(36), CONSTRAINT players_pk PRIMARY KEY (id));");

                statement.executeBatch();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
