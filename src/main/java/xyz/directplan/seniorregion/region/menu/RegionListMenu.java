package xyz.directplan.seniorregion.region.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.directplan.seniorregion.lib.inventory.ActionableItem;
import xyz.directplan.seniorregion.lib.inventory.MenuItem;
import xyz.directplan.seniorregion.lib.inventory.SinglePaginatedMenu;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.RegionManager;
import xyz.directplan.seniorregion.user.User;

import java.util.Collection;

/**
 * @author DirectPlan
 */
public class RegionListMenu extends SinglePaginatedMenu<Region> {

    private final User user;
    private final RegionManager regionManager;
    private final ActionableItem regionMenuItemAction;

    public RegionListMenu(User user, RegionManager regionManager) {
        super("Regions", 4);

        this.user = user;
        this.regionManager = regionManager;
        regionMenuItemAction = new RegionMenuItemAction(user, regionManager);
    }

    @Override
    public Collection<Region> getList() {
        return user.getOwnedRegions();
    }

    @Override
    public MenuItem buildContent(Player player, Region region) {
        MenuItem menuItem = new MenuItem(Material.BIRCH_DOOR, "&e&l" + region.getName());
        menuItem.setAction(regionMenuItemAction);
        menuItem.setItemKey(region);
        menuItem.setLore("&eClick here to manage.");
        return menuItem;
    }
}

record RegionMenuItemAction(User user, RegionManager regionManager) implements ActionableItem {

    @Override
    public void performAction(MenuItem item, Player clicker, ClickType clickType) {
        Region region = (Region) item.getItemKey();
        regionManager.openRegionManageMenu(user, region);
    }
}
