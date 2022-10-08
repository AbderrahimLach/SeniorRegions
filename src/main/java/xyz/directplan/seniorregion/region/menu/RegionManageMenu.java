package xyz.directplan.seniorregion.region.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.directplan.seniorregion.config.MessageConfigKeys;
import xyz.directplan.seniorregion.lib.inventory.ActionableItem;
import xyz.directplan.seniorregion.lib.inventory.InventoryUI;
import xyz.directplan.seniorregion.lib.inventory.MenuItem;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.RegionManager;
import xyz.directplan.seniorregion.region.RegionPositions;
import xyz.directplan.seniorregion.region.procedure.*;
import xyz.directplan.seniorregion.user.User;

/**
 * @author DirectPlan
 */
public class RegionManageMenu extends InventoryUI {

    private final User user;
    private final Region region;
    private final RegionManager regionManager;

    public RegionManageMenu(User user, Region region, RegionManager regionManager) {
        super("Manage " + region.getName(), 4);

        this.user = user;
        this.region = region;
        this.regionManager = regionManager;
    }

    @Override
    public void build(Player player) {
        MenuItem renameItem = new MenuItem(Material.NAME_TAG, "&e&lRename");
        renameItem.setAction(new RegionManageMenuItemAction(user, new RenameRegionProcedure(region)));

        MenuItem whitelistAddItem = new MenuItem(Material.LEAD, "&e&lWhitelist Add");
        whitelistAddItem.setAction(new RegionManageMenuItemAction(user, new WhitelistAddRegionProcedure(regionManager, region)));

        MenuItem whitelistRemoveItem = new MenuItem(Material.BARRIER, "&e&lWhitelist Remove");
        whitelistRemoveItem.setAction(new RegionManageMenuItemAction(user, new WhitelistRemoveRegionProcedure(regionManager, region)));

        MenuItem redefineLocationItem = new MenuItem(Material.COMPASS, "&e&lRedefine Location");
        redefineLocationItem.setAction((item, clicker, clickType) -> {
            clicker.closeInventory();
            if(!user.isWandSelectionSet()) {
                MessageConfigKeys.NO_POSITIONS_SELECTED.sendMessage(user);
                return;
            }
            RegionPositions regionPositions = user.getWandSelection();
            region.setRegionPositions(regionPositions);
            user.sendMessage("&aRegion location has been redefined!");
        });

        setSlot(12, renameItem);
        setSlot(14, whitelistAddItem);
        setSlot(36, whitelistAddItem);
        setSlot(38, whitelistAddItem);
    }
}
record RegionManageMenuItemAction(User user, RegionProcedure regionProcedure) implements ActionableItem {

    @Override
    public void performAction(MenuItem item, Player clicker, ClickType clickType) {
        clicker.closeInventory();

        regionProcedure.prepare(user);
        user.setCurrentProcedure(regionProcedure);
    }
}
