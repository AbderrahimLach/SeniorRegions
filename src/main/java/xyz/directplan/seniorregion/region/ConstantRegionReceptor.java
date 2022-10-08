package xyz.directplan.seniorregion.region;

import xyz.directplan.seniorregion.user.User;
import xyz.directplan.seniorregion.user.UserManager;

import java.util.Collection;

/**
 * @author DirectPlan
 */
public record ConstantRegionReceptor(UserManager userManager, RegionManager regionManager) implements Runnable {


    @Override
    public void run() {
        Collection<User> users = userManager.getUsers().values();
        if(users.isEmpty()) return;

        for(User user : users) {
            regionManager.updateRegion(user);
        }
    }
}
