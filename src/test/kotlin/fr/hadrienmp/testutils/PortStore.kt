package fr.hadrienmp.testutils

import fr.hadrienmp.lib.web.Port

class PortStore {
    private var used: MutableSet<Port> = mutableSetOf()

    fun get(): Port {
        val freePort = findFreePort()
        used.add(freePort)
        return freePort
    }

    private fun findFreePort(): Port {
        var port = Port(8081)
        while (used.contains(port)) {
            port = Port(port.value() + 1)
        }
        return port
    }

    fun free(port: Port) {
        used.remove(port)
    }
}