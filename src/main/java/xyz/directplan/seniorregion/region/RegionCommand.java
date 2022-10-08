package xyz.directplan.seniorregion.region;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.OfflinePlayer;
import xyz.directplan.seniorregion.user.User;

/**
 * @author DirectPlan
 */
@CommandAlias("region")
public class RegionCommand extends BaseCommand {
    
    @Dependency
    private RegionManager regionManager;

    @HelpCommand
    @Syntax("")
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @Default
    public void onRegion(User user, @Optional Region region) {
        if(region != null) {
            regionManager.openRegionManageMenu(user, region);
            return;
        }
        regionManager.openRegionListMenu(user);
    }

    @Subcommand("create")
    @CommandPermission("region.create")
    @Syntax("<name>")
    public void onRegionCreate(User user, String name) {
        regionManager.createRegion(user, name);
    }

    @Subcommand("delete")
    @CommandPermission("region.delete")
    @Syntax("<name>")
    public void onRegionDelete(User user, Region region) {
        regionManager.deleteRegion(user, region);
    }

    @Subcommand("wand")
    @Syntax("")
    @CommandPermission("region.wand")
    public void onRegionWand(User user) {
        regionManager.giveWand(user);
    }

    @Subcommand("add")
    @CommandPermission("region.add")
    @Syntax("<region> <username>")
    public void onRegionAdd(User user, Region region, OfflinePlayer player) {
        regionManager.addWhitelist(user, region, player);
    }

    @Subcommand("remove")
    @CommandPermission("region.remove")
    @Syntax("<region> <username>")
    public void onRegionRemove(User user, Region region, OfflinePlayer player) {
        regionManager.removeWhitelist(user, region, player);
    }

    @Subcommand("whitelist")
    @CommandPermission("region.whitelist")
    @Syntax("<region>")
    public void onRegionWhitelist(User user, Region region) {
        regionManager.showWhitelistedPlayers(user, region);
    }
}
