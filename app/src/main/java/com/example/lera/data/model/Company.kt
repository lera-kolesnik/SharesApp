package com.example.lera.data.model


import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Company (
        @Id var id: Long = 0,
        var symbol: String? = null,
        var name: String? = null,
        var url: String? = if (symbol != null) {
                "https://eodhistoricaldata.com/img/logos/US/$symbol.png"
        } else {
                null
        },
        var price: Double? = null,
        var priceChange: Double? = null,
        var changePercent: Double? = null
)