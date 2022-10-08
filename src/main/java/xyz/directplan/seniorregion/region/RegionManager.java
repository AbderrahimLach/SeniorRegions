package xyz.directplan.seniorregion.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.directplan.seniorregion.SeniorRegion;
import xyz.directplan.seniorregion.config.MessageConfigKeys;
import xyz.directplan.seniorregion.lib.config.replacement.Replacement;
import xyz.directplan.seniorregion.lib.inventory.MenuItem;
import xyz.directplan.seniorregion.lib.inventory.MenuManager;
import xyz.directplan.seniorregion.lib.storage.Storage;
import xyz.directplan.seniorregion.region.menu.RegionListMenu;
import xyz.directplan.seniorregion.region.menu.RegionManageMenu;
import xyz.directplan.seniorregion.user.User;
import xyz.directplan.seniorregion.user.UserManager;
import xyz.directplan.seniorregion.utility.PluginUtility;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author DirectPlan
 */
public class RegionManager {

    private Map<String, Region> regions = new HashMap<>();
    private final Set<String> deletedRegions = new HashSet<>();

    private final SeniorRegion plugin;
    private final Storage storage;
    private final UserManager userManager;
    private final MenuManager menuManager;

    private final ExecutorService executorService;

    public RegionManager(SeniorRegion plugin) {
        this.plugin = plugin;
        storage = plugin.getStorage();
        userManager = plugin.getUserManager();
        menuManager = plugin.getMenuManager();

        executorService = Executors.newFixedThreadPool(10);

    }


