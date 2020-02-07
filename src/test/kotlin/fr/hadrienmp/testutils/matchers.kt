package fr.hadrienmp.testutils

import io.kotlintest.matchers.maps.shouldContain

infix fun <K,V> Map<K,V>.shouldContain(entry: Pair<K,V>) {
    this.shouldContain(entry.first, entry.second)
}