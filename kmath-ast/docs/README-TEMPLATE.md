# Module kmath-ast

Abstract syntax tree expression representation and related optimizations.

${features}

${artifact}

## Dynamic expression code generation

### On JVM

`kmath-ast` JVM module supports runtime code generation to eliminate overhead of tree traversal. Code generator builds
a special implementation of `Expression<T>` with implemented `invoke` function.

For example, the following builder:

```kotlin
DoubleField.mstInField { symbol("x") + 2 }.compile()
``` 

… leads to generation of bytecode, which can be decompiled to the following Java class:

```java
package space.kscience.kmath.asm.generated;

import java.util.Map;
import kotlin.jvm.functions.Function2;
import space.kscience.kmath.asm.internal.MapIntrinsics;
import space.kscience.kmath.expressions.Expression;
import space.kscience.kmath.expressions.Symbol;

public final class AsmCompiledExpression_45045_0 implements Expression<Double> {
    private final Object[] constants;

    public final Double invoke(Map<Symbol, ? extends Double> arguments) {
        return (Double)((Function2)this.constants[0]).invoke((Double)MapIntrinsics.getOrFail(arguments, "x"), 2);
    }

    public AsmCompiledExpression_45045_0(Object[] constants) {
        this.constants = constants;
    }
}

```

### Example Usage

This API extends MST and MstExpression, so you may optimize as both of them:

```kotlin
DoubleField.mstInField { symbol("x") + 2 }.compile()
DoubleField.expression("x+2".parseMath())
```

#### Known issues

- The same classes may be generated and loaded twice, so it is recommended to cache compiled expressions to avoid
  class loading overhead.
- This API is not supported by non-dynamic JVM implementations (like TeaVM and GraalVM) because of using class loaders.

### On JS

A similar feature is also available on JS.

```kotlin
DoubleField.mstInField { symbol("x") + 2 }.compile()
``` 

The code above returns expression implemented with such a JS function:

```js
var executable = function (constants, arguments) {
  return constants[1](constants[0](arguments, "x"), 2);
};
```


```kotlin
import space.kscience.kmath.wasm.*

RealField.mstInField { symbol("x") + 2 }.compile()
```

An example of emitted WASM IR in the form of WAT:

```lisp
(func \$executable (param \$0 f64) (result f64)
  (f64.add
    (local.get \$0)
    (f64.const 2)
  )
)
```

#### Known issues

- ESTree expression compilation uses `eval` which can be unavailable in several environments.
- WebAssembly isn't supported by old versions of browsers (see https://webassembly.org/roadmap/).

## Rendering expressions

kmath-ast also includes an extensible engine to display expressions in LaTeX or MathML syntax.

Example usage:

```kotlin
import space.kscience.kmath.ast.*
import space.kscience.kmath.ast.rendering.*

public fun main() {
    val mst = "exp(sqrt(x))-asin(2*x)/(2e10+x^3)/(-12)".parseMath()
    val syntax = FeaturedMathRendererWithPostProcess.Default.render(mst)
    val latex = LatexSyntaxRenderer.renderWithStringBuilder(syntax)
    println("LaTeX:")
    println(latex)
    println()
    val mathML = MathMLSyntaxRenderer.renderWithStringBuilder(syntax)
    println("MathML:")
    println(mathML)
}
```

Result LaTeX:

![](http://chart.googleapis.com/chart?cht=tx&chl=e%5E%7B%5Csqrt%7Bx%7D%7D-%5Cfrac%7B%5Cfrac%7B%5Coperatorname%7Bsin%7D%5E%7B-1%7D%5C,%5Cleft(2%5C,x%5Cright)%7D%7B2%5Ctimes10%5E%7B10%7D%2Bx%5E%7B3%7D%7D%7D%7B-12%7D)

Result MathML (embedding MathML is not allowed by GitHub Markdown):

```html
<mrow><msup><mrow><mi>e</mi></mrow><mrow><msqrt><mi>x</mi></msqrt></mrow></msup><mo>-</mo><mfrac><mrow><mfrac><mrow><msup><mrow><mo>sin</mo></mrow><mrow><mo>-</mo><mn>1</mn></mrow></msup><mspace width="0.167em"></mspace><mfenced open="(" close=")" separators=""><mn>2</mn><mspace width="0.167em"></mspace><mi>x</mi></mfenced></mrow><mrow><mn>2</mn><mo>&times;</mo><msup><mrow><mn>10</mn></mrow><mrow><mn>10</mn></mrow></msup><mo>+</mo><msup><mrow><mi>x</mi></mrow><mrow><mn>3</mn></mrow></msup></mrow></mfrac></mrow><mrow><mo>-</mo><mn>12</mn></mrow></mfrac></mrow>
```

It is also possible to create custom algorithms of render, and even add support of other markup languages
(see API reference).
