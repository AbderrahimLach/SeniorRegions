package xyz.directplan.seniorregion.lib.storage;

/**
 * @author DirectPlan
 */
public interface StorageConnection {

    String getName();

    void connect();

    void close();
}
