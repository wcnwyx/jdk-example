##FileChannel 

### 一：类注释
```java
/**
 * A channel for reading, writing, mapping, and manipulating a file.
 * 用于读取、写入、映射和操作文件的通道。
 *
 * <p> A file channel is a {@link SeekableByteChannel} that is connected to
 * a file. It has a current <i>position</i> within its file which can
 * be both {@link #position() <i>queried</i>} and {@link #position(long)
 * <i>modified</i>}.  The file itself contains a variable-length sequence
 * of bytes that can be read and written and whose current {@link #size
 * <i>size</i>} can be queried.  The size of the file increases
 * when bytes are written beyond its current size; the size of the file
 * decreases when it is {@link #truncate <i>truncated</i>}.  The
 * file may also have some associated <i>metadata</i> such as access
 * permissions, content type, and last-modification time; this class does not
 * define methods for metadata access.
 * 文件通道是一个连接到文件的SeekableByteChannel，它在文件中有一个当前位置（position），
 * 可以通过position()方法来获取，也可以通过position(long)方法来修改。
 * 文件本身包含一个可变长度的字节序列，可以读取和写入，并且可以使用size()方法查询其当前的长度。
 * 当字节写入超出其当前大小时，文件的大小会增加；当使用truncate截断文件时，文件的大小会减小。
 * 该文件还可能有一些关联的元数据，例如访问权限、内容类型和最后修改时间；这个类没有定义元数据访问的方法。
 *
 * <p> In addition to the familiar read, write, and close operations of byte
 * channels, this class defines the following file-specific operations: </p>
 * 除了熟悉的字节通道的读、写和关闭操作之外，该类还定义了以下文件特定的操作
 *
 * <ul>
 *
 *   <li><p> Bytes may be {@link #read(ByteBuffer, long) read} or
 *   {@link #write(ByteBuffer, long) <i>written</i>} at an absolute
 *   position in a file in a way that does not affect the channel's current
 *   position.  </p></li>
 *   字节可能是在一个绝对位置来read或write，但是不会影响当前通道的位置。
 *
 *   <li><p> A region of a file may be {@link #map <i>mapped</i>}
 *   directly into memory; for large files this is often much more efficient
 *   than invoking the usual <tt>read</tt> or <tt>write</tt> methods.
 *   </p></li>
 *   文件的一个区域可以直接映射到直接内存；对于大文件来说，通常比常用的read或write方法更有效。
 *
 *   <li><p> Updates made to a file may be {@link #force <i>forced
 *   out</i>} to the underlying storage device, ensuring that data are not
 *   lost in the event of a system crash.  </p></li>
 *   对文件所做的更新可能会强制写出到底层存储设备，以确保在系统崩溃时不会丢失数据。
 *
 *   <li><p> Bytes can be transferred from a file {@link #transferTo <i>to
 *   some other channel</i>}, and {@link #transferFrom <i>vice
 *   versa</i>}, in a way that can be optimized by many operating systems
 *   into a very fast transfer directly to or from the filesystem cache.
 *   </p></li>
 *   transferTo 字节可以从文件转移到到其他通道，transferFrom反之亦然，
 *   以一种可以被许多操作系统优化的方式传输到直接进出文件系统缓存的非常快速的传输。
 *
 *   <li><p> A region of a file may be {@link FileLock <i>locked</i>}
 *   against access by other programs.  </p></li>
 *   文件的一个区域可能被锁住，以防止其他程序访问。
 *
 * </ul>
 *
 * <p> File channels are safe for use by multiple concurrent threads.  The
 * {@link Channel#close close} method may be invoked at any time, as specified
 * by the {@link Channel} interface.  Only one operation that involves the
 * channel's position or can change its file's size may be in progress at any
 * given time; attempts to initiate a second such operation while the first is
 * still in progress will block until the first operation completes.  Other
 * operations, in particular those that take an explicit position, may proceed
 * concurrently; whether they in fact do so is dependent upon the underlying
 * implementation and is therefore unspecified.
 * 多个并发线程可以安全地使用文件通道。 由Channel接口指定的close方法可以在任何时候被调用。
 * 在任何给定时间，只有一项涉及通道位置或可以更改其文件大小的操作可以正在进行；
 * 在第一个操作仍在进行时尝试启动第二个此类操作将阻塞，直到第一个操作完成。
 * 其他操作，特别是那些带有明确位置的操作，可以同时进行；他们是否真的这样做取决于底层实现，因此未指定。
 *
 * <p> The view of a file provided by an instance of this class is guaranteed
 * to be consistent with other views of the same file provided by other
 * instances in the same program.  The view provided by an instance of this
 * class may or may not, however, be consistent with the views seen by other
 * concurrently-running programs due to caching performed by the underlying
 * operating system and delays induced by network-filesystem protocols.  This
 * is true regardless of the language in which these other programs are
 * written, and whether they are running on the same machine or on some other
 * machine.  The exact nature of any such inconsistencies are system-dependent
 * and are therefore unspecified.
 * 该类的实例提供的文件视图保证与同一程序中其他实例提供的同一文件的其他视图一致。
 * 但是，由于底层操作系统执行的缓存和网络文件系统协议引起的延迟，
 * 此类实例提供的视图可能与其他并发运行的程序看到的视图一致，也可能不一致。
 * 无论这些其他程序是用什么语言编写的，也不管它们是在同一台机器上还是在其他机器上运行，这都是正确的。
 * 任何此类不一致的确切性质取决于系统，因此未指定。
 *
 * <p> A file channel is created by invoking one of the {@link #open open}
 * methods defined by this class. A file channel can also be obtained from an
 * existing {@link java.io.FileInputStream#getChannel FileInputStream}, {@link
 * java.io.FileOutputStream#getChannel FileOutputStream}, or {@link
 * java.io.RandomAccessFile#getChannel RandomAccessFile} object by invoking
 * that object's <tt>getChannel</tt> method, which returns a file channel that
 * is connected to the same underlying file. Where the file channel is obtained
 * from an existing stream or random access file then the state of the file
 * channel is intimately connected to that of the object whose <tt>getChannel</tt>
 * method returned the channel.  Changing the channel's position, whether
 * explicitly or by reading or writing bytes, will change the file position of
 * the originating object, and vice versa. Changing the file's length via the
 * file channel will change the length seen via the originating object, and vice
 * versa.  Changing the file's content by writing bytes will change the content
 * seen by the originating object, and vice versa.
 * 通过调用此类定义的open方法之一创建文件通道。
 * 也可以通过从一个存在的FileInputStream、FileOutputStream、或者 RandomAccessFile的getChannel方法获取，
 * 其返回一个连接到底层同一个文件的文件通道。
 * 如果文件通道是从现有的流或RandomAccessFile中获得的，那么文件通道的状态与getChannel方法返回通道的对象的状态密切相关。
 * 更改通道的position，无论是显式地还是通过读取或写入字节，都会更改原始对象的文件position，反之亦然。
 * 通过文件通道更改文件的长度将更改通过原始对象看到的长度，反之亦然。
 * 通过写入字节更改文件的内容将更改原始对象看到的内容，反之亦然。
 *
 * <a name="open-mode"></a> <p> At various points this class specifies that an
 * instance that is "open for reading," "open for writing," or "open for
 * reading and writing" is required.  A channel obtained via the {@link
 * java.io.FileInputStream#getChannel getChannel} method of a {@link
 * java.io.FileInputStream} instance will be open for reading.  A channel
 * obtained via the {@link java.io.FileOutputStream#getChannel getChannel}
 * method of a {@link java.io.FileOutputStream} instance will be open for
 * writing.  Finally, a channel obtained via the {@link
 * java.io.RandomAccessFile#getChannel getChannel} method of a {@link
 * java.io.RandomAccessFile} instance will be open for reading if the instance
 * was created with mode <tt>"r"</tt> and will be open for reading and writing
 * if the instance was created with mode <tt>"rw"</tt>.
 * 在不同的点上，这个类指定了一个“开放读取”、“开放写入”或“开放读取和写入”的实例是必需的。
 * 通过FileInputStream实例的getChannel方法获取到的通道，将开放读取。
 * 通过FileOutputStream实例的getChannel方法获取到的通道，将开放写入。
 * 通过RandomAccessFile实例的getChannel方法获取到的通道，如果该实例以'r'模式创建，将开放读取，
 * 如果以'rw'模式创建，将开发读取和写入。
 *
 * <a name="append-mode"></a><p> A file channel that is open for writing may be in
 * <i>append mode</i>, for example if it was obtained from a file-output stream
 * that was created by invoking the {@link
 * java.io.FileOutputStream#FileOutputStream(java.io.File,boolean)
 * FileOutputStream(File,boolean)} constructor and passing <tt>true</tt> for
 * the second parameter.  In this mode each invocation of a relative write
 * operation first advances the position to the end of the file and then writes
 * the requested data.  Whether the advancement of the position and the writing
 * of the data are done in a single atomic operation is system-dependent and
 * therefore unspecified.
 * 为写入而打开的文件通道可能处于 <i>附加模式<i>，例如，如果它是从FileOutputStream获取，
 * 并且FileOutputStream(File,boolean)第二个参数传递 <tt>true<tt>。
 * 在这种模式下，每次调用相对写入操作首先将位置推进到文件末尾，然后写入请求的数据。
 * 位置的提升和数据的写入是否在单个原子操作中完成是系统相关的，因此未指定。
 *
 * @see java.io.FileInputStream#getChannel()
 * @see java.io.FileOutputStream#getChannel()
 * @see java.io.RandomAccessFile#getChannel()
 *
 * @author Mark Reinhold
 * @author Mike McCloskey
 * @author JSR-51 Expert Group
 * @since 1.4
 */

public abstract class FileChannel
    extends AbstractInterruptibleChannel
    implements SeekableByteChannel, GatheringByteChannel, ScatteringByteChannel {

    /**
     * Opens or creates a file, returning a file channel to access the file.
     * 打开或者创建一个文件，返回一个文件通道来访问该文件。
     *
     * <p> The {@code options} parameter determines how the file is opened.
     * The {@link StandardOpenOption#READ READ} and {@link StandardOpenOption#WRITE
     * WRITE} options determine if the file should be opened for reading and/or
     * writing. If neither option (or the {@link StandardOpenOption#APPEND APPEND}
     * option) is contained in the array then the file is opened for reading.
     * By default reading or writing commences at the beginning of the file.
     * options参数决定了文件的打开方式。
     * StandardOpenOption.READ和StandardOpenOption.WRITE决定打开的文件是否可以读写。
     * 如果数组中不包含任何选项（或StandardOpenOption.APPEND选项），则打开文件以供读取。
     * 默认情况下，读取或写入从文件的开头开始。
     *
     * <p> In the addition to {@code READ} and {@code WRITE}, the following
     * options may be present:
     * 除了 READ 和 WRITE 之外，还可能存在以下选项：
     *
     * <table border=1 cellpadding=5 summary="">
     * <tr> <th>Option</th> <th>Description</th> </tr>
     * <tr>
     *   <td> {@link StandardOpenOption#APPEND APPEND} </td>
     *   <td> If this option is present then the file is opened for writing and
     *     each invocation of the channel's {@code write} method first advances
     *     the position to the end of the file and then writes the requested
     *     data. Whether the advancement of the position and the writing of the
     *     data are done in a single atomic operation is system-dependent and
     *     therefore unspecified. This option may not be used in conjunction
     *     with the {@code READ} or {@code TRUNCATE_EXISTING} options. 
     *     如果存在此选项，打开文件以进行写入，并且每次调用通道的write方法，
     *     则首先将position推进到文件的末尾，然后写入请求的数据。
     *     position的提升和数据的写入是否在单个原子操作中完成是系统相关的，因此未指定。
     *     此选项不能与READ或TRUNCATE_EXISTING选项一起使用。
     *     </td>
     *     
     * </tr>
     * <tr>
     *   <td> {@link StandardOpenOption#TRUNCATE_EXISTING TRUNCATE_EXISTING} </td>
     *   <td> If this option is present then the existing file is truncated to
     *   a size of 0 bytes. This option is ignored when the file is opened only
     *   for reading. 
     *   如果存在此选项，则现有文件将被截断为 0 字节大小。当文件只为读取而打开时，此选项将被忽略。
     *   </td>
     * </tr>
     * <tr>
     *   <td> {@link StandardOpenOption#CREATE_NEW CREATE_NEW} </td>
     *   <td> If this option is present then a new file is created, failing if
     *   the file already exists. When creating a file the check for the
     *   existence of the file and the creation of the file if it does not exist
     *   is atomic with respect to other file system operations. This option is
     *   ignored when the file is opened only for reading. 
     *   如果存在此选项，则创建一个新文件，如果该文件已存在则失败。
     *   创建文件时，检查文件是否存在以及如果文件不存在则创建文件对于其他文件系统操作而言是原子操作。
     *   当文件只为读取而打开时，此选项将被忽略
     *   </td>
     * </tr>
     * <tr>
     *   <td > {@link StandardOpenOption#CREATE CREATE} </td>
     *   <td> If this option is present then an existing file is opened if it
     *   exists, otherwise a new file is created. When creating a file the check
     *   for the existence of the file and the creation of the file if it does
     *   not exist is atomic with respect to other file system operations. This
     *   option is ignored if the {@code CREATE_NEW} option is also present or
     *   the file is opened only for reading. 
     *   如果存在此选项，则打开现有文件（如果存在），否则创建新文件。
     *   创建文件时，检查文件是否存在以及如果文件不存在则创建文件对于其他文件系统操作而言是原子操作。
     *   如果还存在 CREATE_NEW 选项或打开文件仅用于读取，则忽略此选项。
     *   </td>
     * </tr>
     * <tr>
     *   <td > {@link StandardOpenOption#DELETE_ON_CLOSE DELETE_ON_CLOSE} </td>
     *   <td> When this option is present then the implementation makes a
     *   <em>best effort</em> attempt to delete the file when closed by the
     *   the {@link #close close} method. If the {@code close} method is not
     *   invoked then a <em>best effort</em> attempt is made to delete the file
     *   when the Java virtual machine terminates. 
     *   当此选项存在时，实现会<em>尽最大努力<em> 在被close方法关闭时尝试删除文件。
     *   如果没有调用close方法，那么当 Java 虚拟机终止时，<em>尽力<em> 尝试删除文件。
     *   </td>
     * </tr>
     * <tr>
     *   <td>{@link StandardOpenOption#SPARSE SPARSE} </td>
     *   <td> When creating a new file this option is a <em>hint</em> that the
     *   new file will be sparse. This option is ignored when not creating
     *   a new file. 
     *   创建新文件时，此选项是一个提示，新文件将是稀疏的。不创建新文件时忽略此选项。
     *   </td>
     * </tr>
     * <tr>
     *   <td> {@link StandardOpenOption#SYNC SYNC} </td>
     *   <td> Requires that every update to the file's content or metadata be
     *   written synchronously to the underlying storage device. (see <a
     *   href="../file/package-summary.html#integrity"> Synchronized I/O file
     *   integrity</a>). 
     *   要求对文件内容或元数据的每次更新都同步写入底层存储设备。
     *   </td>
     * </tr>
     * <tr>
     *   <td> {@link StandardOpenOption#DSYNC DSYNC} </td>
     *   <td> Requires that every update to the file's content be written
     *   synchronously to the underlying storage device. (see <a
     *   href="../file/package-summary.html#integrity"> Synchronized I/O file
     *   integrity</a>). 
     *   要求对文件内容的每次更新都同步写入底层存储设备。
     *   </td>
     * </tr>
     * </table>
     *
     * <p> An implementation may also support additional options.
     * 实现也可能支持其他选项。
     *
     * <p> The {@code attrs} parameter is an optional array of file {@link
     * FileAttribute file-attributes} to set atomically when creating the file.
     * attrs参数是文件FileAttribute的可选数组，用于在创建文件时自动设置。
     *
     * <p> The new channel is created by invoking the {@link
     * FileSystemProvider#newFileChannel newFileChannel} method on the
     * provider that created the {@code Path}.
     * 通过在创建 Path的提供程序上调用 FileSystemProvider.newFileChannel() 方法来创建新通道。
     *
     * @param   path
     *          The path of the file to open or create
     * @param   options
     *          Options specifying how the file is opened
     * @param   attrs
     *          An optional list of file attributes to set atomically when
     *          creating the file
     *
     * @return  A new file channel
     *
     * @throws  IllegalArgumentException
     *          If the set contains an invalid combination of options
     * @throws  UnsupportedOperationException
     *          If the {@code path} is associated with a provider that does not
     *          support creating file channels, or an unsupported open option is
     *          specified, or the array contains an attribute that cannot be set
     *          atomically when creating the file
     * @throws  IOException
     *          If an I/O error occurs
     * @throws  SecurityException
     *          If a security manager is installed and it denies an
     *          unspecified permission required by the implementation.
     *          In the case of the default provider, the {@link
     *          SecurityManager#checkRead(String)} method is invoked to check
     *          read access if the file is opened for reading. The {@link
     *          SecurityManager#checkWrite(String)} method is invoked to check
     *          write access if the file is opened for writing
     *
     * @since   1.7
     */
    public static FileChannel open(Path path,
                                   Set<? extends OpenOption> options,
                                   FileAttribute<?>... attrs)
            throws IOException
    {
        FileSystemProvider provider = path.getFileSystem().provider();
        return provider.newFileChannel(path, options, attrs);
    }
}
```
总结：
1. 用于读取、写入、映射和操作文件的通道。
2. position属性表示当前处理的位置索引
3. 出了一个Channel通用的read、write、close方法只为，还定义了一些文件特有的方法：
   - map() 将文件映射到内存（mmap技术）
   - force() 强制刷盘
   - transferTo() transferFrom()
   - lock()
