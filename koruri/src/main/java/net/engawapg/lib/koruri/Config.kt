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

package net.engawapg.lib.koruri

/**
 * Configuration object for Koruri audio engine.
 */
public object KoruriAudioConfig {
    /**
     * The sample rate used for audio processing in Koruri.
     */
    public const val SAMPLE_RATE: Int = 48000
    /**
     * The buffer size in frames used for audio processing in Koruri.
     */
    public const val BUFFER_SIZE: Int = 128
}