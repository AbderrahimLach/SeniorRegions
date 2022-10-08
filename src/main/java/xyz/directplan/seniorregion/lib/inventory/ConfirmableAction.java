package xyz.directplan.seniorregion.lib.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.directplan.seniorregion.utility.PluginUtility;

import java.util.Collections;
import java.util.List;

/**
 * @author DirectPlan
 */
public class ConfirmableAction extends InventoryUI {

    private final List<String> description;
    private final ActionableItem acceptAction;

    public ConfirmableAction(List<String> description, ActionableItem acceptAction) {
        super("Please confirm", 5);
        this.description = description;
        this.acceptAction = acceptAction;
    }

    public ConfirmableAction(String description, ActionableItem acceptAction) {
        this(Collections.singletonList(description), acceptAction);
    }
    public ConfirmableAction(ActionableItem acceptAction) {
        this(ChatColor.GRAY + "Click here to confirm this action!", acceptAction);
    }

    @Override
    public void build(Player player) {

        MenuItem glassItem = new MenuItem(Material.BLACK_STAINED_GLASS, "&c");

        fillInventory(glassItem);

        ActionableItem cancellationAction = (item, clicker, clickType) -> {
            clicker.sendMessage(PluginUtility.translateMessage("&b&l(!) &fYou've cancelled this action!"));
            clicker.closeInventory();
        };

        int[] confirmationItemSlots = {
                10, 11, 12,
                19, 20, 21,
                28, 29, 30
        };
        int[] cancellationItemSlots = {
                14, 15, 16,
                23, 24, 25,
                32, 33, 34
        };
        MenuItem confirmationItem = new MenuItem(Material.GREEN_CONCRETE, "&a&lConfirm Action", 13, acceptAction);
        confirmationItem.setLore(description);
        MenuItem cancellationItem = new MenuItem(Material.RED_CONCRETE, "&c&lCancel Action", 14, cancellationAction);
        cancellationItem.setLore(PluginUtility.translateMessage("&7Click here to cancel this action!"));

        for (int confirmationItemSlot : confirmationItemSlots) {
            setSlot(confirmationItemSlot, confirmationItem);
        }
        for (int cancellationItemSlot : cancellationItemSlots) {
            setSlot(cancellationItemSlot, cancellationItem);
        }
    }
}
