package xyz.directplan.seniorregion.region;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.directplan.seniorregion.SeniorRegion;
import xyz.directplan.seniorregion.config.MessageConfigKeys;
import xyz.directplan.seniorregion.user.User;
import xyz.directplan.seniorregion.user.UserManager;
import xyz.directplan.seniorregion.utility.PluginUtility;

/**
 * @author DirectPlan
 */
public class RegionListener implements Listener {

    private final UserManager userManager;
    private final RegionManager regionManager;

    public RegionListener(SeniorRegion plugin) {
        userManager = plugin.getUserManager();
        regionManager = plugin.getRegionManager();
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();
        if(clickedBlock == null) return;

        User user = userManager.getUser(player);

        if(!regionManager.isInteractionAllowed(user, clickedBlock.getLocation())) {
            event.setCancelled(true);
            return;
        }
        if((item != null && item.getType() == Material.WOODEN_AXE) && PluginUtility.hasItemNBTKey(item, "region-wand")) {
            Action action = event.getAction();
            if(action == Action.LEFT_CLICK_BLOCK) {
                user.setFirstWandSelection(clickedBlock.getLocation());
                MessageConfigKeys.REGION_WAND_FIRST_POSITION_SET.sendMessage(user);
                return;
            }

            if(action == Action.RIGHT_CLICK_BLOCK) {
                user.setFirstWandSelection(clickedBlock.getLocation());
                MessageConfigKeys.REGION_WAND_SECOND_POSITION_SET.sendMessage(user);
            }
            event.setCancelled(true);
        }
    }
}