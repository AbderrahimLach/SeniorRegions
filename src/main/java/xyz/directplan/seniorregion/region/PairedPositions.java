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
public class PairedPositions implements Cloneable {

    private Location firstPosition, secondPosition;

    public PairedPositions() {
        this(null, null);
    }

    public boolean isSet() {
        return firstPosition != null && secondPosition != null;
    }

    @Override
    public PairedPositions clone() {
        try {
            return (PairedPositions) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
