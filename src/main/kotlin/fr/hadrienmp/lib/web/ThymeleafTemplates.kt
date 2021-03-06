package fr.hadrienmp.lib.web

import io.javalin.plugin.rendering.template.JavalinThymeleaf
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

class ThymeleafTemplates(baseDirectory: String = "") {
    private val templateEngine = templateEngine(baseDirectory)

    private fun templateEngine(baseDirectory: String): TemplateEngine {
        val templateEngine = TemplateEngine()
        templateEngine.setTemplateResolver(templateResolver(baseDirectory))
        return templateEngine
    }

    private fun templateResolver(baseDirectory: String): ClassLoaderTemplateResolver {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.prefix = baseDirectory
        return templateResolver
    }

    fun enable() {
        JavalinThymeleaf.configure(templateEngine)
    }
}