package net.engawapg.lib.koruri

import androidx.compose.runtime.AbstractApplier

internal class KoruriApplier(
    root: KoruriNode,
    private val onChange: () -> Unit,
) : AbstractApplier<KoruriNode>(root) {
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

    override fun onBeginChanges() {
        super.onBeginChanges()
        onChange()
    }
}