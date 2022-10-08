package xyz.directplan.seniorregion.lib.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * @author DirectPlan
 */
public interface ActionableItem {

    void performAction(MenuItem item, Player clicker, ClickType clickType);
}
