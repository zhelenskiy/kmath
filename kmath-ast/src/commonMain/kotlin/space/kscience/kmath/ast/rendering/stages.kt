package space.kscience.kmath.ast.rendering

import space.kscience.kmath.operations.FieldOperations
import space.kscience.kmath.operations.GroupOperations
import space.kscience.kmath.operations.PowerOperations
import space.kscience.kmath.operations.RingOperations

/**
 * Removes unnecessary times (&times;) symbols from [MultiplicationSyntax].
 *
 * @author Iaroslav Postovalov
 */
public object BetterMultiplication : FeaturedMathRendererWithPostProcess.PostProcessStage {
    public override fun perform(node: MathSyntax): Unit = when (node) {
        is NumberSyntax -> Unit
        is SymbolSyntax -> Unit
        is OperatorNameSyntax -> Unit
        is SpecialSymbolSyntax -> Unit
        is OperandSyntax -> perform(node.operand)

        is UnaryOperatorSyntax -> {
            perform(node.prefix)
            perform(node.operand)
        }

        is UnaryPlusSyntax -> perform(node.operand)
        is UnaryMinusSyntax -> perform(node.operand)
        is RadicalSyntax -> perform(node.operand)
        is ExponentialSyntax -> perform(node.operand)

        is SuperscriptSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is SubscriptSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is BinaryOperatorSyntax -> {
            perform(node.prefix)
            perform(node.left)
            perform(node.right)
        }

        is BinaryPlusSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is BinaryMinusSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is FractionSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is RadicalWithIndexSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is MultiplicationSyntax -> {
            node.times = node.right.operand is NumberSyntax && !node.right.parentheses
                    || node.left.operand is NumberSyntax && node.right.operand is FractionSyntax
                    || node.left.operand is NumberSyntax && node.right.operand is NumberSyntax
                    || node.left.operand is NumberSyntax && node.right.operand is SuperscriptSyntax && node.right.operand.left is NumberSyntax

            perform(node.left)
            perform(node.right)
        }
    }
}


/**
 * Applies [ExponentialSyntax.useOperatorForm] to [ExponentialSyntax] when the operand contains a fraction, a
 * superscript or a subscript to improve readability.
 *
 * @author Iaroslav Postovalov
 */
public object BetterExponential : FeaturedMathRendererWithPostProcess.PostProcessStage {
    private fun perform0(node: MathSyntax): Boolean {
        return when (node) {
            is NumberSyntax -> false
            is SymbolSyntax -> false
            is OperatorNameSyntax -> false
            is SpecialSymbolSyntax -> false
            is OperandSyntax -> perform0(node.operand)
            is UnaryOperatorSyntax -> perform0(node.prefix) || perform0(node.operand)
            is UnaryPlusSyntax -> perform0(node.operand)
            is UnaryMinusSyntax -> perform0(node.operand)
            is RadicalSyntax -> perform0(node.operand)

            is ExponentialSyntax -> {
                val r = perform0(node.operand)
                node.useOperatorForm = r
                r
            }

            is SuperscriptSyntax -> true
            is SubscriptSyntax -> true
            is BinaryOperatorSyntax -> perform0(node.prefix) || perform0(node.left) || perform0(node.right)
            is BinaryPlusSyntax -> perform0(node.left) || perform0(node.right)
            is BinaryMinusSyntax -> perform0(node.left) || perform0(node.right)
            is FractionSyntax -> true
            is RadicalWithIndexSyntax -> perform0(node.left) || perform0(node.right)
            is MultiplicationSyntax -> perform0(node.left) || perform0(node.right)
        }
    }

    public override fun perform(node: MathSyntax) {
        perform0(node)
    }
}

/**
 * Removes unnecessary parentheses from [OperandSyntax].
 *
 * @property precedenceFunction Returns the precedence number for syntax node. Higher number is lower priority.
 * @author Iaroslav Postovalov
 */
public class SimplifyParentheses(public val precedenceFunction: (MathSyntax) -> Int) :
    FeaturedMathRendererWithPostProcess.PostProcessStage {
    public override fun perform(node: MathSyntax): Unit = when (node) {
        is NumberSyntax -> Unit
        is SymbolSyntax -> Unit
        is OperatorNameSyntax -> Unit
        is SpecialSymbolSyntax -> Unit

        is OperandSyntax -> {
            val isRightOfSuperscript =
                (node.parent is SuperscriptSyntax) && (node.parent as SuperscriptSyntax).right === node

            val precedence = precedenceFunction(node.operand)

            val needParenthesesByPrecedence = when (val parent = node.parent) {
                null -> false

                is BinarySyntax -> {
                    val parentPrecedence = precedenceFunction(parent)

                    parentPrecedence < precedence ||
                            parentPrecedence == precedence && parentPrecedence != 0 && node === parent.right
                }

                else -> precedence > precedenceFunction(parent)
            }

            val isInsideExpOperator =
                node.parent is ExponentialSyntax && (node.parent as ExponentialSyntax).useOperatorForm

            node.parentheses = !isRightOfSuperscript
                    && (needParenthesesByPrecedence || node.parent is UnaryOperatorSyntax || isInsideExpOperator)

            perform(node.operand)
        }

        is UnaryOperatorSyntax -> {
            perform(node.prefix)
            perform(node.operand)
        }

        is UnaryPlusSyntax -> perform(node.operand)
        is UnaryMinusSyntax -> perform(node.operand)
        is RadicalSyntax -> perform(node.operand)
        is ExponentialSyntax -> perform(node.operand)

        is SuperscriptSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is SubscriptSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is BinaryOperatorSyntax -> {
            perform(node.prefix)
            perform(node.left)
            perform(node.right)
        }

        is BinaryPlusSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is BinaryMinusSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is FractionSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is MultiplicationSyntax -> {
            perform(node.left)
            perform(node.right)
        }

        is RadicalWithIndexSyntax -> {
            perform(node.left)
            perform(node.right)
        }
    }

    public companion object {
        /**
         * The default configuration of [SimplifyParentheses] where power is 1, multiplicative operations are 2,
         * additive operations are 3.
         */
        public val Default: SimplifyParentheses = SimplifyParentheses {
            when (it) {
                is TerminalSyntax -> 0
                is UnarySyntax -> 2

                is BinarySyntax -> when (it.operation) {
                    PowerOperations.POW_OPERATION -> 1
                    RingOperations.TIMES_OPERATION -> 3
                    FieldOperations.DIV_OPERATION -> 3
                    GroupOperations.MINUS_OPERATION -> 4
                    GroupOperations.PLUS_OPERATION -> 4
                    else -> 0
                }

                else -> 0
            }
        }
    }
}
