package com.wcn.jdk.example.io.file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 该类主要是测试文件写入。
 *
 * MAX=1千万，总体的测试时间为（毫秒）：
 * test1_outPutStream costTime:17256
 * test2_bufferedOutputStream costTime:128
 * test3_nio_fileChannel_HeapBuffer costTime:16850
 * test4_nio_fileChannel_HeapBufferCache costTime:121
 * test5_nio_fileChannel_directBuffer costTime:17337
 * test6_nio_mmap_mappedByteBuffer costTime:92
 */
public class FileWriteTest {
    public static String filePath =  "/Users/wucheng/Documents/work/test/";
    public static int MAX = 10000000;
    public static byte[] values = "0123456789\r\n".getBytes(StandardCharsets.UTF_8);
    public static void main(String[] args) throws Exception {
        test1_bio_outputStream();
        test2_bio_bufferedOutputStream();
        test3_nio_fileChannel_HeapBuffer();
        test4_nio_fileChannel_HeapBufferCache();
        test5_nio_fileChannel_directBuffer();
        test6_nio_mmap_mappedByteBuffer();
    }

    /**
     * 最原始的bio，通过outputStream直接写入，每次循环调用一次操作系统级写入操作(只是写入到pageCache，不包括pageCache到磁盘)。
     * 一次操作系统级写入包括：用户态切换到内核态 + 内存拷贝（JVM内存到pageCache）+ 内核态切回到用户态
     * java中的write触发的就是系统函数 write(int fd, const void *buf, size_t count);
     * 通过 strace -ff -o out -T -tt java FileTest 运行，可以监控到该方法执行过程中所有的系统调用，可以看出每循环一次调用一次系统write
     * 16:33:31.003722 write(4, "0123456789\r\n", 12) = 12 <0.000126>
     * 16:33:31.004297 write(4, "0123456789\r\n", 12) = 12 <0.000129>
     * 16:33:31.004581 write(4, "0123456789\r\n", 12) = 12 <0.000130>
     * 16:33:31.004861 write(4, "0123456789\r\n", 12) = 12 <0.000116>
     * 16:33:31.005040 write(4, "0123456789\r\n", 12) = 12 <0.000030>
     * 16:33:31.005132 write(4, "0123456789\r\n", 12) = 12 <0.000118>
     * 16:33:31.005313 write(4, "0123456789\r\n", 12) = 12 <0.000058>
     * 16:33:31.005437 write(4, "0123456789\r\n", 12) = 12 <0.000306>
     * 16:33:31.005806 write(4, "0123456789\r\n", 12) = 12 <0.000030>
     * 16:33:31.005898 write(4, "0123456789\r\n", 12) = 12 <0.000030>
     * 16:33:31.005990 write(4, "0123456789\r\n", 12) = 12 <0.000030>
     * @throws Exception
     */
    private static void test1_bio_outputStream() throws Exception{
        String fileName = filePath+"test1_outPutStream";
        fileCheck(fileName);
        long beginTime = System.currentTimeMillis();
        FileOutputStream out = new FileOutputStream(fileName);
        for (int i = 0; i < MAX; i++) {
            out.write(values);
            //OutputSteam的flush方法是空实现，不会产生系统调用进行刷盘的，从源码来看，方法中每没有任何实现代码。strace跟踪也看不到任何系统调用。
            //out.flush();
        }
        System.out.println(fileName + " costTime:"+(System.currentTimeMillis()-beginTime));
    }

