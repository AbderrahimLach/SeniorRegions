package xyz.directplan.seniorregion.region.procedure;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.RegionManager;
import xyz.directplan.seniorregion.user.User;

/**
 * @author DirectPlan
 */
public record WhitelistAddRegionProcedure(RegionManager regionManager, Region region) implements RegionProcedure {

    @Override
    public void prepare(User issuer) {
        issuer.sendMessage(" ");
        issuer.sendMessage("&aPlease type the name of the player you wish to add:");
        issuer.sendMessage(" ");
    }

    @Override
    public void execute(User user, String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if(!player.hasPlayedBefore()) {
            user.sendMessage("&cThis player has never joined the server.");
            return;
        }
        regionManager.addWhitelist(user, region, player);
    }
}
