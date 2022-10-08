package xyz.directplan.seniorregion.region.procedure;

import xyz.directplan.seniorregion.region.Region;
import xyz.directplan.seniorregion.user.User;

/**
 * @author DirectPlan
 */
public record RenameRegionProcedure(Region region) implements RegionProcedure {

    @Override
    public void prepare(User issuer) {
        issuer.sendMessage(" ");
        issuer.sendMessage("&aPlease type a new name for your region:");
        issuer.sendMessage(" ");
    }

    @Override
    public void execute(User user, String name) {
        region.setName(name);
    }
}
