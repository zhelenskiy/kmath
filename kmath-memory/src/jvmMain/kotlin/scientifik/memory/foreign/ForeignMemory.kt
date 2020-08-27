package scientifik.memory.foreign

import jdk.incubator.foreign.MemoryHandles
import jdk.incubator.foreign.MemorySegment
import scientifik.memory.*
import java.lang.invoke.VarHandle
import java.nio.ByteOrder

fun Memory.Companion.allocateForeign(length: Int): Memory {
    return ForeignMemory(MemorySegment.allocateNative(length.toLong()))
}

internal class ForeignMemory(val scope: MemorySegment) : Memory, AutoCloseable {
    override val size: Int
        get() = scope.byteSize().toInt()

    private val writer: MemoryWriter = ForeignWriter(this)
    private val reader: MemoryReader = ForeignReader(this)

    override fun view(offset: Int, length: Int): ForeignMemory =
        ForeignMemory(scope.asSlice(offset.toLong(), length.toLong()))

    override fun copy(): Memory {
        val bytes = scope.toByteArray()!!
        val newScope = MemorySegment.allocateNative(scope.byteSize())!!

        var point = newScope.baseAddress()!!

        bytes.forEach {
            byteHandle.set(point, it)
            point = point.addOffset(1)
        }

        return ForeignMemory(newScope)
    }

    override fun reader(): MemoryReader = reader
    override fun writer(): MemoryWriter = writer
    override fun close(): Unit = scope.close()

    internal companion object {
        internal val doubleHandle: VarHandle = MemoryHandles.varHandle(Double::class.java, ByteOrder.nativeOrder())!!
        internal val floatHandle: VarHandle = MemoryHandles.varHandle(Float::class.java, ByteOrder.nativeOrder())!!
        internal val byteHandle: VarHandle = MemoryHandles.varHandle(Byte::class.java, ByteOrder.nativeOrder())!!
        internal val shortHandle: VarHandle = MemoryHandles.varHandle(Short::class.java, ByteOrder.nativeOrder())!!
        internal val intHandle: VarHandle = MemoryHandles.varHandle(Int::class.java, ByteOrder.nativeOrder())!!
        internal val longHandle: VarHandle = MemoryHandles.varHandle(Long::class.java, ByteOrder.nativeOrder())!!
    }
}