    public void initialize() {
        loadRegions();

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new ConstantRegionReceptor(userManager, this), 20L, 20L);
    }

    public void shutdown() {
        saveRegions();
    }

    public Region getRegion(String name) {
        return regions.get(name);
    }

    public boolean isDeleted(String regionName) {
        return deletedRegions.contains(regionName);
    }

    public void createRegion(User issuer, String name) {
        if(!issuer.isWandSelectionSet()) {
            MessageConfigKeys.NO_POSITIONS_SELECTED.sendMessage(issuer);
            return;
        }
        if(getRegion(name) != null) {
            MessageConfigKeys.REGION_ALREADY_EXIST.sendMessage(issuer);
            return;
        }
        if(isDeleted(name)) deletedRegions.remove(name);

        PairedPositions wandSelection = issuer.getWandSelection();
        Region region = new Region(issuer.getUuid(), name, wandSelection.clone());

        regions.put(name, region);
        issuer.getOwnedRegions().add(region);
        MessageConfigKeys.REGION_CREATED.sendMessage(issuer, new Replacement("name", name));
    }

    public void deleteRegion(User issuer, String name) {
        Region region = getRegion(name);
        deleteRegion(issuer, region);
    }

    public void deleteRegion(User issuer, Region region) {
        if(region == null) {
            MessageConfigKeys.REGION_DOES_NOT_EXIST.sendMessage(issuer);
            return;
        }

        Set<Region> ownedRegions = issuer.getOwnedRegions();
        if(!ownedRegions.remove(region)) { // If condition is true then the user doesn't own the region.
            MessageConfigKeys.REGION_PERMISSION_DENIED.sendMessage(issuer);
            return;
        }

        String regionName = region.getName();
        regions.remove(regionName);
        deletedRegions.add(regionName);

        issuer.getOwnedRegions().remove(region);
        MessageConfigKeys.REGION_DELETED.sendMessage(issuer, new Replacement("name", regionName));
    }

    public void updateRegion(User user) {
        Player player = user.getPlayer();

        for(Region region : regions.values()) {
            Region currentRegion = user.getCurrentRegion();
            if(currentRegion == region) continue;

            if(!isInsideRegion(player, region)) continue;

            user.setCurrentRegion(region);
            user.sendMessage("&aYou are now in the &e" + region.getName() + "&a region!");
        }
    }

    public boolean hasRegionAccess(User user, Region region) {
        return (user.isRegionOwner(region) || region.isWhitelisted(user.getUuid()));
    }

    public boolean isInteractionAllowed(User user, Location interactLocation) {
        boolean allowed = false;
        for(Region region : regions.values()) {
            if(!isInsideRegion(interactLocation, region)) continue;
            allowed = hasRegionAccess(user, region);
            break;
        }
        return allowed;
    }

    public void addWhitelist(User issuer, Region region, OfflinePlayer player) {
        if(region == null) {
            MessageConfigKeys.REGION_DOES_NOT_EXIST.sendMessage(issuer);
            return;
        }

        if(!issuer.isRegionOwner(region)) {
            MessageConfigKeys.REGION_PERMISSION_DENIED.sendMessage(issuer);
            return;
        }

        UUID playerUuid = player.getUniqueId();
        if(region.isWhitelisted(playerUuid)) {
            MessageConfigKeys.PLAYER_ALREADY_WHITELISTED.sendMessage(issuer);
            return;
        }
        region.addWhitelist(playerUuid);
        MessageConfigKeys.REGION_WHITELIST_ADDED.sendMessage(issuer, new Replacement("player", player.getName()), new Replacement("region", region.getName()));
    }

    public void removeWhitelist(User issuer, Region region, OfflinePlayer player) {
        if(region == null) {
            MessageConfigKeys.REGION_DOES_NOT_EXIST.sendMessage(issuer);
            return;
        }

        if(!issuer.isRegionOwner(region)) {
            MessageConfigKeys.REGION_PERMISSION_DENIED.sendMessage(issuer);
            return;
        }

        UUID playerUuid = player.getUniqueId();
        if(region.isWhitelisted(playerUuid)) {
            MessageConfigKeys.PLAYER_NOT_WHITELISTED.sendMessage(issuer);
            return;
        }
        region.removeWhitelist(playerUuid);
        MessageConfigKeys.REGION_WHITELIST_REMOVED.sendMessage(issuer, new Replacement("player", player.getName()), new Replacement("region", region.getName()));

    }

    public void showWhitelistedPlayers(User issuer, Region region) {
        if(region == null) {
            MessageConfigKeys.REGION_DOES_NOT_EXIST.sendMessage(issuer);
            return;
        }

        List<UUID> whitelistPlayers = region.getWhitelistedPlayers();
        List<String> whitelistNames = new ArrayList<>();
        for(UUID whitelistUuid : whitelistPlayers) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(whitelistUuid);
            whitelistNames.add(player.getName());
        }

        issuer.sendMessage("&eShowing whitelisted players of " + region.getName() + " region:");
        issuer.sendMessage("&7(&e" + whitelistPlayers.size() + "&7): &a" + String.join("&7, &a", whitelistNames));
    }

    public void giveWand(User user) {
        Player player = user.getPlayer();
        MenuItem menuItem = new MenuItem(Material.WOODEN_AXE, "&6&lRegion Wand");
        menuItem.setCompoundKey("region-wand");
        player.getInventory().addItem(menuItem.getItemStack());
    }

    public void openRegionListMenu(User user) {
        menuManager.openInventory(user, new RegionListMenu(user, this));
    }

    public void openRegionManageMenu(User user, Region region) {
        menuManager.openInventory(user, new RegionManageMenu(user, region, this));
    }


    public boolean isInsideRegion(Location location, Region region) {
        Location firstPos = region.getFirstPosition();
        Location secondPos = region.getSecondPosition();

        return PluginUtility.isInRegion(location, firstPos, secondPos);
    }

    public boolean isInsideRegion(Player player, Region region) {
        return isInsideRegion(player.getLocation(), region);
    }

    public void loadRegions() {
        CompletableFuture.supplyAsync(storage::loadRegions, executorService).thenAccept(regions -> this.regions = regions);
    }

    public void saveRegions() {
        CompletableFuture.runAsync(() -> storage.saveRegions(regions), executorService);
    }

    public Collection<Region> getRegions() {
        return regions.values();
    }
}
