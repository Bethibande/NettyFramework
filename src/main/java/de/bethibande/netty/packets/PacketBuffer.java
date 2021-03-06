package de.bethibande.netty.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PacketBuffer extends ByteBuf {

    public static PacketBuffer wrap(ByteBuf buf) {
        return new PacketBuffer(buf);
    }
    
    private final ByteBuf wrapped;
    
    protected PacketBuffer(ByteBuf wrap) {
        this.wrapped = wrap;
    }
    
    @Override
    public int maxFastWritableBytes() {
        return this.wrapped.maxFastWritableBytes();
    }

    @Override
    public float getFloatLE(int index) {
        return this.wrapped.getFloatLE(index);
    }

    @Override
    public double getDoubleLE(int index) {
        return this.wrapped.getDoubleLE(index);
    }

    @Override
    public ByteBuf setFloatLE(int index, float value) {
        return this.wrapped.setFloatLE(index, value);
    }

    @Override
    public ByteBuf setDoubleLE(int index, double value) {
        return this.wrapped.setDoubleLE(index, value);
    }

    @Override
    public float readFloatLE() {
        return this.wrapped.readFloatLE();
    }

    @Override
    public double readDoubleLE() {
        return this.wrapped.readDoubleLE();
    }

    @Override
    public ByteBuf writeFloatLE(float value) {
        return this.wrapped.writeFloatLE(value);
    }

    @Override
    public ByteBuf writeDoubleLE(double value) {
        return this.wrapped.writeDoubleLE(value);
    }

    @Override
    public boolean isContiguous() {
        return this.wrapped.isContiguous();
    }

    @Override
    public ByteBuf asByteBuf() {
        return this.wrapped.asByteBuf();
    }

    @Override
    public int capacity() {
        return this.wrapped.capacity();
    }

    @Override
    public ByteBuf capacity(int i) {
        return this.wrapped.capacity(i);
    }

    @Override
    public int maxCapacity() {
        return this.wrapped.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.wrapped.alloc();
    }

    @Override
    public ByteOrder order() {
        return this.wrapped.order();
    }

    @Override
    public ByteBuf order(ByteOrder byteOrder) {
        return this.wrapped.order(byteOrder);
    }

    @Override
    public ByteBuf unwrap() {
        return this.wrapped.unwrap();
    }

    @Override
    public boolean isDirect() {
        return this.wrapped.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return this.wrapped.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.wrapped.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return this.wrapped.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int i) {
        return this.wrapped.readerIndex(i);
    }

    @Override
    public int writerIndex() {
        return this.wrapped.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int i) {
        return this.wrapped.writerIndex(i);
    }

    @Override
    public ByteBuf setIndex(int i, int i1) {
        return this.wrapped.setInt(i, i1);
    }

    @Override
    public int readableBytes() {
        return this.wrapped.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.wrapped.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.wrapped.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.wrapped.isReadable();
    }

    @Override
    public boolean isReadable(int i) {
        return this.wrapped.isReadable(i);
    }

    @Override
    public boolean isWritable() {
        return this.wrapped.isWritable();
    }

    @Override
    public boolean isWritable(int i) {
        return this.wrapped.isWritable(i);
    }

    @Override
    public ByteBuf clear() {
        return this.wrapped.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this.wrapped.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this.wrapped.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this.wrapped.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this.wrapped.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return this.wrapped.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return this.wrapped.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int i) {
        return this.wrapped.ensureWritable(i);
    }

    @Override
    public int ensureWritable(int i, boolean b) {
        return this.wrapped.ensureWritable(i, b);
    }

    @Override
    public boolean getBoolean(int i) {
        return this.wrapped.getBoolean(i);
    }

    @Override
    public byte getByte(int i) {
        return this.wrapped.getByte(i);
    }

    @Override
    public short getUnsignedByte(int i) {
        return this.wrapped.getUnsignedByte(i);
    }

    @Override
    public short getShort(int i) {
        return this.wrapped.getShort(i);
    }

    @Override
    public short getShortLE(int i) {
        return this.wrapped.getShortLE(i);
    }

    @Override
    public int getUnsignedShort(int i) {
        return this.wrapped.getUnsignedShort(i);
    }

    @Override
    public int getUnsignedShortLE(int i) {
        return this.wrapped.getUnsignedShortLE(i);
    }

    @Override
    public int getMedium(int i) {
        return this.wrapped.getMedium(i);
    }

    @Override
    public int getMediumLE(int i) {
        return this.wrapped.getMediumLE(i);
    }

    @Override
    public int getUnsignedMedium(int i) {
        return this.wrapped.getUnsignedMedium(i);
    }

    @Override
    public int getUnsignedMediumLE(int i) {
        return this.wrapped.getUnsignedMediumLE(i);
    }

    @Override
    public int getInt(int i) {
        return this.wrapped.getInt(i);
    }

    @Override
    public int getIntLE(int i) {
        return this.wrapped.getIntLE(i);
    }

    @Override
    public long getUnsignedInt(int i) {
        return this.wrapped.getUnsignedInt(i);
    }

    @Override
    public long getUnsignedIntLE(int i) {
        return this.wrapped.getUnsignedIntLE(i);
    }

    @Override
    public long getLong(int i) {
        return this.wrapped.getLong(i);
    }

    @Override
    public long getLongLE(int i) {
        return this.wrapped.getLongLE(i);
    }

    @Override
    public char getChar(int i) {
        return this.wrapped.getChar(i);
    }

    @Override
    public float getFloat(int i) {
        return this.wrapped.getFloat(i);
    }

    @Override
    public double getDouble(int i) {
        return this.wrapped.getDouble(i);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf) {
        return this.wrapped.getBytes(i, byteBuf);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf, int i1) {
        return this.wrapped.getBytes(i, byteBuf, i1);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf, int i1, int i2) {
        return this.wrapped.getBytes(i, byteBuf, i1, i2);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] bytes) {
        return this.wrapped.getBytes(i, bytes);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] bytes, int i1, int i2) {
        return this.wrapped.getBytes(i, bytes, i1, i2);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
        return this.wrapped.getBytes(i, byteBuffer);
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputStream, int i1) throws IOException {
        return this.wrapped.getBytes(i, outputStream, i1);
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int i1) throws IOException {
        return this.wrapped.getBytes(i, gatheringByteChannel, i1);
    }

    @Override
    public int getBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
        return this.wrapped.getBytes(i, fileChannel, l, i1);
    }

    @Override
    public CharSequence getCharSequence(int i, int i1, Charset charset) {
        return this.wrapped.getCharSequence(i, i1, charset);
    }

    @Override
    public ByteBuf setBoolean(int i, boolean b) {
        return this.wrapped.setBoolean(i, b);
    }

    @Override
    public ByteBuf setByte(int i, int i1) {
        return this.wrapped.setByte(i, i1);
    }

    @Override
    public ByteBuf setShort(int i, int i1) {
        return this.wrapped.setShort(i, i1);
    }

    @Override
    public ByteBuf setShortLE(int i, int i1) {
        return this.wrapped.setShortLE(i, i1);
    }

    @Override
    public ByteBuf setMedium(int i, int i1) {
        return this.wrapped.setMedium(i, i1);
    }

    @Override
    public ByteBuf setMediumLE(int i, int i1) {
        return this.wrapped.setMediumLE(i, i1);
    }

    @Override
    public ByteBuf setInt(int i, int i1) {
        return this.wrapped.setInt(i, i1);
    }

    @Override
    public ByteBuf setIntLE(int i, int i1) {
        return this.wrapped.setIntLE(i, i1);
    }

    @Override
    public ByteBuf setLong(int i, long l) {
        return this.wrapped.setLong(i, l);
    }

    @Override
    public ByteBuf setLongLE(int i, long l) {
        return this.wrapped.setLongLE(i, l);
    }

    @Override
    public ByteBuf setChar(int i, int i1) {
        return this.wrapped.setChar(i, i1);
    }

    @Override
    public ByteBuf setFloat(int i, float v) {
        return this.wrapped.setFloat(i, v);
    }

    @Override
    public ByteBuf setDouble(int i, double v) {
        return this.wrapped.setDouble(i, v);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf) {
        return this.wrapped.setBytes(i, byteBuf);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf, int i1) {
        return this.wrapped.setBytes(i, byteBuf, i1);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf, int i1, int i2) {
        return this.wrapped.setBytes(i, byteBuf, i1, i2);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] bytes) {
        return this.wrapped.setBytes(i, bytes);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] bytes, int i1, int i2) {
        return this.wrapped.setBytes(i, bytes, i1, i2);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
        return this.wrapped.setBytes(i, byteBuffer);
    }

    @Override
    public int setBytes(int i, InputStream inputStream, int i1) throws IOException {
        return this.wrapped.setBytes(i, inputStream, i1);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int i1) throws IOException {
        return this.wrapped.setBytes(i, scatteringByteChannel, i1);
    }

    @Override
    public int setBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
        return this.wrapped.setBytes(i, fileChannel, l, i1);
    }

    @Override
    public ByteBuf setZero(int i, int i1) {
        return this.wrapped.setZero(i, i1);
    }

    @Override
    public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
        return this.wrapped.setCharSequence(i, charSequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return this.wrapped.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.wrapped.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.wrapped.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.wrapped.readShort();
    }

    @Override
    public short readShortLE() {
        return this.wrapped.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.wrapped.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.wrapped.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.wrapped.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.wrapped.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.wrapped.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.wrapped.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.wrapped.readInt();
    }

    @Override
    public int readIntLE() {
        return this.wrapped.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.wrapped.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.wrapped.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.wrapped.readLong();
    }

    @Override
    public long readLongLE() {
        return this.wrapped.readLongLE();
    }

    @Override
    public char readChar() {
        return this.wrapped.readChar();
    }

    @Override
    public float readFloat() {
        return this.wrapped.readFloat();
    }

    @Override
    public double readDouble() {
        return this.wrapped.readDouble();
    }

    @Override
    public ByteBuf readBytes(int i) {
        return this.wrapped.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(int i) {
        return this.wrapped.readSlice(i);
    }

    @Override
    public ByteBuf readRetainedSlice(int i) {
        return this.wrapped.readRetainedSlice(i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf) {
        return this.wrapped.readBytes(byteBuf);
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf, int i) {
        return this.wrapped.readBytes(byteBuf, i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf, int i, int i1) {
        return this.wrapped.readBytes(byteBuf, i, i1);
    }

    @Override
    public ByteBuf readBytes(byte[] bytes) {
        return this.wrapped.readBytes(bytes);
    }

    @Override
    public ByteBuf readBytes(byte[] bytes, int i, int i1) {
        return this.wrapped.readBytes(bytes, i, i1);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer byteBuffer) {
        return this.wrapped.readBytes(byteBuffer);
    }

    @Override
    public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
        return this.wrapped.readBytes(outputStream, i);
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
        return this.wrapped.readBytes(gatheringByteChannel, i);
    }

    @Override
    public CharSequence readCharSequence(int i, Charset charset) {
        return this.wrapped.readCharSequence(i, charset);
    }

    public String readString() {
        int length = this.wrapped.readInt();
        if(length <= 0) return null;

        return (String) this.wrapped.readCharSequence(length, StandardCharsets.UTF_8);
    }

    @Override
    public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return this.wrapped.readBytes(fileChannel, l, i);
    }

    @Override
    public ByteBuf skipBytes(int i) {
        return this.wrapped.skipBytes(i);
    }

    @Override
    public ByteBuf writeBoolean(boolean b) {
        return this.wrapped.writeBoolean(b);
    }

    @Override
    public ByteBuf writeByte(int i) {
        return this.wrapped.writeByte(i);
    }

    @Override
    public ByteBuf writeShort(int i) {
        return this.wrapped.writeShort(i);
    }

    @Override
    public ByteBuf writeShortLE(int i) {
        return this.wrapped.writeShortLE(i);
    }

    @Override
    public ByteBuf writeMedium(int i) {
        return this.wrapped.writeMedium(i);
    }

    @Override
    public ByteBuf writeMediumLE(int i) {
        return this.wrapped.writeMediumLE(i);
    }

    @Override
    public ByteBuf writeInt(int i) {
        return this.wrapped.writeInt(i);
    }

    @Override
    public ByteBuf writeIntLE(int i) {
        return this.wrapped.writeIntLE(i);
    }

    @Override
    public ByteBuf writeLong(long l) {
        return this.wrapped.writeLong(l);
    }

    @Override
    public ByteBuf writeLongLE(long l) {
        return this.wrapped.writeLongLE(l);
    }

    @Override
    public ByteBuf writeChar(int i) {
        return this.wrapped.writeChar(i);
    }

    @Override
    public ByteBuf writeFloat(float v) {
        return this.wrapped.writeFloat(v);
    }

    @Override
    public ByteBuf writeDouble(double v) {
        return this.wrapped.writeDouble(v);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf) {
        return this.wrapped.writeBytes(byteBuf);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
        return this.wrapped.writeBytes(byteBuf, i);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf, int i, int i1) {
        return this.wrapped.writeBytes(byteBuf, i, i1);
    }

    @Override
    public ByteBuf writeBytes(byte[] bytes) {
        return this.wrapped.writeBytes(bytes);
    }

    @Override
    public ByteBuf writeBytes(byte[] bytes, int i, int i1) {
        return this.wrapped.writeBytes(bytes, i, i1);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer byteBuffer) {
        return this.wrapped.writeBytes(byteBuffer);
    }

    @Override
    public int writeBytes(InputStream inputStream, int i) throws IOException {
        return this.wrapped.writeBytes(inputStream, i);
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
        return this.wrapped.writeBytes(scatteringByteChannel, i);
    }

    @Override
    public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return this.wrapped.writeBytes(fileChannel, l, i);
    }

    @Override
    public ByteBuf writeZero(int i) {
        return this.wrapped.writeZero(i);
    }

    @Override
    public int writeCharSequence(CharSequence charSequence, Charset charset) {
        return this.wrapped.writeCharSequence(charSequence, charset);
    }

    public void writeString(String str) {
        if(str == null) {
            this.wrapped.writeInt(0);
            return;
        }

        byte[] b = str.getBytes(StandardCharsets.UTF_8);
        this.wrapped.writeInt(b.length);
        this.wrapped.writeBytes(b);
    }

    @Override
    public int indexOf(int i, int i1, byte b) {
        return this.wrapped.indexOf(i, i1, b);
    }

    @Override
    public int bytesBefore(byte b) {
        return this.wrapped.bytesBefore(b);
    }

    @Override
    public int bytesBefore(int i, byte b) {
        return this.wrapped.bytesBefore(i, b);
    }

    @Override
    public int bytesBefore(int i, int i1, byte b) {
        return this.wrapped.bytesBefore(i, i1, b);
    }

    @Override
    public int forEachByte(ByteProcessor byteProcessor) {
        return this.wrapped.forEachByte(byteProcessor);
    }

    @Override
    public int forEachByte(int i, int i1, ByteProcessor byteProcessor) {
        return this.wrapped.forEachByte(i, i1, byteProcessor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor byteProcessor) {
        return this.wrapped.forEachByteDesc(byteProcessor);
    }

    @Override
    public int forEachByteDesc(int i, int i1, ByteProcessor byteProcessor) {
        return this.wrapped.forEachByteDesc(i, i1, byteProcessor);
    }

    @Override
    public ByteBuf copy() {
        return this.wrapped.copy();
    }

    @Override
    public ByteBuf copy(int i, int i1) {
        return this.wrapped.copy(i, i1);
    }

    @Override
    public ByteBuf slice() {
        return this.wrapped.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.wrapped.retainedSlice();
    }

    @Override
    public ByteBuf slice(int i, int i1) {
        return this.wrapped.slice(i, i1);
    }

    @Override
    public ByteBuf retainedSlice(int i, int i1) {
        return this.wrapped.retainedSlice(i, i1);
    }

    @Override
    public ByteBuf duplicate() {
        return this.wrapped.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.wrapped.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return this.wrapped.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.wrapped.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int i, int i1) {
        return this.wrapped.nioBuffer(i, i1);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int i1) {
        return this.wrapped.internalNioBuffer(i, i1);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.wrapped.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int i1) {
        return this.wrapped.nioBuffers(i, i1);
    }

    @Override
    public boolean hasArray() {
        return this.wrapped.hasArray();
    }

    @Override
    public byte[] array() {
        return this.wrapped.array();
    }

    @Override
    public int arrayOffset() {
        return this.wrapped.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.wrapped.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.wrapped.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return this.wrapped.toString(charset);
    }

    @Override
    public String toString(int i, int i1, Charset charset) {
        return this.wrapped.toString(i, i1, charset);
    }

    @Override
    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.wrapped.equals(o);
    }

    @Override
    public int compareTo(ByteBuf byteBuf) {
        return this.wrapped.compareTo(byteBuf);
    }

    @Override
    public String toString() {
        return this.wrapped.toString();
    }

    @Override
    public ByteBuf retain(int i) {
        return this.wrapped.retain(i);
    }

    @Override
    public ByteBuf retain() {
        return this.wrapped.retain();
    }

    @Override
    public ByteBuf touch() {
        return this.wrapped.touch();
    }

    @Override
    public ByteBuf touch(Object o) {
        return this.wrapped.touch(o);
    }

    @Override
    public int refCnt() {
        return this.wrapped.refCnt();
    }

    @Override
    public boolean release() {
        return this.wrapped.release();
    }

    @Override
    public boolean release(int i) {
        return this.wrapped.release(i);
    }
}
