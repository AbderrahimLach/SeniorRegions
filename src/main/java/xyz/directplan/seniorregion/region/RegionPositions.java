package xyz.directplan.seniorregion.region;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 * @author DirectPlan
 */
@AllArgsConstructor
@Getter
@Setter
public class RegionPositions implements Cloneable {

    private Location firstPosition, secondPosition;

    public RegionPositions() {
        this(null, null);
    }

    public boolean isSet() {
        return firstPosition != null && secondPosition != null;
    }

    @Override
    public RegionPositions clone() {
        try {
            return (RegionPositions) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
