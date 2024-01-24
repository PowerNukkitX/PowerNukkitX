package cn.nukkit.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

public class ByteBufTest {
    @Test
    //readRetainedSlice creates a slice of the original buf, with a reference count of +1,
    //and the reference count of the slice is the same as that of the original buf,but the index is independent
    void testReadRetainedSlice(){
        ByteBuf originalBuf = Unpooled.wrappedBuffer(new byte[]{1, 2, 3, 4, 5});
        ByteBuf slice = originalBuf.readRetainedSlice(3);

        System.out.println("Original Buf refCnt: " + originalBuf.refCnt());
        System.out.println("Original Buf readerIndex: " + originalBuf.readerIndex());
        System.out.println("Slice refCnt: " + slice.refCnt());
        System.out.println("Slice readerIndex: " + slice.readerIndex());

        System.out.println("read value :"+slice.readByte());
        System.out.println("Original Buf readerIndex after slice read: " + originalBuf.readerIndex());
        System.out.println("Slice readerIndex after slice read: " + slice.readerIndex());

        slice.release();

        System.out.println("Original Buf refCnt after slice release: " + originalBuf.refCnt());
        System.out.println("Slice Buf refCnt after slice release: " + slice.refCnt());
    }
}
