package xyz.directplan.seniorregion.region;

import lombok.Getter;
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

/**
 * @author DirectPlan
 */
public class RegionManager {

    private Map<String, Region> regions = new HashMap<>();
    @Getter private final Set<UUID> deletedRegions = new HashSet<>();
    @Getter private final Set<UUID> removedWhitelists = new HashSet<>();

    private final SeniorRegion plugin;
    private final Storage storage;
    private final UserManager userManager;
    private final MenuManager menuManager;

    public RegionManager(SeniorRegion plugin) {
        this.plugin = plugin;
        storage = plugin.getStorage();
        userManager = plugin.getUserManager();
        menuManager = plugin.getMenuManager();

    }


    public void initialize() {
        loadRegions();

        plugin.getServer().getPluginManager().registerEvents(new RegionListener(userManager, this), plugin);
        // I guess 0.5 seconds period is perfect?
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new ConstantRegionReceptor(userManager, this), 20L, 10L);
    }

    public void shutdown() {
        saveRegions();
    }

    public Region getRegion(String name) {
        return regions.get(name);
    }

    public boolean isDeleted(UUID regionId) {
        return deletedRegions.contains(regionId);
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

        RegionPositions wandSelection = issuer.getWandSelection();
        Region region = new Region(issuer.getUuid(), name, wandSelection.clone());

        regions.put(name, region);
        issuer.getOwnedRegions().add(region);
        issuer.resetWandSelection();
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
        deletedRegions.add(region.getId());

        MessageConfigKeys.REGION_DELETED.sendMessage(issuer, new Replacement("name", regionName));
    }

    public void updateRegion(User user) {
        Player player = user.getPlayer();

        for(Region region : regions.values()) {
            String regionName = region.getName();

            Region currentRegion = user.getCurrentRegion();

            if(!isInsideRegion(player, region)) {
                if(currentRegion == region) {
                    MessageConfigKeys.REGION_LEFT.sendMessage(user, new Replacement("region", regionName));
                    user.setCurrentRegion(null);
                }
                continue;
            }
            if(currentRegion == region) continue;

            user.setCurrentRegion(region);
            MessageConfigKeys.REGION_ENTERED.sendMessage(user, new Replacement("region", regionName));
        }
    }

    public boolean hasRegionAccess(User user, Region region) {
        return (user.isRegionOwner(region) || region.isWhitelisted(user.getUuid()));
    }

    public boolean isInteractionAllowed(User user, Location interactLocation) {
        boolean allowed = true;
        for(Region region : regions.values()) {
            if(!isInsideRegion(interactLocation, region)) continue;
            allowed = hasRegionAccess(user, region);
            break;
        }
        return allowed;
    }

    public void addWhitelist(User issuer, Region region, OfflinePlayer player) {
        if(issuer.getPlayer() == player) {
            issuer.sendMessage("&cYou cannot whitelist yourself.");
            return;
        }
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
        removedWhitelists.remove(playerUuid);
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

        removedWhitelists.add(playerUuid);
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
        plugin.getLogger().info("Loading regions...");
        regions = storage.loadRegions();
        plugin.getLogger().info("Loaded " + regions.size() + " regions!");
    }

    public void saveRegions() {
        plugin.getLogger().info("Saving regions...");
        storage.saveRegions(regions, this);
    }

    public Collection<Region> getRegions() {
        return regions.values();
    }
}
