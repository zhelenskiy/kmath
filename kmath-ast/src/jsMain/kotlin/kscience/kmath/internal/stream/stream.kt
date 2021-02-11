package kscience.kmath.internal.stream

import kscience.kmath.internal.emitter.Emitter

internal open external class Stream : Emitter {
    open fun pipe(dest: Any, options: Any): Any
}
