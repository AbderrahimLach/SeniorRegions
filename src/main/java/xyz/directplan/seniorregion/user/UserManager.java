package xyz.directplan.seniorregion.user;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.directplan.seniorregion.SeniorRegion;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.RegionManager;

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

    private final SeniorRegion plugin;
    private final Logger logger;
    private final ExecutorService executorService;

    public UserManager(SeniorRegion plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();

        executorService = Executors.newFixedThreadPool(10);
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

        RegionManager regionManager = plugin.getRegionManager();

        CompletableFuture.runAsync(() -> {
            User user = new User(uuid);
            user.setPlayer(player);
            for(Region region : regionManager.getRegions()) {
                if(!region.getOwner().equals(uuid)) continue;

                user.getOwnedRegions().add(region);
            }
            users.put(uuid, user);
            completion.accept(user);
        }, executorService);
    }

    public void handleQuit(Player player) {
        users.remove(player.getUniqueId());
    }
}