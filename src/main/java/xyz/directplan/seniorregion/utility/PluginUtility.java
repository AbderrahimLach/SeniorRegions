package xyz.directplan.seniorregion.utility;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author DirectPlan
 */
public class PluginUtility {

    public static boolean isInRegion(Location source, Location bound1, Location bound2) {
        return source.getX() >= Math.min(bound1.getX(), bound2.getX()) &&
                source.getY() >= Math.min(bound1.getY(), bound2.getY()) &&
                source.getZ() >= Math.min(bound1.getZ(), bound2.getZ()) &&
                source.getX() <= Math.max(bound1.getX(), bound2.getX()) &&
                source.getY() <= Math.max(bound1.getY(), bound2.getY()) &&
                source.getZ() <= Math.max(bound1.getZ(), bound2.getZ());
    }

    public static String translateMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static boolean hasItemNBTKey(ItemStack itemStack, String tag) {
        net.minecraft.world.item.ItemStack raw = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = raw.v();
        if(compound == null) return false;
        return compound.e(tag);
    }

    public static void registerListeners(JavaPlugin plugin, Listener... listeners) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        for(Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }
}
