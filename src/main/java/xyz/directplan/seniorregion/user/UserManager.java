package xyz.directplan.seniorregion.user;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.directplan.seniorregion.SeniorRegion;
import xyz.directplan.seniorregion.lib.storage.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * @author DirectPlan
 */
public class UserManager {

    @Getter
    private final Map<UUID, User> users = new HashMap<>();

    private final Logger logger;
    private final Storage storage;
    private final ExecutorService executorService;

    public UserManager(SeniorRegion plugin) {
        logger = plugin.getLogger();

        executorService = Executors.newFixedThreadPool(10);
        storage = plugin.getStorage();
    }

    public User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public void handleJoin(Player player, Consumer<User> completion) {
        UUID uuid = player.getUniqueId();
        logger.info("Loading user " + player.getName() + "...");
        loadUser(uuid, user -> {
            user.setPlayer(player);
            users.put(uuid, user);
            if(completion != null) completion.accept(user);
        });
    }

    public void loadUser(UUID uuid, Consumer<User> consumer) {
        CompletableFuture.supplyAsync(() -> {
            User user = getUser(uuid);
            if(user == null) {
                user = storage.loadPlayer(uuid);
            }
            return user;
        }, executorService).thenAccept(consumer);
    }


    public void handleQuit(Player player) {
        User user = users.remove(player.getUniqueId());
        user.setOnline(false);
        saveUser(user);
    }

    public void saveUser(User user) {
        CompletableFuture.runAsync(() -> storage.saveUser(user), executorService);
    }

    /* This bulk update operation is synchronous. Should only be executed on shutdown */
    public void saveAllUsers() {
        users.forEach((uuid, user) -> saveUser(user));
    }
}