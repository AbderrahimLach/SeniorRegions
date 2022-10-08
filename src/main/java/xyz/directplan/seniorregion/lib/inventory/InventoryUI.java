package xyz.directplan.seniorregion.lib.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.function.Function;

/**
 * @author DirectPlan
 */
@Getter
public abstract class InventoryUI {

    private Inventory inventory;

    @Setter private String title;
    private final MenuItem[] items;
    private final int size;
    private final boolean displayUi;
    private Object lock;

    public InventoryUI(String title, int rows, boolean displayUi){
        this.displayUi = displayUi;
        this.title = title;
        size = (9 * rows);
        items = new MenuItem[size];


        if(title != null) {
            String translatedTitle = ChatColor.translateAlternateColorCodes('&', title);
            this.inventory = Bukkit.createInventory(null, size, translatedTitle);
        }
    }

    public InventoryUI(String title, int rows) {
        this(title, rows, false);
    }

    public int getSize() {
        return size;
    }

    public void setSlot(int slot, MenuItem item){
        items[slot] = item;
        item.setSlot(slot);
    }

    public void setSlot(MenuItem item) {
        int slot = item.getSlot();
        setSlot(slot, item);
    }

    public void clearItems() {
        Arrays.fill(items, null);
    }

    public MenuItem getItem(int slot){
        if(slot > items.length - 1) {
            return null;
        }
        return items[slot];
    }

    public void fillInventory(MenuItem item) {
        Arrays.fill(items, item);
    }

    public void onClick(InventoryClickEvent event) {
        if(displayUi) {
            event.setCancelled(true);
            return;
        }

        Player clicker = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if(slot < 0) return;

        MenuItem item = getItem(slot);
        if(item == null) return;

        if(item.isCancelAction()) {
            event.setCancelled(true);
        }
        if(!item.isCancelAction()) {
            return;
        }
        if(!item.hasAction()){
            return;
        }
        item.performAction(item, clicker, event.getClick());
    }

    public void onClose(Inventory inventory) {}

    public Inventory buildInventory(){
        int size = items.length;
        for(int i = 0; i < size; i++){
            MenuItem button = items[i];
            if(button == null) continue;
            this.inventory.setItem(i, button.getItemStack());
        }
        return inventory;
    }

    public void updateSlot(int slot, Function<MenuItem, MenuItem> itemFunction) {
        MenuItem item = getItem(slot);
        if(item == null) return;
        setSlot(slot, itemFunction.apply(item));

        updateInventory();
    }

    /**
     * This override methods, clones the {@parameter other}
     * @param other The inventory to swap
     */
    @Deprecated
    public void override(Player player, InventoryUI other) {
        for(int i = 0; i < other.getItems().length; i++) {
            MenuItem item = other.getItem(i);
            this.items[i] = item;
        }
        this.open(player);
    }

    public void updateInventory() {
        for(int i = 0; i < items.length; i++) {
            MenuItem button = items[i];
            if(button == null) continue;
            inventory.setItem(i, button.getItemStack());
        }
    }


    public int buildHeader() {
        MenuItem glassItem = new MenuItem(Material.BLACK_STAINED_GLASS_PANE, "&c");

        for(int firstRow = 0; firstRow < 9; firstRow++) {
            setSlot(firstRow, glassItem);
        }

        return 9;
    }

    public String getInventoryId() {
        return "Inventory";
    }

    public abstract void build(Player player);

    public void open(Player player){
        inventory.clear();
        build(player);
        Inventory inventory = buildInventory();
        player.openInventory(inventory);
    }

    public void lock() {
        lock = new Object();
    }
    public void unlock() {
        lock = null;
    }
    public boolean isLocked() {
        return lock != null;
    }
}