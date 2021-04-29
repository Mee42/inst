import java.io.File
import java.lang.RuntimeException

class Config {

   val testSeparator = Regex("""={3,}""")

   val directiveStart = Regex("""///""")

}

fun main() {
   // should be cli args
   val inFile = File("res/tests1.inst.js")
   val config = Config()

   val lines = inFile.readLines()
   val tests = lines.splitBy { it matches config.testSeparator }
   val testsParsed = tests.map { parseTest(it, config) }
}


class UserError(str: String): RuntimeException(str)
fun userError(str: String): Nothing = throw UserError(str)



object DefaultParsingData {
   var runner: String? = null
   var arguments: String = ""
}

data class Test(
   val name: String? = null,
   val desc: String? = null,
   val stdin: String = "",
   val runner: String = DefaultParsingData.runner ?: userError("can't find runner!"),
   val arguments: String = DefaultParsingData.arguments,
)

fun parseTest(lines: List<String>, config: Config) {
   // we'll go line by line
   var test = Test()
   for(line in lines) {
      if(line.isBlank()) continue
      if(!(line matches config.directiveStart)) {
         userError("Found a line that doesn't match the directive start regex (${config.directiveStart}, not sure why: \"$line\"")
      }
      val directive = config.directiveStart.replaceFirst(line, "")
      // directive

   }
}
/*
 supported directives:

<value> means that it can either be a colon, then the rest of the text (trimmed) to the end of the line
or a single hyphen (after trimming). If it's a single hyphen, then it takes the text content of all lines
until the next line that matches directiveStart

a union (|) is just an "either works" operator



# runner|run <value>
# arguments|arg|args| <value>
# name <value>
# desc <value>
# stdin <value>
# pre <value>

The command to run the test is `$runner $arguments`, then parsed by bash.

stdin is what is passed as stdin

pre is bash commands that are ran before executing, for example, setting environmental variables



Several of these options support default values. You can set them with
# default <option name> <value>
and unset them with
# unset default <option name>


assertions are what are actually checked. Several things are allowed to be checked, and there's a couple ways to check them.
things that can be checked:



 */



fun <T> List<T>.splitBy(predicate: (T) -> Boolean): List<List<T>> {
   val ret = mutableListOf<List<T>>()
   var current = mutableListOf<T>()
   for(elem in this) {
      if(predicate(elem)) {
         ret += current
         current = mutableListOf()
      } else {
         current += elem
      }
   }
   return ret.filter { it.isEmpty() }
}