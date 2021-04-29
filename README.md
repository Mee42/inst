# inst
An integration testing library for CLI programs

A `*.inst.*` file contains a set of tests, seperated by the (configurable) test deliminator.
By default, it is `^={4,}$`.

Each test contains a set of directives, without a signifigant order. The tests can be executed on the command line, with options to only rerun changed or failed tests.

### Supported Directives:
Name|Description
--|--
`run`/`runner`/`pre`/`prefix`|The first part of the executed command
`arg`/`argument`/`args`|The second part of the executed command
`postfix`/`post`|The last part of the executed command
`name`/`test` | The name of the test
`desc` | The description, more information about the test
`stdin` | The STDIN to pass to the test program
`setup` | Bash commands to run before executing the test


All of these are followed by a `<value>`, which means that it can either be a colon, then the rest of the text (trimmed) to the end of the line
or a single hyphen (after trimming). If it's a single hyphen, then it takes the text content of all lines
until the next line that's a directive (`#`, by default)

Several of these options support default values, such as `run`, `args`, `prefix`, `postfix`, and `setup`. You can set them with:

```
# default <option name> <value>
```
and unset them with
```
# unset default <option name>
```
Here's some examples:
```bash
#test This is a test of the emergancy broadcast system
#desc - 
Testing how cat treats STDIN when using the -n flag
#runner cat
#args -n
#stdin -
hello, world!
I like cats
cats are fuzzy
```
Now, running cat is great, but how do we verify it works as we expected?

#### Assertions
Assertions are what are actually checked. Several things are allowed to be checked, and there's a couple ways to check them.

Supported Assertions:
Name|Description|Default Format
--|--|--
`stdout`|STDOUT|string
`stderr`|STDERR|string
`exitcode`/`exit`/`code`|The exit code of the program|string
`time`/`elapsed`|The time it takes to terminate|time

The syntax for assertions is as follows, the goal being human readability
```bash
# <type> assert <name> <value>
```
The `type` is optional, and will revert to what the chart says. `<value>` is the same, supporting on-the-same-line and on the following lines with `-`.

For example:
```bash
# assert stdout -
     1  hello, world!
     2  I like cats
     3  cats are fuzzy
# assert exit: 0
# assert stderr: 
# assert time <1s
```

The types can be specified. For example, we could rewrite the stdout check to
```bash
# regex assert stdout -
(^ {6}\d+ {2}[^\n]+$)*
```
and while you may consider this uglier, it does work

Here's a list of all supported types:
Name|Description
---|---
`string`|Your basic string comparison
`regex`|The expected value will be used as a regex, and the test will pass if the real value matches the regex
`range`|The real value is parsed as an numbber, the test passes if it fits within the range of the expected value.<br/>The syntax is `(N<x)?<N)` where `N` can be any number, and `<` can be any range operation (`<`, `<=`, `>`, `>=`). <br/>`x` is a literal here to make `0<x<1` easier to read
`time`|Like `range`, but the numbers need to be postfixed with a time unit.
`python`|Launch a python interpreter and evaluate an expression (where `it` is a variable containing the data)

Supported Time units|Prefix
---|---
Seconds|`s`
Minutes|`m`
Milliseconds|`ms`
Hours|`h`
Days|`d`



Here's an example test using `time`:
```bash
# test: Test the sleep command in bash
# runner: bash
# stdin: sleep 2
# assert time: 1900ms < x < 2100ms
```

Or to do custom predicates
```bash
# pre: gcc -x c - 
# post: && ./a.out
# stdin - 
int main() {
    return sizeof(int);
}
# python assert exitcode: it in {4, 8}
```


You can also change the directive prefix. It's suggested you pick something that compiles in your language, so that you can get syntax highlighting support.
`///` might be good for things like C:
```c
/// pre: gcc -x c -
/// post && ./a.out
/// stdin -
int main() {
     return sizeof(int);
}
/// python assert exitcode: it in {4, 8}
```
