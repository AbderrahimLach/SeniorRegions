package xyz.directplan.seniorregion.region.menu;

import org.bukkit.entity.Player;
import xyz.directplan.seniorregion.lib.inventory.InventoryUI;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.RegionManager;
import xyz.directplan.seniorregion.user.User;

/**
 * @author DirectPlan
 */
public class RegionManageMenu extends InventoryUI {

    private final User user;
    private final Region region;
    private final RegionManager regionManager;

    public RegionManageMenu(User user, Region region, RegionManager regionManager) {
        super("Manage " + region.getName(), 6);

        this.user = user;
        this.region = region;
        this.regionManager = regionManager;
    }

    @Override
    public void build(Player player) {

    }
}
