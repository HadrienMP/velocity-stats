package fr.hadrienmp.lib.web

class AppArguments(val args: Array<String>) {
    fun find(argument: String) =
            args.firstOrNull() { it.matches(Regex("$argument=.*")) }
                    ?.split("$argument=")
                    ?.get(1)
    fun print() = args.joinToString(",")
}