package xyz.directplan.seniorregion.lib.inventory;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

/**
 * @author DirectPlan
 */
@RequiredArgsConstructor
public class MenuListener implements Listener {

    private final MenuManager menuManager;

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();

        // Player Currently Opened UI
        InventoryUI inventoryUI = menuManager.getInventory(player.getUniqueId());
        if(inventoryUI == null) return;

        inventoryUI.onClick(event);
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();

        UUID uuid = player.getUniqueId();
        InventoryUI inventoryUI = menuManager.getInventory(player.getUniqueId());
        if(inventoryUI != null && !inventoryUI.isLocked()) {
            inventoryUI.onClose(event.getInventory());
            menuManager.removeInventory(uuid);
        }
    }
}
