package hs.elementPlugin.core;

public final class Constants {
    // Timing
    public static final long TICKS_PER_SECOND = 20L;
    public static final long HALF_SECOND = 10L;
    public static final long TWO_SECONDS = 40L;
    public static final long FIVE_SECONDS = 100L;

    // Mana
    public static final int DEFAULT_MAX_MANA = 100;
    public static final int DEFAULT_MANA_REGEN = 1;

    // Health
    public static final double NORMAL_MAX_HEALTH = 20.0;
    public static final double LIFE_MAX_HEALTH = 30.0;

    // Animation
    public static final int ROLL_STEPS = 16;
    public static final long ROLL_DELAY_TICKS = 3L;

    // Double-tap detection
    public static final long DOUBLE_TAP_THRESHOLD_MS = 250L;
    public static final long TAP_CHECK_DELAY = 6L;
    public static final long TAP_CLEANUP_DELAY = 2L;

    private Constants() {}
}