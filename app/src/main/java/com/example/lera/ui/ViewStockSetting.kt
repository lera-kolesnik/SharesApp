package com.example.lera.ui

enum class ViewStockSetting(
    val inputDateFormat: String,
    val outputDateFormat: String
) {
    INTRA_DAY(
        inputDateFormat="yyyy-MM-dd HH:mm:ss",
        outputDateFormat="HH:mm dd MMM"
    ),
    INTRA_WEEKLY(
        inputDateFormat="yyyy-MM-dd",
        outputDateFormat="dd MMM yyyy"
    ),
    INTRA_MONTHLY(
        inputDateFormat="yyyy-MM-dd",
        outputDateFormat="dd MMM yyyy"
    ),
    ALL(
        inputDateFormat="yyyy-MM-dd",
        outputDateFormat="dd MMM yyyy"
    )
}