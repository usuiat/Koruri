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

import androidx.compose.runtime.AbstractApplier

internal class KoruriApplier(root: KoruriNode) : AbstractApplier<KoruriNode>(root) {
    override fun insertBottomUp(index: Int, instance: KoruriNode) {
        current.children.add(index, instance)
    }

    override fun insertTopDown(index: Int, instance: KoruriNode) {}

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
    }

    override fun onClear() {}

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
    }
}