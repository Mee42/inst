/// global runner: node -
/// test: adding two integers returns their sum, proces.exit with that value
/// stdin -
process.exit(1 + 1)
/// assert exit: 2
/// assert stderr empty

//////////

/// test: console.log prints to stdout, with a space deliminator
/// stdin -
console.log(1, 7, "hello")
/// assert stdout: 1 7 hello

//////////

/// test: error on unmatched paren
/// stdin: (
/// assert exit: 1
/// assert stderr contains: SyntaxError: Unexpected end of input

////////////

///