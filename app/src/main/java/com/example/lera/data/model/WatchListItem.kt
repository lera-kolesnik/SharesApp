package com.example.lera.data.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Data entity to hold data about watch list item.
 * TODO Further extend logic to render saved items.
 */
@Entity
data class WatchListItem (
    @Id var id: Long = 0,
    var symbol: String? = null,
    var name: String? = null,
    var previousClose: Double? = 0.0,
    var open: Double? = 0.0,
    var high: Double? = 0.0,
    var low: Double? = 0.0,
    var volume: Double? = 0.0,
    var change: Double? = 0.0
)
