package org.derekn.p2pSim;

/**
 * Defines constant values used throughout the simulation.
 * Includes commonly used data size definitions and default tick duration.
 */
public class Constants {

    // Sizes in bytes
    public static final long GB = 1_073_741_824L; // (2^30)
    public static final long MB = 1_048_576L;     // (2^20)
    public static final long KB = 1_024L;         // (2^10)

    // Default simulation tick duration in milliseconds
    // Controls the pace of the simulation cycle
    public static final long DEFAULT_TICK_DUR_MS = 500;
}