4. 线程安全的
5. 可以通过FileInputStream、FileOutputStream、RandomAccessFile、或者本类中的open方法创建。
   - FileInputStream创建的默认是读取权限
   - FileOutputStream创建的默认是写入权限
   - RandomAccessFile创建的，其权限和RandomAccessFile的读写权限一致
   - 自己的open方法创建，需要参数指定权限
6. 拥有以下权限选项：
   - READ：读取
   - WRITE：写入
   — APPEND：每次写入时，position会先移动到文件尾部，再从尾部写入
   - TRANCATE_EXISTING：将现有文件截断为0字节
   - CREATE_NEW：如已存在文件，则失败；如不存在，则创建
   - CREATE：如已存在文件，则打开；如不存在，则创建
   - DELETE_ON_CLOSE：close该channel后，进最大努力删除文件
   - SPARSE：创建新文件时，文件时稀疏的
   - SYNC：每次更新文件内容和原数据都要同步更新到底层存储设备
   - DSYNC：每次更新文件内容时，要同步写入到底层存储设备

### 二：Other operations
```java
public abstract class FileChannel
    extends AbstractInterruptibleChannel
    implements SeekableByteChannel, GatheringByteChannel, ScatteringByteChannel
{
    /**
     * Returns this channel's file position.
     * 返回此通道的文件位置。
     *
     * @return  This channel's file position,
     *          a non-negative integer counting the number of bytes
     *          from the beginning of the file to the current position
     *          该通道的文件位置，一个非负整数，计算从文件开头到当前位置的字节数
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract long position() throws IOException;

    /**
     * Sets this channel's file position.
     * 设置此通道的文件位置。
     *
     * <p> Setting the position to a value that is greater than the file's
     * current size is legal but does not change the size of the file.  A later
     * attempt to read bytes at such a position will immediately return an
     * end-of-file indication.  A later attempt to write bytes at such a
     * position will cause the file to be grown to accommodate the new bytes;
     * the values of any bytes between the previous end-of-file and the
     * newly-written bytes are unspecified.  </p>
     * 将位置设置为大于文件当前大小的值是合法的，但不会更改文件的大小。
     * 稍后尝试在该位置读取字节将立即返回文件结束指示。
     * 稍后尝试在这样的位置写入字节将导致文件增长以容纳新字节；
     * 未指定前一个文件结尾和新写入字节之间的任何字节的值。
     *
     * @param  newPosition
     *         The new position, a non-negative integer counting
     *         the number of bytes from the beginning of the file
     *
     * @return  This file channel
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  IllegalArgumentException
     *          If the new position is negative
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract FileChannel position(long newPosition) throws IOException;

    /**
     * Returns the current size of this channel's file.
     * 返回此通道文件的当前大小。
     *
     * @return  The current size of this channel's file,
     *          measured in bytes
     *          此通道文件的当前大小，以字节为单位
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract long size() throws IOException;

    /**
     * Truncates this channel's file to the given size.
     * 将此通道的文件截断为给定大小。
     *
     * <p> If the given size is less than the file's current size then the file
     * is truncated, discarding any bytes beyond the new end of the file.  If
     * the given size is greater than or equal to the file's current size then
     * the file is not modified.  In either case, if this channel's file
     * position is greater than the given size then it is set to that size.
     * </p>
     * 如果给定的大小小于文件的当前大小，则文件将被截断，丢弃文件新结尾之外的任何字节。
     * 如果给定大小大于或等于文件的当前大小，则不修改文件。
     * 在任何一种情况下，如果此通道的文件位置大于给定大小，则将其设置为该大小。
     *
     * @param  size
     *         The new size, a non-negative byte count
     *
     * @return  This file channel
     *
     * @throws  NonWritableChannelException
     *          If this channel was not opened for writing
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  IllegalArgumentException
     *          If the new size is negative
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract FileChannel truncate(long size) throws IOException;

    /**
     * Forces any updates to this channel's file to be written to the storage
     * device that contains it.
     * 强制将此通道文件的任何更新写入包含它的存储设备。
     *
     * <p> If this channel's file resides on a local storage device then when
     * this method returns it is guaranteed that all changes made to the file
     * since this channel was created, or since this method was last invoked,
     * will have been written to that device.  This is useful for ensuring that
     * critical information is not lost in the event of a system crash.
     * 如果此通道的文件驻留在本地存储设备上，则当此方法返回时，
     * 可以保证自创建此通道或自上次调用此方法以来对文件所做的所有更改都已写入该设备。
     * 这对于确保在系统崩溃时不会丢失关键信息很有用。
     *
     * <p> If the file does not reside on a local device then no such guarantee
     * is made.
     * 如果文件不驻留在本地设备上，则不会做出此类保证。
     *
     * <p> The <tt>metaData</tt> parameter can be used to limit the number of
     * I/O operations that this method is required to perform.  Passing
     * <tt>false</tt> for this parameter indicates that only updates to the
     * file's content need be written to storage; passing <tt>true</tt>
     * indicates that updates to both the file's content and metadata must be
     * written, which generally requires at least one more I/O operation.
     * Whether this parameter actually has any effect is dependent upon the
     * underlying operating system and is therefore unspecified.
     * metaData 参数可用于限制此方法需要执行的 IO 操作数。
     * 为该参数传递 false 表示只需要将文件内容的更新写入存储；
     * 传递 true 表示必须写入文件内容和元数据的更新，这通常需要至少多一次 IO 操作。
     * 此参数是否实际有任何影响取决于底层操作系统，因此未指定。
     *
     * <p> Invoking this method may cause an I/O operation to occur even if the
     * channel was only opened for reading.  Some operating systems, for
     * example, maintain a last-access time as part of a file's metadata, and
     * this time is updated whenever the file is read.  Whether or not this is
     * actually done is system-dependent and is therefore unspecified.
     * 调用此方法可能会导致发生 IO 操作，即使通道只是为读取而打开。
     * 例如，某些操作系统将上次访问时间作为文件元数据的一部分维护，并且每次读取文件时都会更新此时间。
     * 这是否实际执行取决于系统，因此未指定。
     *
     * <p> This method is only guaranteed to force changes that were made to
     * this channel's file via the methods defined in this class.  It may or
     * may not force changes that were made by modifying the content of a
     * {@link MappedByteBuffer <i>mapped byte buffer</i>} obtained by
     * invoking the {@link #map map} method.  Invoking the {@link
     * MappedByteBuffer#force force} method of the mapped byte buffer will
     * force changes made to the buffer's content to be written.  </p>
     * 此方法仅保证通过此类中定义的方法强制对此通道的文件进行更改。
     * 通过调用 map 方法获得的 MappedByteBuffer 进行更改，它可能会或可能不会强制更改。
     * 调用映射字节缓冲区的 MappedByteBuffer.force方法将强制对要写入的缓冲区内容进行更改。
     *
     * @param   metaData
     *          If <tt>true</tt> then this method is required to force changes
     *          to both the file's content and metadata to be written to
     *          storage; otherwise, it need only force content changes to be
     *          written
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract void force(boolean metaData) throws IOException;

    /**
     * Transfers bytes from this channel's file to the given writable byte
     * channel.
     * 将此通道的文件中的字节传输到给定的可写字节通道。
     *
     * <p> An attempt is made to read up to <tt>count</tt> bytes starting at
     * the given <tt>position</tt> in this channel's file and write them to the
     * target channel.  An invocation of this method may or may not transfer
     * all of the requested bytes; whether or not it does so depends upon the
     * natures and states of the channels.  Fewer than the requested number of
     * bytes are transferred if this channel's file contains fewer than
     * <tt>count</tt> bytes starting at the given <tt>position</tt>, or if the
     * target channel is non-blocking and it has fewer than <tt>count</tt>
     * bytes free in its output buffer.
     * 尝试从该通道文件中的给定 position 开始读取最多 count 个字节，并将它们写入目标通道。
     * 此方法的调用可能会也可能不会传输所有请求的字节；是否这样做取决于通道的性质和状态。
     * 如果此通道的文件包含少于从给定 position 开始的 count 个字节，
     * 或者如果目标通道是非阻塞的并且它具有少于count 个字节在其输出缓冲区中空闲。
     *
     * <p> This method does not modify this channel's position.  If the given
     * position is greater than the file's current size then no bytes are
     * transferred.  If the target channel has a position then bytes are
     * written starting at that position and then the position is incremented
     * by the number of bytes written.
     * 此方法不会修改此通道的位置。如果给定位置大于文件的当前大小，则不传输任何字节。
     * 如果目标通道有一个位置，则从该位置开始写入字节，然后该位置增加写入的字节数。
     *
     * <p> This method is potentially much more efficient than a simple loop
     * that reads from this channel and writes to the target channel.  Many
     * operating systems can transfer bytes directly from the filesystem cache
     * to the target channel without actually copying them.  </p>
     * 这种方法可能比从该通道读取并写入目标通道的简单循环更有效。
     * 许多操作系统可以直接将字节从文件系统缓存传输到目标通道，而无需实际复制它们。
     *
     * @param  position
     *         The position within the file at which the transfer is to begin;
     *         must be non-negative
     *
     * @param  count
     *         The maximum number of bytes to be transferred; must be
     *         non-negative
     *
     * @param  target
     *         The target channel
     *
     * @return  The number of bytes, possibly zero,
     *          that were actually transferred
     *
     * @throws IllegalArgumentException
     *         If the preconditions on the parameters do not hold
     *
     * @throws  NonReadableChannelException
     *          If this channel was not opened for reading
     *
     * @throws  NonWritableChannelException
     *          If the target channel was not opened for writing
     *
     * @throws  ClosedChannelException
     *          If either this channel or the target channel is closed
     *
     * @throws  AsynchronousCloseException
     *          If another thread closes either channel
     *          while the transfer is in progress
     *
     * @throws  ClosedByInterruptException
     *          If another thread interrupts the current thread while the
     *          transfer is in progress, thereby closing both channels and
     *          setting the current thread's interrupt status
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract long transferTo(long position, long count,
                                    WritableByteChannel target)
            throws IOException;

    /**
     * Transfers bytes into this channel's file from the given readable byte
     * channel.
     * 将字节从给定的可读字节通道传输到此通道的文件中。
     *
     * <p> An attempt is made to read up to <tt>count</tt> bytes from the
     * source channel and write them to this channel's file starting at the
     * given <tt>position</tt>.  An invocation of this method may or may not
     * transfer all of the requested bytes; whether or not it does so depends
     * upon the natures and states of the channels.  Fewer than the requested
     * number of bytes will be transferred if the source channel has fewer than
     * <tt>count</tt> bytes remaining, or if the source channel is non-blocking
     * and has fewer than <tt>count</tt> bytes immediately available in its
     * input buffer.
     * 尝试从源通道读取最多 count 个字节，并将它们从给定的 position 开始写入此通道的文件。
     * 此方法的调用可能会也可能不会传输所有请求的字节；是否这样做取决于通道的性质和状态。
     * 如果源通道剩余的字节数少于 count，或者源通道是非阻塞的并且立即可用的字节数少于 count，
     * 则将传输少于请求的字节数在其输入缓冲区中。
     *
     * <p> This method does not modify this channel's position.  If the given
     * position is greater than the file's current size then no bytes are
     * transferred.  If the source channel has a position then bytes are read
     * starting at that position and then the position is incremented by the
     * number of bytes read.
     * 此方法不会修改此通道的位置。如果给定位置大于文件的当前大小，则不传输任何字节。
     * 如果源通道有一个位置，则从该位置开始读取字节，然后该位置增加读取的字节数。
     *
     * <p> This method is potentially much more efficient than a simple loop
     * that reads from the source channel and writes to this channel.  Many
     * operating systems can transfer bytes directly from the source channel
     * into the filesystem cache without actually copying them.  </p>
     * 这种方法可能比从源通道读取并写入该通道的简单循环更有效。
     * 许多操作系统可以直接从源通道将字节传输到文件系统缓存中，而无需实际复制它们。
     *
     * @param  src
     *         The source channel
     *
     * @param  position
     *         The position within the file at which the transfer is to begin;
     *         must be non-negative
     *
     * @param  count
     *         The maximum number of bytes to be transferred; must be
     *         non-negative
     *
     * @return  The number of bytes, possibly zero,
     *          that were actually transferred
     *
     * @throws IllegalArgumentException
     *         If the preconditions on the parameters do not hold
     *
     * @throws  NonReadableChannelException
     *          If the source channel was not opened for reading
     *
     * @throws  NonWritableChannelException
     *          If this channel was not opened for writing
     *
     * @throws  ClosedChannelException
     *          If either this channel or the source channel is closed
     *
     * @throws  AsynchronousCloseException
     *          If another thread closes either channel
     *          while the transfer is in progress
     *
     * @throws  ClosedByInterruptException
     *          If another thread interrupts the current thread while the
     *          transfer is in progress, thereby closing both channels and
     *          setting the current thread's interrupt status
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract long transferFrom(ReadableByteChannel src,
                                      long position, long count)
            throws IOException;

    /**
     * Reads a sequence of bytes from this channel into the given buffer,
     * starting at the given file position.
     * 从给定的文件位置开始，从此通道将字节序列读取到给定的缓冲区中。
     *
     * <p> This method works in the same manner as the {@link
     * #read(ByteBuffer)} method, except that bytes are read starting at the
     * given file position rather than at the channel's current position.  This
     * method does not modify this channel's position.  If the given position
     * is greater than the file's current size then no bytes are read.  </p>
     * 此方法的工作方式与 read(ByteBuffer) 方法相同，不同之处在于从给定文件位置而不是从通道的当前位置开始读取字节。
     * 此方法不会修改此通道的位置。如果给定位置大于文件的当前大小，则不读取任何字节。
     *
     * @param  dst
     *         The buffer into which bytes are to be transferred
     *
     * @param  position
     *         The file position at which the transfer is to begin;
     *         must be non-negative
     *
     * @return  The number of bytes read, possibly zero, or <tt>-1</tt> if the
     *          given position is greater than or equal to the file's current
     *          size
     *
     * @throws  IllegalArgumentException
     *          If the position is negative
     *
     * @throws  NonReadableChannelException
     *          If this channel was not opened for reading
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  AsynchronousCloseException
     *          If another thread closes this channel
     *          while the read operation is in progress
     *
     * @throws  ClosedByInterruptException
     *          If another thread interrupts the current thread
     *          while the read operation is in progress, thereby
     *          closing the channel and setting the current thread's
     *          interrupt status
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract int read(ByteBuffer dst, long position) throws IOException;

    /**
     * Writes a sequence of bytes to this channel from the given buffer,
     * starting at the given file position.
     * 从给定的缓冲区将字节序列写入此通道，从给定的文件位置开始。
     *
     * <p> This method works in the same manner as the {@link
     * #write(ByteBuffer)} method, except that bytes are written starting at
     * the given file position rather than at the channel's current position.
     * This method does not modify this channel's position.  If the given
     * position is greater than the file's current size then the file will be
     * grown to accommodate the new bytes; the values of any bytes between the
     * previous end-of-file and the newly-written bytes are unspecified.  </p>
     * 此方法的工作方式与 write(ByteBuffer) 方法相同，只是字节从给定文件位置开始写入，而不是从通道的当前位置开始。
     * 此方法不会修改此通道的位置。如果给定位置大于文件的当前大小，则文件将增长以容纳新字节；
     * 未指定前一个文件结尾和新写入字节之间的任何字节的值。
     *
     * @param  src
     *         The buffer from which bytes are to be transferred
     *
     * @param  position
     *         The file position at which the transfer is to begin;
     *         must be non-negative
     *
     * @return  The number of bytes written, possibly zero
     *
     * @throws  IllegalArgumentException
     *          If the position is negative
     *
     * @throws  NonWritableChannelException
     *          If this channel was not opened for writing
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  AsynchronousCloseException
     *          If another thread closes this channel
     *          while the write operation is in progress
     *
     * @throws  ClosedByInterruptException
     *          If another thread interrupts the current thread
     *          while the write operation is in progress, thereby
     *          closing the channel and setting the current thread's
     *          interrupt status
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract int write(ByteBuffer src, long position) throws IOException;
}
```

