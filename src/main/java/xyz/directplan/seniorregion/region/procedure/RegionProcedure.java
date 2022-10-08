package xyz.directplan.seniorregion.region.procedure;

import xyz.directplan.seniorregion.user.User;

/**
 * @author DirectPlan
 */
public interface RegionProcedure {

    void prepare(User issuer);

    void execute(User issuer, String input);
}
