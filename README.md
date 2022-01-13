# Advent of code data for kotlin

This is a port of the [advent-of-code-data python package](https://pypi.org/project/advent-of-code-data/), also known as aocd.py.

It's a submitter-lib for [adventofcode.com](https://adventofcode.com/) by [Eric Wastl](https://github.com/topaz)

If you know it already, let intellisense guide you.

# Usage with lambdas / anonymous functions

```kotlin
AOCD.solve(day=1, year=2015, testInput="))(((((", testAnswer0="3", testAnswer1="1") { input: String, part2: Boolean ->
    var bracketLevel = 0
    for (i in input.indices) {
        bracketLevel += if (input[i] == '(') 1 else -1
        if (part2 && bracketLevel == -1) return@solve i + 1
    }
    bracketLevel
}
```

# Usage with CachedRequestClient

The suggested client is the CachedRequestClient, as it caches files to `AOCD_DIR/<username>.<loginprovider>.<anonid>` the same way aocd.py does.

```kotlin
val crc = CachedRequestClient()  // if you don't like to submit the same wrong answer 10x, use RequestClient
val input: String = crc.input(day=1, year=2015)
val answer = magic() // solve
crc.submit(answer, part=0, day=1, year=2015)  // submit for part 1
```

Input/Submit: No day/year params = today. One param = this year, day x.

This will fail if it's not december.

```kotlin
val crc = RequestClient()
val input: String = crc.input()
val answer = magic() // solve
crc.submit(answer, part=0)  // submit for part 1
```

# Differences to aocd

+ A cool recursive [automagic parser](src/main/kotlin/AutoParser.kt), that can give you `SI = String | Integer`, `List[SI]`, `List[List[SI]]`, `List[List[List[String]]]` depending on the "\n\n", "\n", " " and inner types. It will even check it with the type of your input parameter.
+ 1/5th of the code. (223 lines / 877 words / 9863 chars) vs (1436 lines / 4758 words / 49822 chars)

- No [blocker](https://github.com/wimglenn/advent-of-code-data/blob/master/aocd/utils.py#L34) that only submits if you are allowed to. Yet.
- No "object-oriented "puzzle" interface, but a `CachedRequestClient`

# Setup

Same as the original lib. Set the `AOC_SESSION` environment variable or your token-file in `AOCD_DIR` (defaults to `~/.config/aocd/token`).

If no token is set, the stdin will ask you, and it will be set for you. Otherwise, set it like this.
```
export AOC_SESSION=cafef00db01dfaceba5eba11deadbeef
echo cafef00db01dfaceba5eba11deadbeef > ~/.config/aocd/token
```

# Installation

`git clone `

# Requirements
Just use your own local gradle. Requirements are
* com.github.kittinunf.fuel for HTTP requests
* com.google.code.gson for JSON token parsing
* org.junit.jupiter for tests

Load / install with `gradle dependencies`

# License
The original python can be found at [GitHub](https://github.com/wimglenn/advent-of-code-data/tree/master/) and is MIT-Licensed, so this project is too.
