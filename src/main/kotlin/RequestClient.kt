
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import java.nio.file.Paths
import java.time.LocalDate
import java.time.ZoneId

// regex to extract response
const val BASE = "https://adventofcode.com"
val reOptions = setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL)
val articleRe = "<article><p>(.+?)</p></article>".toRegex(reOptions)
val anonIdRe = """\(anonymous user #(\d+)\)""".toRegex()
val linkToRe = "<span>Link to https://(.+)</span></label>".toRegex()
val linksRe = "</?a[^>]*>".toRegex()
val spanRe = "</?span[^>]*>".toRegex()


open class RequestClient {
    private val home = System.getenv("HOME")
    protected val aocdDataDir = System.getenv("AOCD_DIR") ?: Paths.get(home, ".config", "aocd").toString()
    private val aocdConfigDir = System.getenv("AOCD_CONFIG_DIR") ?: aocdDataDir

    // gtet the value of the first capture group. Why is this so hard?
    protected fun Regex.firstGroupVal(toSearch: String): String = findAll(toSearch).first().groups[1]!!.value.trim()

    protected fun token(): String {
        val envToken = System.getenv("AOC_SESSION")
        if(envToken.isNotEmpty())
            return envToken
        val f = Paths.get(aocdConfigDir, "token").toFile()
        if(!f.exists()) {
            println("Token does not exist, please enter it or create a file in ${f.path} containing it")
            val token: String? = readLine()
            checkNotNull(token)
            f.createNewFile()
            f.writeText(token)
            return token
        }
        return f.readText().trim()
    }

    private fun fixDate(day: Int?, year: Int?): Pair<Int, Int> {
        val estDate = LocalDate.now(ZoneId.of("EST", ZoneId.SHORT_IDS))
        check((year ?: estDate.year) >= 2015) { "AOC started in 2015. You tried $year" }
        return Pair(day ?: estDate.dayOfMonth, year ?: estDate.year)
    }

    private fun url(day: Int?, year: Int?, postfix: String): String {
        val dayYear = fixDate(day, year)
        return "$BASE/${dayYear.second}/day/${dayYear.first}/$postfix"
    }

    protected fun Request.getWithToken(token: String): String =
        header(Headers.COOKIE to "session=$token").responseString().third.get()

    fun input() : String = input(null, null)
    fun input(day: Int) : String = input(day, null)
    fun input(day: Int?, year: Int?): String {
        val dayYear = fixDate(day, year)
        return inputInner(dayYear.first, dayYear.second)
    }

    fun submit(answer: String, partId: Int) = submit(answer, partId, null, null)
    fun submit(answer: String, partId: Int, day: Int?) = submit(answer, partId, day, null)
    fun submit(answer: String, partId: Int, day: Int?, year: Int?): String {
        val dayYear = fixDate(day, year)
        return submitInner(answer, "12"[partId], dayYear.first, dayYear.second)
    }

    // same interface as https://github.com/wimglenn/advent-of-code-data/blob/master/aocd/get.py#L25
    open fun inputInner(day: Int, year: Int): String = url(day, year, "input").httpGet().getWithToken(token())

    // same interface as https://github.com/wimglenn/advent-of-code-data/blob/master/aocd/post.py#L19
    open fun submitInner(answer: String, part: Char, day: Int, year: Int): String {
        val fullBody = url(day, year, "answer")
            .httpPost(listOf("level" to part, "answer" to answer))
            .getWithToken(token())
        val withLinksAndSpans = articleRe.firstGroupVal(fullBody)
        return spanRe.replace(linksRe.replace(withLinksAndSpans, ""), "")
    }
}
