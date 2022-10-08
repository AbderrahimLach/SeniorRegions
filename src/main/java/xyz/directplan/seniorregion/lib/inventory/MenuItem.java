package xyz.directplan.seniorregion.lib.inventory;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

/**
 * @author DirectPlan
 */
@Getter
public class MenuItem {

    private ItemStack itemStack;
    private final ItemBuilder builder;
    private final String displayName;
    @Setter private Object itemKey;
    @Setter private boolean cancelAction = true;
    @Setter private int slot = -1;
    private int itemAmount;

    @Setter private ActionableItem action;

    public MenuItem(Material type, String displayName) {
        this(type, displayName, 0);
    }

    public MenuItem(Material type, String displayName, int durability){
        this(type, displayName, durability, null);
    }

    public MenuItem(Material type, String displayName, ActionableItem action){
        this(type, displayName, 0, action);
    }

    public MenuItem(Material type, String displayName, int durability, ActionableItem action){
        if(displayName == null) {
            displayName = "";
        }
        this.builder = new ItemBuilder(type).name(displayName);
        if(durability > 0){
            this.builder.durability(durability);
        }
        this.itemStack = builder.build();
        this.displayName = itemStack.getItemMeta().getDisplayName();
        this.action = action;
    }

    public void setMeta(ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
    }

    public void setCustomSkullName(String name) {
        this.itemStack = builder.type(Material.PLAYER_HEAD).skullOwner(name).build();
    }
    public void addEnchantments(ItemEnchantment... enchantments) {
        for(ItemEnchantment enchantment : enchantments) {
            builder.enchantment(enchantment.getEnchantment(), enchantment.getLevel());
        }
        itemStack = builder.build();
    }

    public void addFlags(ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flags);
        itemStack.setItemMeta(meta);
    }

    public void setCompoundKey(String compoundKey) { // I hope this works
        net.minecraft.world.item.ItemStack raw = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tagCompound = raw.v();
        tagCompound.a(compoundKey, compoundKey);
        raw.b(tagCompound);
        itemStack = CraftItemStack.asBukkitCopy(raw);
    }

    public List<String> getLore() {
        return itemStack.getItemMeta().getLore();
    }

    public void setAmount(int amount) {
        this.itemAmount = amount;
        itemStack = builder.amount(amount).build();
    }

    public void setLore(List<String> lore){
        this.itemStack = builder.lore(lore).build();
    }

    public void setLore(String lore) {
        this.itemStack = builder.lore(lore).build();
    }

    public void setDisplayName(String displayName) {
        this.itemStack = builder.name(displayName).build();
    }

    public boolean hasAction(){
        return action != null;
    }

    public void performAction(MenuItem item, Player clicker, ClickType clickType){
        if(this.action != null){
            this.action.performAction(item, clicker, clickType);
        }
    }
}
