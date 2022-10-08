package xyz.directplan.seniorregion.lib.inventory;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.directplan.seniorregion.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author AbderrahimLach
 */
public class MenuManager {

    @Getter private final Map<UUID, InventoryUI> inventories = new HashMap<>();
    
    public InventoryUI getInventory(UUID uuid){
        return inventories.get(uuid);
    }

    public InventoryUI removeInventory(UUID uuid){
        return this.inventories.remove(uuid);
    }

    public void addInventory(UUID uuid, InventoryUI inventoryUI){
        inventories.put(uuid, inventoryUI);
    }

    public void openInventory(User user, InventoryUI inventoryUI) {
        Player player = user.getPlayer();

        openInventory(player, inventoryUI);
    }

    public void openInventory(Player player, InventoryUI inventoryUI) {
        inventoryUI.open(player);
        addInventory(player.getUniqueId(), inventoryUI);
    }
}
