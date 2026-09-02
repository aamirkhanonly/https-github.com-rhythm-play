package com.example.player

import com.example.model.LyricsLine
import java.util.regex.Pattern

object LyricsParser {
    private val TIME_TAG_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})(?:\\.(\\d{2,3}))?\\]")

    fun parseLrc(lrcText: String): List<LyricsLine> {
        if (lrcText.isBlank()) return emptyList()

        val lines = lrcText.lines()
        val result = mutableListOf<LyricsLine>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val matcher = TIME_TAG_PATTERN.matcher(trimmed)
            if (matcher.find()) {
                val minutes = matcher.group(1)?.toLongOrNull() ?: 0L
                val seconds = matcher.group(2)?.toLongOrNull() ?: 0L
                val millisRaw = matcher.group(3)
                val millis = when {
                    millisRaw == null -> 0L
                    millisRaw.length == 2 -> (millisRaw.toLongOrNull() ?: 0L) * 10
                    else -> (millisRaw.toLongOrNull() ?: 0L)
                }

                val timestampMs = (minutes * 60 + seconds) * 1000 + millis
                val lyricText = trimmed.substring(matcher.end()).trim()
                if (lyricText.isNotEmpty()) {
                    result.add(LyricsLine(timestampMs, lyricText))
                }
            } else if (!trimmed.startsWith("[ti:") && !trimmed.startsWith("[ar:") && !trimmed.startsWith("[al:")) {
                // If line has no timestamp, estimate spaced timestamps
                if (result.isNotEmpty()) {
                    val lastTs = result.last().timestampMs
                    result.add(LyricsLine(lastTs + 4000L, trimmed))
                } else {
                    result.add(LyricsLine(result.size * 4000L, trimmed))
                }
            }
        }

        return result.sortedBy { it.timestampMs }
    }

    fun getActiveLineIndex(lines: List<LyricsLine>, currentPositionMs: Long): Int {
        if (lines.isEmpty()) return -1
        var activeIndex = 0
        for (i in lines.indices) {
            if (lines[i].timestampMs <= currentPositionMs) {
                activeIndex = i
            } else {
                break
            }
        }
        return activeIndex
    }
}
