package biz.aydin.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import java.io.OutputStream

/**
 * Represents a sealed subclass along with its immediate parent.
 * This is used to generate extensions with the correct receiver type for nested hierarchies.
 */
data class SealedSubclassWithParent(
    val subclass: KSClassDeclaration,
    val parent: KSClassDeclaration
)

class StateProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val sealedTypesAnnotatedWithUIState = resolver
            .getSymbolsWithAnnotation("biz.aydin.annotation.UIState")
            .filterIsInstance<KSClassDeclaration>()

        if (!sealedTypesAnnotatedWithUIState.iterator().hasNext()) return emptyList()

        sealedTypesAnnotatedWithUIState.forEach {
            it.accept(Visitor(resolver = resolver), Unit)
        }

        val unableToProcess = sealedTypesAnnotatedWithUIState.filterNot { it.validate() }.toList()
        return unableToProcess
    }

    inner class Visitor(private val resolver: Resolver) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (classDeclaration.isNotSealed()) logger.error(
                "UIState annotation only works for Sealed types.",
                symbol = classDeclaration
            )
            val file = codeGenerator.createNewFile(
                dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
                packageName = classDeclaration.packageName.asString() + ".${
                    classDeclaration.simpleName.getShortName().lowercase()
                }.extensions",
                fileName = "${classDeclaration.simpleName.getShortName()}Extensions"
            )

            file += "package ${classDeclaration.packageName.asString()}.${
                classDeclaration.simpleName.getShortName().lowercase()
            }.extensions"

            file.newLine()

            val className = classDeclaration.qualifiedName!!.asString()

            file += generateInvokeFunction(className)
            findAllSealedSubclassesWithParents(classDeclaration).forEach { subclassWithParent ->
                val receiverType = subclassWithParent.parent.qualifiedName!!.asString()
                file += generateCamelCasedConvenienceFunctionForSubclass(
                    subclassWithParent.subclass,
                    receiverType
                )
            }
            file.close()
            super.visitClassDeclaration(classDeclaration, data)
        }

        private fun findAllSealedSubclassesWithParents(
            clazz: KSClassDeclaration
        ): Sequence<SealedSubclassWithParent> {
            val directSubclasses = clazz.getSealedSubclasses()
            return directSubclasses.flatMap { subclass ->
                // Create entry for this direct subclass with current class as parent
                val entry = sequenceOf(SealedSubclassWithParent(subclass, clazz))
                // Recursively get nested subclasses, where subclass is their parent
                val nestedEntries = findAllSealedSubclassesWithParents(subclass)
                entry + nestedEntries
            }
        }

        private fun generateCamelCasedConvenienceFunctionForSubclass(
            it: KSClassDeclaration,
            className: String
        ): String {
            val functionName = getCamelCasedFunctionName(it)
            val sealedObjectQualifiedName = it.qualifiedName?.asString() ?: return ""
            val function = buildString {
                appendLine()
                appendLine("inline fun $className.$functionName(body: $sealedObjectQualifiedName.() -> Unit): $className {")
                appendLine("\tif (this is $sealedObjectQualifiedName) body()")
                appendLine("\treturn this")
                appendLine("}")
            }
            return function
        }

        private fun getCamelCasedFunctionName(ksClassDeclaration: KSClassDeclaration) =
            ksClassDeclaration.simpleName.getShortName().substring(0, 1)
                .lowercase() + ksClassDeclaration.simpleName.getShortName().substring(1)

        private fun generateInvokeFunction(className: String): String {
            val invokeFunction = buildString {
                appendLine()
                appendLine("inline operator fun $className.invoke(body: $className.() -> Unit) {")
                appendLine("\tbody()")
                appendLine("}")
            }
            return invokeFunction
        }
    }

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    fun KSClassDeclaration.isNotSealed(): Boolean = modifiers.none { it.name == "SEALED" }

    fun OutputStream.newLine() {
        this += "\n"
    }
}
