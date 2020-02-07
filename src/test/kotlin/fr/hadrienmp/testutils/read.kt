package fr.hadrienmp.testutils

fun read(path: String) = Thread
        .currentThread()
        .contextClassLoader
        .getResourceAsStream(path)
        .reader()
        .readText()