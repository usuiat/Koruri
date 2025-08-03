package net.engawapg.lib.koruri.processor

enum class Pitch(
    val frequency: Float,
) {
    Silence(0.0f),
    C4(261.63f),
    D4(293.66f),
    E4(329.63f),
    F4(349.23f),
    G4(392.00f),
    A4(440.00f),
    B4(493.88f),
    C5(523.25f),
    D5(587.33f),
    E5(659.25f),
    F5(698.46f),
    G5(783.99f),
    A5(880.00f),
    B5(987.77f),
    C6(1046.50f),
    D6(1174.66f),
    E6(1318.51f),
    F6(1396.91f),
    G6(1567.98f),
    A6(1760.00f),
    B6(1975.53f),
    C7(2093.00f),
}