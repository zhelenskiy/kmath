package scientifik.memory.foreign

import jdk.incubator.foreign.MemoryHandles
import jdk.incubator.foreign.MemorySegment
import scientifik.memory.*
import java.lang.invoke.VarHandle
import java.nio.ByteOrder

fun Memory.Companion.allocateForeign(length: Int): Memory = ForeignMemory(MemorySegment.allocateNative(length.toLong()))

internal class ForeignMemory(val scope: MemorySegment) : Memory, AutoCloseable {
    override val size: Int
        get() = scope.byteSize().toInt()

    private val writer: MemoryWriter = ForeignWriter(this)
    private val reader: MemoryReader = ForeignReader(this)

    override fun view(offset: Int, length: Int): ForeignMemory =
        ForeignMemory(scope.asSlice(offset.toLong(), length.toLong()))

    override fun copy(): Memory {
        val newScope = MemorySegment.allocateNative(scope.byteSize())!!
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
