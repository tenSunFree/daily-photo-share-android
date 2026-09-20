package com.sun.daily_photo_share_android.core.model

/**
 * An immutable, ordered selection set.
 * List order = sharing order = UI indicators ①②③; membership and order are represented by the same data.
 * Uses a lazy-initialized index for O(1) lookups in contains / orderOf during list recompositions.
 */
data class PhotoSelection(val items: List<MediaUri> = emptyList()) {

    private val orderIndex: Map<MediaUri, Int> by lazy(LazyThreadSafetyMode.NONE) {
        items.withIndex().associate { (index, uri) -> uri to index + 1 }
    }

    val size: Int get() = items.size
    val isEmpty: Boolean get() = items.isEmpty()

    operator fun contains(uri: MediaUri): Boolean = orderIndex.containsKey(uri)

    /** 1-based order; returns null if not selected. */
    fun orderOf(uri: MediaUri): Int? = orderIndex[uri]

    fun toggle(uri: MediaUri): PhotoSelection =
        if (uri in this) PhotoSelection(items - uri) else PhotoSelection(items + uri)

    /** Preserves existing order, appends new items at the end, skips duplicates. */
    fun addAll(uris: Iterable<MediaUri>): PhotoSelection =
        PhotoSelection((items + uris).distinct())

    fun clear(): PhotoSelection = PhotoSelection()
}