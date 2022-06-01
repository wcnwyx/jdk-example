package com.wcn.jdk.example.io.file;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Set;

/**
 * 模拟启动一个socket，然后accept到连接后，直接将文件通过transfer to写入到SocketChannel。
 * 测试linux系统的sendfile系统调用。
 *
 * 测试步骤：
 * 1. strace -ff -o out java TransferToTest 启动服务
 *
 * 2. telnet localhost 8080 连接上启动的服务端口，立即开始打印服务端写入的报文（文件中的前10个字节）
 *
 * 3.
 *      3.1 ps -ef| grep TransferToTest 找到 java进程编号
 *      3.2 lsof -p pid 查看该java进程的所有文件描述符信息，如下所示：
 *      4为/root/temp/data.txt文件
 *      10为刚建立的socket链接。
 *
 * COMMAND   PID USER   FD   TYPE             DEVICE SIZE/OFF     NODE NAME
 * java    14889 root  cwd    DIR              253,0     4096  2757311 /root/temp
 * java    14889 root  rtd    DIR              253,0     4096        2 /
 * java    14889 root  txt    REG              253,0     7734  1315113 /home/app/jdk1.8.0_181/bin/java
 * java    14889 root  mem    REG              253,0   161776  1704133 /lib64/ld-2.12.so
 * java    14889 root  mem    REG              253,0  1930416  1704134 /lib64/libc-2.12.so
 * java    14889 root  mem    REG              253,0   146592  1704135 /lib64/libpthread-2.12.so
 * java    14889 root  mem    REG              253,0    23088  1704137 /lib64/libdl-2.12.so
 * java    14889 root  mem    REG              253,0    47760  1704136 /lib64/librt-2.12.so
 * java    14889 root  mem    REG              253,0   600048  1704143 /lib64/libm-2.12.so
 * java    14889 root  mem    REG              253,0 99174448   787038 /usr/lib/locale/locale-archive
 * java    14889 root  mem    REG              253,0   115485  1316829 /home/app/jdk1.8.0_181/jre/lib/amd64/libnet.so
 * java    14889 root  mem    REG              253,0    93308  1316832 /home/app/jdk1.8.0_181/jre/lib/amd64/libnio.so
 * java    14889 root  mem    REG              253,0 66339559  1316613 /home/app/jdk1.8.0_181/jre/lib/rt.jar
 * java    14889 root  mem    REG              253,0   128794  1316825 /home/app/jdk1.8.0_181/jre/lib/amd64/libzip.so
 * java    14889 root  mem    REG              253,0    66432  1700637 /lib64/libnss_files-2.12.so
 * java    14889 root  mem    REG              253,0   226512  1316819 /home/app/jdk1.8.0_181/jre/lib/amd64/libjava.so
 * java    14889 root  mem    REG              253,0    66472  1316824 /home/app/jdk1.8.0_181/jre/lib/amd64/libverify.so
 * java    14889 root  mem    REG              253,0 17068604  1316801 /home/app/jdk1.8.0_181/jre/lib/amd64/server/libjvm.so
 * java    14889 root  mem    REG              253,0   104289  1316475 /home/app/jdk1.8.0_181/lib/amd64/jli/libjli.so
 * java    14889 root  mem    REG              253,0    32768  2354702 /tmp/hsperfdata_root/14889
 * java    14889 root    0u   CHR              136,0      0t0        3 /dev/pts/0
 * java    14889 root    1u   CHR              136,0      0t0        3 /dev/pts/0
 * java    14889 root    2u   CHR              136,0      0t0        3 /dev/pts/0
 * java    14889 root    3r   REG              253,0 66339559  1316613 /home/app/jdk1.8.0_181/jre/lib/rt.jar
 * java    14889 root    4u   REG              253,0       18  2757314 /root/temp/data.txt
 * java    14889 root    5u  unix 0xffff88006aad7180      0t0 63614476 socket
 * java    14889 root    6u  IPv6           63614478      0t0      TCP *:webcache (LISTEN)
 * java    14889 root    7r  FIFO                0,8      0t0 63614479 pipe
 * java    14889 root    8w  FIFO                0,8      0t0 63614479 pipe
 * java    14889 root    9u   REG                0,9        0     3791 [eventpoll]
 * java    14889 root   10u  IPv6           63614570      0t0      TCP localhost:webcache->localhost:41940 (ESTABLISHED)
 *
 *
 * 4. 查看strace 的系统调用跟踪文件（out.xxxx 大小一直在增长的那个文件），从selector.select之后为：
 *
 * epoll_wait(9, {{EPOLLIN, {u32=6, u64=3011036236459540486}}}, 4096, 4294967295) = 1
 *
 * xxx
 *
 * accept(6, {sa_family=AF_INET6, sin6_port=htons(41940), inet_pton(AF_INET6, "::1", &sin6_addr), sin6_flowinfo=0, sin6_scope_id=0}, [28]) = 10
 *
 * xxx
 *
 * -- 后面一直是这些系统调用的循环
 * sendfile(10, 4, [0], 10)                = 10
 * futex(0x7f1d24009e54, FUTEX_WAIT_BITSET_PRIVATE, 1, {27050441, 307283993}, ffffffff) = -1 ETIMEDOUT (Connection timed out)
 * futex(0x7f1d24009e28, FUTEX_WAKE_PRIVATE, 1) = 0
 * fstat(4, {st_mode=S_IFREG|0644, st_size=18, ...}) = 0
 * sendfile(10, 4, [0], 10)                = 10
 * futex(0x7f1d24009e54, FUTEX_WAIT_BITSET_PRIVATE, 1, {27050441, 407814734}, ffffffff) = -1 ETIMEDOUT (Connection timed out)
 * futex(0x7f1d24009e28, FUTEX_WAKE_PRIVATE, 1) = 0
 * fstat(4, {st_mode=S_IFREG|0644, st_size=18, ...}) = 0
 * sendfile(10, 4, [0], 10)                = 10
 * futex(0x7f1d24009e54, FUTEX_WAIT_BITSET_PRIVATE, 1, {27050441, 508690207}, ffffffff) = -1 ETIMEDOUT (Connection timed out)
 * futex(0x7f1d24009e28, FUTEX_WAKE_PRIVATE, 1) = 0
 * fstat(4, {st_mode=S_IFREG|0644, st_size=18, ...}) = 0
 *
 * epoll_wait 为selector.select的系统调用。
 * accept 为serverSocketChannel.accept()的系统调用。
 * sendfile 为fileChannel.transferTo()的系统调用
 * futex、fstat 为Thread.sleep的系统调用
 *
 * 以下为man sendfile的介绍：
 * SENDFILE(2)                Linux Programmer’s Manual               SENDFILE(2)
 *
 * NAME
 *        sendfile - transfer data between file descriptors
 *
 * SYNOPSIS
 *        #include <sys/sendfile.h>
 *
 *        ssize_t sendfile(int out_fd, int in_fd, off_t *offset, size_t count);
 *
 * DESCRIPTION
 *        sendfile() copies data between one file descriptor and another.  Because this copying is done within the kernel, sendfile() is more efficient than the combination of read(2)
 *        and write(2), which would require transferring data to and from user space.
 *
 *        in_fd should be a file descriptor opened for reading and out_fd should be a descriptor opened for writing.
 *
 *        If offset is not NULL, then it points to a variable holding the file offset from which sendfile() will start reading data from in_fd.  When sendfile() returns, this variable
 *        will be set to the offset of the byte following the last byte that was read.  If offset is not NULL, then sendfile() does not modify the current file offset of in_fd; other-
 *        wise the current file offset is adjusted to reflect the number of bytes read from in_fd.
 *
 *        count is the number of bytes to copy between the file descriptors.
 *
 *        Presently (Linux 2.6.9): in_fd, must correspond to a file which supports mmap(2)-like operations (i.e., it cannot be a socket); and out_fd must refer to a socket.
 *
 *        Applications may wish to fall back to read(2)/write(2) in the case where sendfile() fails with EINVAL or ENOSYS.
 * */
public class TransferToTest {

    public static void main(String[] args) throws Exception {
        String filePath = "/root/temp/data.txt";
        FileChannel fileChannel = FileChannel.open(new File(filePath).toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("bind success.");
        while(true){
            int selectNum = selector.select();
            if(selectNum>0){
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel1.accept();
                    System.out.println("new socketChannel:"+socketChannel);

                    while(true){
                        fileChannel.transferTo(0, 10, socketChannel);
                        Thread.sleep(100);
                    }
                }
            }
        }
    }
}
