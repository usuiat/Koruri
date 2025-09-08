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

package net.engawapg.lib.koruri.processor.generator

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
    C2(65.41f),
    Cs2(69.30f), // C#2
    D2(73.42f),
    Ds2(77.78f), // D#2
    E2(82.41f),
    F2(87.31f),
    Fs2(92.50f), // F#2
    G2(98.00f),
    Gs2(103.83f), // G#2
    A2(110.00f),
    As2(116.54f), // A#2
    B2(123.47f),
    C3(130.81f),
    Cs3(138.59f), // C#3
    D3(146.83f),
    Ds3(155.56f), // D#3
    E3(164.81f),
    F3(174.61f),
    Fs3(185.00f), // F#3
    G3(196.00f),
    Gs3(207.65f), // G#3
    A3(220.00f),
    As3(233.08f), // A#3
    B3(246.94f),
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