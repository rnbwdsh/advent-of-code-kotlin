// look mum, no imports

object AOCD {
    var quiet = false
    var cachedRequestClient = CachedRequestClient()
    var autoParser = AutoParser()

    // override function with only one test answer
    inline fun <reified T> solve(
        day: Int?,
        year: Int?,
        testInput: String,
        testAnswer0: String?,
        solver: (T, Boolean) -> Any,
    ) = solve(day, year, testInput, testAnswer0, null, solver)

    // kotlin-style interface where the last param is a lambda
    inline fun <reified T> solve(
        day: Int?,
        year: Int?,
        testInput: String,
        testAnswer0: String?,
        testAnswer1: String?,
        solver: (T, Boolean) -> Any,
    ) {
        val realInput = cachedRequestClient.input(day, year)
        val testAnswers = listOf(testAnswer0, testAnswer1)

        for (partId in listOf(0, 1)) {
            val testAnswer = testAnswers[partId]  // nullable
            if (!testAnswer.isNullOrEmpty()) {
                val tres = parseCheckSolve(testInput, solver, partId == 1)
                check(tres == testAnswer) { "\nTest for $year / $day / $partId failed. $tres != $testAnswer" }
            }

            val solution = parseCheckSolve(realInput, solver, partId == 1)  // run real check
            if (!quiet) print("Submitting '$solution' for year=$year / day=$day / part=$partId\t\t")
            val response = cachedRequestClient.submit(solution, partId, day, year)
            if (!quiet) println(response)
        }
    }

    inline fun <reified T> parseCheckSolve(input: String, solver: (T, Boolean) -> Any, part: Boolean): String =
        solver((if (String is T) input else autoParser.parse(input)) as T, part).toString()
}
