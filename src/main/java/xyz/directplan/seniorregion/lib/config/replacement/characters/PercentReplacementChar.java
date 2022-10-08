package xyz.directplan.seniorregion.lib.config.replacement.characters;

import xyz.directplan.seniorregion.lib.config.replacement.ReplacementChar;

/**
 * @author DirectPlan
 */
public class PercentReplacementChar implements ReplacementChar {

    @Override
    public char start() {
        return '%';
    }

    @Override
    public char end() {
        return '%';
    }
}
