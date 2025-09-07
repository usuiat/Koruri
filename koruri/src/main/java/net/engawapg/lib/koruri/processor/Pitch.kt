/*
 * Copyright 2025 usuiat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.engawapg.lib.koruri.processor

/**
 * Enum representing musical pitches and their corresponding frequencies.
 *
 * @property frequency The frequency (Hz) of the pitch.
 */
public enum class Pitch(
    public val frequency: Float,
) {
    /** Represents silence, with a frequency of 0 Hz. */
    Silence(0.0f),
    C4(261.63f),
    D4(293.66f),
    E4(329.63f),
    F4(349.23f),
    G4(392.00f),
    A4(440.00f),
    B4(493.88f),
    C5(523.25f),
    Cs5(554.37f), // C#5
    D5(587.33f),
    Ds5(622.25f), // D#5
    E5(659.25f),
    F5(698.46f),
    Fs5(739.99f), // F#5
    G5(783.99f),
    Gs5(830.61f), // G#5
    A5(880.00f),
    As5(932.33f), // A#5
    B5(987.77f),
    C6(1046.50f),
    Cs6(1108.73f), // C#6
    D6(1174.66f),
    Ds6(1244.51f), // D#6
    E6(1318.51f),
    F6(1396.91f),
    G6(1567.98f),
    A6(1760.00f),
    B6(1975.53f),
    C7(2093.00f),
}