open class AutoParser {
    open fun parse(input: String): Any {
        with(input) {
            for (sep in listOf("\n\n", "\n", " ")) {
                val spl = split(sep)
                if (spl.size > 1)
                    return spl.filter { it.isNotEmpty() }.map { parse(it) }
            }
            return toIntOrNull() ?: input
        }
    }
}
