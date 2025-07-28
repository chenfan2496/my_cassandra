package org.miniCassandra.io.util;

import org.junit.Test;
import org.miniCassandra.db.io.util.BufferedDataOutputStreamPlus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BufferedDataOutputStreamPlusTest {

    @Test
    public void writeTest() throws IOException {
        File file = new File("/Volumes/ssd/workspace/my_cassandra/testDir/test.txt");
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        BufferedDataOutputStreamPlus bdosp = new BufferedDataOutputStreamPlus(raf);
        String testStr = "dshdjaskdhaskhdjsa";
        bdosp.write(testStr.getBytes());
        bdosp.flush();
    }

    @Test
    public void writeTest2() throws IOException {
        File file = new File("/Volumes/ssd/workspace/my_cassandra/testDir/test.txt");
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        BufferedDataOutputStreamPlus bdosp = new BufferedDataOutputStreamPlus(raf,8);
        String testStr = "dshdjaskdhaskhdjsa";
        bdosp.write(testStr.getBytes());
        bdosp.flush();
    }

}
