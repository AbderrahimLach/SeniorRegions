package xyz.directplan.seniorregion.lib.storage.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import xyz.directplan.seniorregion.SeniorRegion;
import xyz.directplan.seniorregion.lib.storage.StorageRepository;
import xyz.directplan.seniorregion.lib.storage.misc.ConnectionData;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.user.User;

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
    public User loadUser(UUID uuid) {
        User user = new User(uuid);
        requestConnection(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + USERS_REGIONS_TABLE + " WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet result = ps.executeQuery()) {
                    if(result.next()) {

                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }


        });

        return user;
    }

    @Override
    public void saveUser(User user) {
        String stringUuid = user.getUuid().toString();
        String username = user.getName();
        requestConnection(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO " + USERS_REGIONS_TABLE + "(uuid, name, beheaded_uuid, beheaded_name, beheaded_date) VALUES (?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE beheaded_date = beheaded_date")) {
                ps.setString(1, stringUuid);
                ps.setString(2, username);


                ps.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Map<String, Region> loadRegions() {
        return null;
    }

    @Override
    public void saveRegions(Map<String, Region> regions) {

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
                        "(name varchar(30), creator_uuid varchar(36), first_pos TEXT, second_pos TEXT, CONSTRAINT players_pk PRIMARY KEY (name));");

                statement.addBatch("CREATE TABLE IF NOT EXISTS " + USERS_REGIONS_TABLE +
                        "(name varchar(30), whitelisted_player varchar(36), CONSTRAINT players_pk PRIMARY KEY (name));");

                statement.executeBatch();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
