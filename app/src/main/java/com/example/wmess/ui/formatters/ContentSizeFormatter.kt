package com.example.wmess.ui.formatters

fun formatContentSize(size: Long): String {
    if (size < 1024) return "$size B"
    if (size < 1024 * 1024) return "${size / 1024} KB"
    return "%.2f MB".format(size / 1024f / 1024f)
}
