package dev.gravesown.entity;

/**
 * Immutable biological profiles for the planet's native land fauna. Registry
 * ids intentionally live beside their behavior/stats so entity registration,
 * rendering and tests all consume one source of truth.
 */
public enum NativeFaunaSpecies {
    ASH_HOPPER("ash_hopper", Archetype.PREY, 8.0D, 0.34D, 0.0D, 14.0D, 0.55F, 0.62F, 0.46F, false),
    GRAVEWING("gravewing", Archetype.FLYER, 6.0D, 0.30D, 0.0D, 16.0D, 0.62F, 0.58F, 0.42F, false),
    ROOTBACK("rootback", Archetype.NEUTRAL, 30.0D, 0.20D, 5.0D, 18.0D, 1.35F, 1.05F, 0.78F, false),
    BARK_MARTEN("bark_marten", Archetype.PREDATOR, 12.0D, 0.34D, 3.0D, 18.0D, 0.82F, 0.58F, 0.38F, false),
    CRAG_RAM("crag_ram", Archetype.NEUTRAL, 28.0D, 0.24D, 5.0D, 18.0D, 1.10F, 1.28F, 1.02F, false),
    RIFT_PUMA("rift_puma", Archetype.PREDATOR, 34.0D, 0.31D, 7.0D, 24.0D, 1.25F, 1.05F, 0.78F, true),
    MIRE_TOAD("mire_toad", Archetype.PREY, 7.0D, 0.25D, 0.0D, 12.0D, 0.68F, 0.42F, 0.28F, false),
    REED_LYNX("reed_lynx", Archetype.PREDATOR, 25.0D, 0.30D, 6.0D, 22.0D, 1.05F, 0.92F, 0.68F, true),
    EMBER_FOX("ember_fox", Archetype.PREDATOR, 14.0D, 0.33D, 4.0D, 18.0D, 0.82F, 0.70F, 0.48F, false),
    CINDER_FOWL("cinder_fowl", Archetype.FLYER, 8.0D, 0.27D, 0.0D, 14.0D, 0.68F, 0.78F, 0.52F, false),
    PALLID_HART("pallid_hart", Archetype.PREY, 26.0D, 0.29D, 0.0D, 20.0D, 1.15F, 1.65F, 1.35F, false),
    MOSSBOAR("mossboar", Archetype.NEUTRAL, 32.0D, 0.23D, 6.0D, 18.0D, 1.25F, 1.05F, 0.76F, false),
    AMBER_JAY("amber_jay", Archetype.FLYER, 6.0D, 0.31D, 0.0D, 16.0D, 0.58F, 0.58F, 0.40F, false),
    SUNHORN("sunhorn", Archetype.PREY, 30.0D, 0.27D, 0.0D, 22.0D, 1.20F, 1.72F, 1.40F, false);

    private final String id;
    private final Archetype archetype;
    private final double health;
    private final double movementSpeed;
    private final double attackDamage;
    private final double followRange;
    private final float width;
    private final float height;
    private final float eyeHeight;
    private final boolean aggressiveToPlayers;

    NativeFaunaSpecies(
            String id,
            Archetype archetype,
            double health,
            double movementSpeed,
            double attackDamage,
            double followRange,
            float width,
            float height,
            float eyeHeight,
            boolean aggressiveToPlayers
    ) {
        this.id = id;
        this.archetype = archetype;
        this.health = health;
        this.movementSpeed = movementSpeed;
        this.attackDamage = attackDamage;
        this.followRange = followRange;
        this.width = width;
        this.height = height;
        this.eyeHeight = eyeHeight;
        this.aggressiveToPlayers = aggressiveToPlayers;
    }

    public String id() {
        return this.id;
    }

    public Archetype archetype() {
        return this.archetype;
    }

    public double health() {
        return this.health;
    }

    public double movementSpeed() {
        return this.movementSpeed;
    }

    public double attackDamage() {
        return this.attackDamage;
    }

    public double followRange() {
        return this.followRange;
    }

    public float width() {
        return this.width;
    }

    public float height() {
        return this.height;
    }

    public float eyeHeight() {
        return this.eyeHeight;
    }

    public boolean aggressiveToPlayers() {
        return this.aggressiveToPlayers;
    }

    public boolean isPrey() {
        return this.archetype == Archetype.PREY || this.archetype == Archetype.FLYER;
    }

    public enum Archetype {
        PREY,
        FLYER,
        NEUTRAL,
        PREDATOR
    }
}