### Memory-mapped buffers
```java
public abstract class FileChannel
    extends AbstractInterruptibleChannel
    implements SeekableByteChannel, GatheringByteChannel, ScatteringByteChannel
{
    /**
     * Maps a region of this channel's file directly into memory.
     * 将此通道文件的一个区域直接映射到内存中。
     *
     * <p> A region of a file may be mapped into memory in one of three modes:
     * 文件的一个区域可以通过以下三种模式之一映射到内存中：
     * </p>
     *
     * <ul>
     *
     *   <li><p> <i>Read-only:</i> Any attempt to modify the resulting buffer
     *   will cause a {@link java.nio.ReadOnlyBufferException} to be thrown.
     *   ({@link MapMode#READ_ONLY MapMode.READ_ONLY}) </p></li>
     *   任何尝试修改返回的缓冲区都将导致抛出 {@link java.nio.ReadOnlyBufferException}。
     *
     *   <li><p> <i>Read/write:</i> Changes made to the resulting buffer will
     *   eventually be propagated to the file; they may or may not be made
     *   visible to other programs that have mapped the same file.  ({@link
     *   MapMode#READ_WRITE MapMode.READ_WRITE}) </p></li>
     *   对返回的缓冲区所做的更改最终将传播到文件；它们可能对映射相同文件的其他程序可见，也可能不可见。
     *
     *   <li><p> <i>Private:</i> Changes made to the resulting buffer will not
     *   be propagated to the file and will not be visible to other programs
     *   that have mapped the same file; instead, they will cause private
     *   copies of the modified portions of the buffer to be created.  ({@link
     *   MapMode#PRIVATE MapMode.PRIVATE}) </p></li>
     *   对返回的缓冲区所做的更改不会传播到文件，并且对映射同一文件的其他程序不可见；
     *   相反，它们将导致创建缓冲区修改部分的私有副本。
     *   
     *
     * </ul>
     *
     * <p> For a read-only mapping, this channel must have been opened for
     * reading; for a read/write or private mapping, this channel must have
     * been opened for both reading and writing.
     * 对于只读映射，该通道必须已打开读取；对于读写或私有映射，此通道必须同时打开读写。
     *
     * <p> The {@link MappedByteBuffer <i>mapped byte buffer</i>}
     * returned by this method will have a position of zero and a limit and
     * capacity of <tt>size</tt>; its mark will be undefined.  The buffer and
     * the mapping that it represents will remain valid until the buffer itself
     * is garbage-collected.
     * 此方法返回的 MappedByteBuffer 的position为零，limit和capacity为 size;
     * 它的mark将是未定义的。缓冲区和它所代表的映射将保持有效，直到缓冲区本身被垃圾收集。
     *
     * <p> A mapping, once established, is not dependent upon the file channel
     * that was used to create it.  Closing the channel, in particular, has no
     * effect upon the validity of the mapping.
     * 映射一旦建立，就不再依赖于用于创建它的文件通道。特别是关闭通道对映射的有效性没有影响。
     *
     * <p> Many of the details of memory-mapped files are inherently dependent
     * upon the underlying operating system and are therefore unspecified.  The
     * behavior of this method when the requested region is not completely
     * contained within this channel's file is unspecified.  Whether changes
     * made to the content or size of the underlying file, by this program or
     * another, are propagated to the buffer is unspecified.  The rate at which
     * changes to the buffer are propagated to the file is unspecified.
     * 内存映射文件的许多细节本质上依赖于底层操作系统，因此未指定。
     * 当请求的区域未完全包含在此通道的文件中时，此方法的行为未指定。
     * 未指定此程序或其他程序对基础文件的内容或大小所做的更改是否传播到缓冲区。
     * 未指定缓冲区更改传播到文件的速率。
     *
     * <p> For most operating systems, mapping a file into memory is more
     * expensive than reading or writing a few tens of kilobytes of data via
     * the usual {@link #read read} and {@link #write write} methods.  From the
     * standpoint of performance it is generally only worth mapping relatively
     * large files into memory.  </p>
     * 对于大多数操作系统，将文件映射到内存比通过常用的 read 和 write 方法读取或写入几十 KB 的数据更昂贵。
     * 从性能的角度来看，通常只值得将相对较大的文件映射到内存中。
     *
     * @param  mode
     *         One of the constants {@link MapMode#READ_ONLY READ_ONLY}, {@link
     *         MapMode#READ_WRITE READ_WRITE}, or {@link MapMode#PRIVATE
     *         PRIVATE} defined in the {@link MapMode} class, according to
     *         whether the file is to be mapped read-only, read/write, or
     *         privately (copy-on-write), respectively
     *
     * @param  position
     *         The position within the file at which the mapped region
     *         is to start; must be non-negative
     *
     * @param  size
     *         The size of the region to be mapped; must be non-negative and
     *         no greater than {@link java.lang.Integer#MAX_VALUE}
     *
     * @return  The mapped byte buffer
     *
     * @throws NonReadableChannelException
     *         If the <tt>mode</tt> is {@link MapMode#READ_ONLY READ_ONLY} but
     *         this channel was not opened for reading
     *
     * @throws NonWritableChannelException
     *         If the <tt>mode</tt> is {@link MapMode#READ_WRITE READ_WRITE} or
     *         {@link MapMode#PRIVATE PRIVATE} but this channel was not opened
     *         for both reading and writing
     *
     * @throws IllegalArgumentException
     *         If the preconditions on the parameters do not hold
     *
     * @throws IOException
     *         If some other I/O error occurs
     *
     * @see java.nio.channels.FileChannel.MapMode
     * @see java.nio.MappedByteBuffer
     */
    public abstract MappedByteBuffer map(MapMode mode,
                                         long position, long size)
            throws IOException;
}
```

