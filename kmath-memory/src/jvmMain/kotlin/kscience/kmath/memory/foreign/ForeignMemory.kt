package kscience.kmath.memory.foreign

import jdk.incubator.foreign.MemoryHandles
import jdk.incubator.foreign.MemorySegment
import kscience.kmath.memory.Memory
import kscience.kmath.memory.MemoryReader
import kscience.kmath.memory.MemoryWriter
import java.lang.invoke.VarHandle
import java.nio.ByteOrder

/**
 * Allocates memory using JDK Foreign Memory API. It should be even faster than default ByteBuffer memory provided by
 * [Memory.Companion.allocate].
 */
public fun Memory.Companion.allocateAsForeign(length: Int): Memory =
    ForeignMemory(MemorySegment.allocateNative(length.toLong()))

/**
 * Wraps a [Memory] around existing [ByteArray]. This operation is unsafe since the array is not copied
 * and could be mutated independently from the resulting [Memory].
 *
 * The memory is wrapped to JDK Foreign Memory segment.
 */
public fun Memory.Companion.wrapAsForeign(array: ByteArray): Memory = ForeignMemory(MemorySegment.ofArray(array))

internal class ForeignMemory(val scope: MemorySegment) : Memory, AutoCloseable {
    override val size: Int
        get() = scope.byteSize().toInt()

    private val writer: MemoryWriter = ForeignWriter(this)
    private val reader: MemoryReader = ForeignReader(this)

    override fun view(offset: Int, length: Int): ForeignMemory =
        ForeignMemory(scope.asSlice(offset.toLong(), length.toLong()))

    override fun copy(): Memory {
        val newScope = MemorySegment.allocateNative(scope.byteSize())
        newScope.copyFrom(scope)
        return ForeignMemory(newScope)
    }

    override fun reader(): MemoryReader = reader
    override fun writer(): MemoryWriter = writer
    override fun close(): Unit = scope.close()

    internal companion object {
        val shortHandle: VarHandle = MemoryHandles.varHandle(Short::class.javaPrimitiveType, ByteOrder.nativeOrder())
        val doubleHandle: VarHandle = MemoryHandles.varHandle(Double::class.javaPrimitiveType, ByteOrder.nativeOrder())
        val longHandle: VarHandle = MemoryHandles.varHandle(Long::class.javaPrimitiveType, ByteOrder.nativeOrder())
        val intHandle: VarHandle = MemoryHandles.varHandle(Int::class.javaPrimitiveType, ByteOrder.nativeOrder())
        val floatHandle: VarHandle = MemoryHandles.varHandle(Float::class.javaPrimitiveType, ByteOrder.nativeOrder())
        val byteHandle: VarHandle = MemoryHandles.varHandle(Byte::class.javaPrimitiveType, ByteOrder.nativeOrder())
    }
}
