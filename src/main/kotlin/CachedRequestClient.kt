import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.nio.file.Path
import java.nio.file.Paths

private const val RIGHT = "That's the right answer!"
private const val COMPLETE = "You don't seem to be solving the right level.  Did you already complete it?"

open class CachedRequestClient : RequestClient() {
    private val gson = Gson()

    private fun Int.pad2(): String = toString().padStart(2, '0')

    private fun idPath(): String {
        val token2idFile = Paths.get(super.aocdDataDir, "token2id.json").toFile()
        if(!token2idFile.exists()) {
            token2idFile.createNewFile()
            token2idFile.writeText("{}")
        }
        val json2id = gson.fromJson(token2idFile.reader(), JsonObject::class.java)  // empty file leads to null
        return if (json2id.has(token()))
            Path.of(aocdDataDir, json2id.get(token()).asString).toString()
        else {
            val resp = "$BASE/settings".httpGet().getWithToken(token())
            val anonId = anonIdRe.firstGroupVal(resp)
            val linkTo = linkToRe.firstGroupVal(resp).split(".com/", limit = 2).joinToString(".")
            val id = "$linkTo.$anonId"
            json2id.addProperty(token(), id)

            // write and close afterwards, so it's really written
            val w = token2idFile.writer()
            gson.toJson(json2id, w)
            w.close()

            val idPath = Path.of(aocdDataDir, id)
            idPath.toFile().mkdirs()  // create id directory
            println("Created $idPath and added to ${token2idFile.path}")
            return idPath.toString()
        }
    }

    override fun inputInner(day: Int, year: Int): String {
        val inputFile = Path.of(idPath(), "${year}_${day.pad2()}_input.txt").toFile()
        return if (inputFile.exists()) {
            inputFile.readText()
        } else {
            val resp = super.inputInner(day, year)
            inputFile.createNewFile()
            inputFile.writeText(resp)
            resp
        }
    }

    override fun submitInner(answer: String, part: Char, day: Int, year: Int): String {
        val aOrB = if (part == '1') "a" else "b"

        val correctFile = Path.of(idPath(), "${year}_${day.pad2()}${aOrB}_answer.txt").toFile()
        val badFile = Path.of(idPath(), "${year}_${day.pad2()}${aOrB}_bad_answers.txt").toFile()
        if (correctFile.exists()) {
            val correct = correctFile.readText()
            check(answer == correct) { "You submitted a different answer before" }
            return "[cached] correct"  // early exit
        } else if (badFile.exists())
            badFile.readLines().filter { it.isNotEmpty() }.forEach {
                check(!it.startsWith("$answer ")) { "You already gave this wrong answer. The response was $it" }
            }

        // handle response
        val resp = super.submitInner(answer, part, day, year)
        if (resp.startsWith(RIGHT)) {
            correctFile.createNewFile()
            correctFile.writeText(answer)
        } else if (resp.startsWith(COMPLETE)) {
            print("Can't save as correct/incorrect, due to\t\t")
        } else {
            if (!badFile.exists())
                badFile.createNewFile()
            badFile.appendText("$answer $resp\n")
        }
        return resp
    }
}