### Lock
```java
public abstract class FileChannel
        extends AbstractInterruptibleChannel
        implements SeekableByteChannel, GatheringByteChannel, ScatteringByteChannel
{
    /**
     * Acquires a lock on the given region of this channel's file.
     * 获取对该通道文件的给定区域的锁定。
     *
     * <p> An invocation of this method will block until the region can be
     * locked, this channel is closed, or the invoking thread is interrupted,
     * whichever comes first.
     * 此方法的调用将阻塞，直到可以锁定区域、关闭此通道或中断调用线程，以先到者为准。
     *
     * <p> If this channel is closed by another thread during an invocation of
     * this method then an {@link AsynchronousCloseException} will be thrown.
     * 如果在调用此方法期间此通道被另一个线程关闭，则将抛出 AsynchronousCloseException。
     *
     * <p> If the invoking thread is interrupted while waiting to acquire the
     * lock then its interrupt status will be set and a {@link
     * FileLockInterruptionException} will be thrown.  If the invoker's
     * interrupt status is set when this method is invoked then that exception
     * will be thrown immediately; the thread's interrupt status will not be
     * changed.
     * 如果调用线程在等待获取锁时被中断，则将设置其中断状态并抛出 FileLockInterruptionException。
     * 如果在调用此方法时设置了调用者的中断状态，则将立即抛出该异常；线程的中断状态不会改变。
     *
     * <p> The region specified by the <tt>position</tt> and <tt>size</tt>
     * parameters need not be contained within, or even overlap, the actual
     * underlying file.  Lock regions are fixed in size; if a locked region
     * initially contains the end of the file and the file grows beyond the
     * region then the new portion of the file will not be covered by the lock.
     * If a file is expected to grow in size and a lock on the entire file is
     * required then a region starting at zero, and no smaller than the
     * expected maximum size of the file, should be locked.  The zero-argument
     * {@link #lock()} method simply locks a region of size {@link
     * Long#MAX_VALUE}.
     * position 和 size 参数指定的区域不需要包含在实际的底层文件中，甚至不需要重叠。
     * 锁定区域的大小是固定的；如果锁定区域最初包含文件的结尾，并且文件超出该区域，则文件的新部分将不会被锁定覆盖。
     * 如果预期文件的大小会增长并且需要锁定整个文件，则应锁定从零开始且不小于文件预期最大大小的区域。
     * 零参数的 lock() 方法只是锁定大小为 Long.MAX_VALUE 的区域。
     *
     * <p> Some operating systems do not support shared locks, in which case a
     * request for a shared lock is automatically converted into a request for
     * an exclusive lock.  Whether the newly-acquired lock is shared or
     * exclusive may be tested by invoking the resulting lock object's {@link
     * FileLock#isShared() isShared} method.
     * 某些操作系统不支持共享锁，在这种情况下，共享锁请求会自动转换为排他锁请求。
     * 新获得的锁是共享的还是独占的，可以通过调用生成的锁对象的 FileLock。isShared()方法来测试。
     *
     * <p> File locks are held on behalf of the entire Java virtual machine.
     * They are not suitable for controlling access to a file by multiple
     * threads within the same virtual machine.  </p>
     * 文件锁代表整个 Java 虚拟机持有。它们不适用于控制同一虚拟机内的多个线程对文件的访问。
     *
     * @param  position
     *         The position at which the locked region is to start; must be
     *         non-negative
     *
     * @param  size
     *         The size of the locked region; must be non-negative, and the sum
     *         <tt>position</tt>&nbsp;+&nbsp;<tt>size</tt> must be non-negative
     *
     * @param  shared
     *         <tt>true</tt> to request a shared lock, in which case this
     *         channel must be open for reading (and possibly writing);
     *         <tt>false</tt> to request an exclusive lock, in which case this
     *         channel must be open for writing (and possibly reading)
     *
     * @return  A lock object representing the newly-acquired lock
     *
     * @throws  IllegalArgumentException
     *          If the preconditions on the parameters do not hold
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  AsynchronousCloseException
     *          If another thread closes this channel while the invoking
     *          thread is blocked in this method
     *
     * @throws  FileLockInterruptionException
     *          If the invoking thread is interrupted while blocked in this
     *          method
     *
     * @throws  OverlappingFileLockException
     *          If a lock that overlaps the requested region is already held by
     *          this Java virtual machine, or if another thread is already
     *          blocked in this method and is attempting to lock an overlapping
     *          region
     *
     * @throws  NonReadableChannelException
     *          If <tt>shared</tt> is <tt>true</tt> this channel was not
     *          opened for reading
     *
     * @throws  NonWritableChannelException
     *          If <tt>shared</tt> is <tt>false</tt> but this channel was not
     *          opened for writing
     *
     * @throws  IOException
     *          If some other I/O error occurs
     *
     * @see     #lock()
     * @see     #tryLock()
     * @see     #tryLock(long,long,boolean)
     */
    public abstract FileLock lock(long position, long size, boolean shared)
            throws IOException;
}
```