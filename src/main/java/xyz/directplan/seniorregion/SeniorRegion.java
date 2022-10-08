package xyz.directplan.seniorregion;

import co.aikar.commands.*;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.directplan.seniorregion.config.BukkitConfigHandler;
import xyz.directplan.seniorregion.config.ConfigKeys;
import xyz.directplan.seniorregion.config.MessageConfigKeys;
import xyz.directplan.seniorregion.lib.config.ConfigHandler;
import xyz.directplan.seniorregion.lib.inventory.MenuListener;
import xyz.directplan.seniorregion.lib.inventory.MenuManager;
import xyz.directplan.seniorregion.lib.storage.Storage;
import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.region.RegionCommand;
import xyz.directplan.seniorregion.region.RegionManager;
import xyz.directplan.seniorregion.user.User;
import xyz.directplan.seniorregion.user.UserListener;
import xyz.directplan.seniorregion.user.UserManager;
import xyz.directplan.seniorregion.utility.PluginUtility;

@Getter
public final class SeniorRegion extends JavaPlugin {

    private ConfigHandler configHandler;
    private Storage storage;

    private MenuManager menuManager;
    private UserManager userManager;
    private RegionManager regionManager;

    @Override
    public void onEnable() {
        configHandler = new BukkitConfigHandler(this);
        configHandler.loadConfiguration("config.yml", ConfigKeys.class);

        storage = new Storage(this);
        storage.connect();

        menuManager = new MenuManager();
        userManager = new UserManager(this);
        PluginUtility.registerListeners(this, new UserListener(userManager), new MenuListener(menuManager));

        regionManager = new RegionManager(this);
        regionManager.initialize();

        setupCommands();
    }

    @Override
    public void onDisable() {
        configHandler.saveConfigurations();
        regionManager.shutdown();

        storage.close();
    }

    private void setupCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.enableUnstableAPI("help");

        commandManager.registerDependency(RegionManager.class, regionManager);
        // Command Contexts
        CommandContexts<BukkitCommandExecutionContext> commandContexts = commandManager.getCommandContexts();

        commandContexts.registerIssuerAwareContext(User.class, resolver -> {
            BukkitCommandIssuer commandIssuer = resolver.getIssuer();
            if(resolver.hasFlag("other")) {
                String name = resolver.popFirstArg();
                Player player = ACFBukkitUtil.findPlayerSmart(commandIssuer, name);
                if(player == null) throw new ShowCommandHelp();
                return userManager.getUser(player);
            }
            if(!commandIssuer.isPlayer()) {
                throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE);
            }
            Player player = commandIssuer.getPlayer();
            return userManager.getUser(player);
        });

        commandContexts.registerContext(Region.class, resolver -> {
            String name = resolver.popFirstArg();
            return regionManager.getRegion(name);
        });

        commandManager.registerCommand(new RegionCommand());
    }
}
