package space.kscience.kmath.ast

import space.kscience.kmath.ast.rendering.FeaturedMathRendererWithPostProcess
import space.kscience.kmath.ast.rendering.LatexSyntaxRenderer
import space.kscience.kmath.ast.rendering.MathMLSyntaxRenderer
import space.kscience.kmath.ast.rendering.renderWithStringBuilder

public fun main() {
    val mst = "exp(sqrt(x))-asin(2*x)/(2e10+x^3)/(-12)".parseMath()
    val syntax = FeaturedMathRendererWithPostProcess.Default.render(mst)
    println("MathSyntax:")
    println(syntax)
    println()
    val latex = LatexSyntaxRenderer.renderWithStringBuilder(syntax)
    println("LaTeX:")
    println(latex)
    println()
    val mathML = MathMLSyntaxRenderer.renderWithStringBuilder(syntax)
    println("MathML:")
    println(mathML)
}
