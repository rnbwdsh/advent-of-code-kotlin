import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AocdKtTest {
    @Test
    fun test_2015_1_ab() {
        AOCD.solve(day=1, year=2015, testInput="))(((((", testAnswer0="3", testAnswer1="1") { input: String, part2: Boolean ->
            var bracketLevel = 0
            for (i in input.indices) {
                bracketLevel += if (input[i] == '(') 1 else -1
                if (part2 && bracketLevel == -1) return@solve i + 1
            }
            bracketLevel
        }
    }

    @Test
    fun test_2015_2_a() {
        AOCD.solve(2, 2015, "2x3x4\n1x1x10", "101") { input: List<String>, part2 ->
            var total = 0
            for (line in input) {
                val s = line.split("x").map { it.toInt() }
                total += if (part2) {
                    val lw = listOf(s[0] + s[1], s[1] + s[2], s[2] + s[0])
                    lw.minOrNull()!! * 2 + s[0] * s[1] * s[2]
                } else {
                    val lw = listOf(s[0] * s[1], s[1] * s[2], s[2] * s[0])
                    lw.sum() * 2 + lw.minOrNull()!!
                }
            }
            println(total)
            total
        }
    }

    @Test
    fun test_2015_wrong() {
        assertThrows<java.lang.IllegalStateException> {
            AOCD.solve(1, 2015, "))(((((", "3", "1") { _: String, _: Boolean -> 1 }
        }
    }
}
