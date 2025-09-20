package org.miniCassandra.utils.BTree;

import org.junit.Test;
import org.miniCassandra.utils.btree.BTree;
import org.miniCassandra.utils.btree.UpdateFunction;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class BTreeTest {
    static Integer[] ints = new Integer[50];
    static  {
        System.setProperty("cassandra.btree.fanfactor", "4");
    }
    static final UpdateFunction<Integer,Integer> updateF = UpdateFunction.noOp();

    private static final UpdateFunction<Integer, Integer> noOp = new UpdateFunction<Integer, Integer>()
    {
        public Integer apply(Integer replacing, Integer update)
        {
            return update;
        }

        public boolean abortEarly()
        {
            return false;
        }

        public void allocated(long heapSize)
        {
        }

        public Integer apply(Integer k)
        {
            return k;
        }
    };

    private static List<Integer> seq(int count)
    {
        List<Integer> r = new ArrayList<>();
        for (int i = 0 ; i < count ; i++)
            r.add(i);
        return r;
    }

    private static List<Integer> rand(int count)
    {
        Random rand = ThreadLocalRandom.current();
        List<Integer> r = seq(count);
        for (int i = 0 ; i < count - 1 ; i++)
        {
            int swap = i + rand.nextInt(count - i);
            Integer tmp = r.get(i);
            r.set(i, r.get(swap));
            r.set(swap, tmp);
        }
        return r;
    }

    private static final Comparator<Integer> CMP = new Comparator<Integer>()
    {
        public int compare(Integer o1, Integer o2)
        {
            return Integer.compare(o1, o2);
        }
    };

    @Test
    public void testBuilding_UpdateFunctionReplacement()
    {
        for (int i = 0; i < 20 ; i++)
            checkResult(i, BTree.build(seq(i), updateF));
    }
    private static void checkResult(int count, Object[] btree)
    {
        Iterator<Integer> iter = BTree.slice(btree, CMP, BTree.Dir.ASC);
        int i = 0;
        while (iter.hasNext())
            assertEquals(iter.next(), ints[i++]);
        assertEquals(count, i);
    }

    @Test
    public void testBuildBTree() {
       List<Integer> buildList = seq(36);
       Object[] btree =  BTree.build(buildList,updateF);
       assertEquals(Optional.of(10).get(),BTree.find(btree,Comparator.naturalOrder(),10));
    }
}
