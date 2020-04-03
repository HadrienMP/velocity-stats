package fr.hadrienmp.lib.web

import io.javalin.Javalin

class WebApp(private val app: Javalin, private val port: Port) {
    fun start() {
        app.start(port.value())
    }

    fun stop() {
        app.stop()
    }

    fun withRoutes(routes: (Javalin) -> Javalin): WebApp {
        return WebApp(routes(app), port)
    }

    constructor(port: Port, folder: String) : this(Javalin.create { config -> config.addStaticFiles(folder) }, port)
}