    /**
     * 和test1类似，但是使用了BufferedOutputStream，不会导致每次循环都进行系统级写入，
     * 会在buffer满了后才进行一次系统级写入操作，性能大大提升。
     * @throws Exception
     */
    private static void test2_bio_bufferedOutputStream() throws Exception{
        String fileName = filePath+"test2_bufferedOutputStream";
        fileCheck(fileName);
        long beginTime = System.currentTimeMillis();
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName), 1024*1024);
        for (int i = 0; i < MAX; i++) {
            out.write(values);
        }
        //这里的flush会强制将Buffer中的数据进行write，但是也不会强制调用系统级写入磁盘
        out.flush();
        System.out.println(fileName + " costTime:"+(System.currentTimeMillis()-beginTime));
    }

    /**
     * 采用nio的FileChannel，通过ByteBuffer写入。
     * 通过strace指令的跟踪显示：每次循环中
     *      fileChannel.write()调用的是 write(int fd, const void *buf, size_t count);
     *      fileChannel.force()调用的是 fsync(int fd);
     *
     * 以下为strace -ff -o out -T -tt java FileTest 打印出来的日志：
     * 16:23:42.124201 write(4, "0123456789\r\n", 12) = 12 <0.000043>
     * 16:23:42.124315 fsync(4)                = 0 <0.010596>
     * 16:23:42.135132 write(4, "0123456789\r\n", 12) = 12 <0.000036>
     * 16:23:42.135243 fsync(4)                = 0 <0.011386>
     * 16:23:42.146948 write(4, "0123456789\r\n", 12) = 12 <0.000085>
     * 16:23:42.147246 fsync(4)                = 0 <0.011551>
     * 16:23:42.158994 write(4, "0123456789\r\n", 12) = 12 <0.000120>
     * 16:23:42.159291 fsync(4)                = 0 <0.011673>
     *
     * 从日志中可以看出来：
     * write和fsync两个系统函数的执行耗时相差很大，几乎是100倍，所以才有了pageCache这个缓冲，用来加速写。
     *
     * @throws Exception
     */
    private static void test3_nio_fileChannel_HeapBuffer() throws Exception{
        String fileName = filePath+"test3_nio_fileChannel_HeapBuffer";
        fileCheck(fileName);
        long beginTime = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(values.length);
        for (int i = 0; i < MAX; i++) {
            byteBuffer.put(values);
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            //fileChannel.force(true);//强制从pageCache持久化到硬盘
            byteBuffer.compact();
        }

        System.out.println(fileName + " costTime:"+(System.currentTimeMillis()-beginTime));
    }

    /**
     * 这个方法其实是对test3的一个优化，我们也可以模仿一个类似于BufferedOutputStream的缓存结果。
     * ByteBuffer申请的空间大一点，等buffer写满后再调用系统函数write，减少系统函数调用来加速。
     * @throws Exception
     */
    private static void test4_nio_fileChannel_HeapBufferCache() throws Exception{
        String fileName = filePath+"test4_nio_fileChannel_HeapBufferCache";
        fileCheck(fileName);
        long beginTime = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(values.length*2000);
        for (int i = 0; i < MAX; i++) {
            byteBuffer.put(values);
            if(i%2000==0){
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                byteBuffer.compact();
            }
        }

        byteBuffer.flip();
        fileChannel.write(byteBuffer);
        byteBuffer.compact();
        System.out.println(fileName + " costTime:"+(System.currentTimeMillis()-beginTime));
    }

    /**
     * 采用nio的FileChannel，通过DirectByteBuffer写入。
     * 通过源码 FileChannelImpl.write(ByteBuffer var1) -> IOUtil.write(FileDescriptor var0, ByteBuffer var1, long var2, NativeDispatcher var4)
     * 可以在IOUtils中看到以下逻辑：如果传入的byteBuffer是DirectByteBuffer，就直接写入，如果是HeapByteBuffer，则会先转成一个DirectByteBuffer再写入，
     * 按照逻辑来说，DirectByteBuffer会快一点的。
     * 但是实际运行与test3_nio_fileChannel_HeapBuffer来对比，写入1亿次，
     * heapByteBuffer耗时197秒，directByteBuffer耗时191秒，感觉没有什么太大的性能差别。
     * @throws Exception
     */
    private static void test5_nio_fileChannel_directBuffer() throws Exception{
        String fileName = filePath+"test5_nio_fileChannel_directBuffer";
        fileCheck(fileName);
        long beginTime = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(values.length);
        for (int i = 0; i < MAX; i++) {
            byteBuffer.put(values);
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.compact();
        }

        System.out.println(fileName + " costTime:"+(System.currentTimeMillis()-beginTime));
    }

    /**
     * 采用NIO中的MappedByteBuffer，通过FileChannel.map()方法，将该buffer是和pageCache做了映射了，操作系统的mmap技术，
     * 它的写入就是直接写入了pageCache，就不会再调用系统函数的write来写入了，也就不会有用户态和内核态的转换了。
     *
     * @throws Exception
     */
    private static void test6_nio_mmap_mappedByteBuffer() throws Exception{
        String fileName = filePath+"test6_nio_mmap_mappedByteBuffer";
        fileCheck(fileName);
        long beginTime = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        MappedByteBuffer buffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, values.length*MAX);
        System.out.println(buffer);
        for (int i = 0; i < MAX; i++) {
            buffer.put(values);
        }
        System.out.println(fileName + " costTime:"+(System.currentTimeMillis()-beginTime));
    }

    private static void fileCheck(String fileName) throws Exception{
        deleteFile(fileName);
        createFile(fileName);
    }

    private static void deleteFile(String fileName) throws Exception{
        File file = new File(fileName);
        if(file.exists()){
            System.out.println("文件已存在，进行删除");
            boolean deleteRet = file.delete();
            if(deleteRet){
                System.out.println("文件已删除");
            }else{
                throw new Exception("文件删除失败");
            }
        }
    }

    private static void createFile(String fileName) throws Exception{
        File file = new File(fileName);
        if(!file.exists()){
            System.out.println("文件不存在，进行创建");
            boolean createRet = file.createNewFile();
            if(createRet){
                System.out.println("文件已创建");
            }else{
                throw new Exception("文件创建失败");
            }
        }
    }
}
