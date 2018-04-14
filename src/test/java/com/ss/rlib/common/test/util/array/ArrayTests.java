package com.ss.rlib.common.test.util.array;

import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.array.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

/**
 * The list of tests {@link Array}.
 *
 * @author JavaSaBr
 */
public class ArrayTests {

    @Test
    public void testFastArray() {

        final Array<Integer> array = ArrayFactory.asArray(2, 5, 1, 7, 6, 8, 4);

        // sorting
        array.sort(Integer::compareTo);

        Assertions.assertArrayEquals(array.toArray(Integer.class),
                ArrayFactory.toArray(1, 2, 4, 5, 6, 7, 8));

        // performace operations
        final UnsafeArray<Integer> unsafe = array.asUnsafe();
        unsafe.prepareForSize(10);
        unsafe.unsafeAdd(3);
        unsafe.unsafeAdd(9);
        unsafe.unsafeAdd(10);

        Assertions.assertEquals(10, array.size());

        // additional API
        final Integer first = array.first();
        final Integer last = array.last();

        array.slowRemove(1);
        array.fastRemove(1);

        Integer searched = array.search(integer -> integer == 2);
        searched = array.search(2, (el, arg) -> el == arg);

        array.forEach(5, (el, arg) -> System.out.println(el + arg));
        array.forEach(5, 7, (el, firstArg, secondArg) -> System.out.println(el + firstArg + secondArg));
    }

    @Test
    public void testAtomicARSWLockArray() {

        final ConcurrentArray<Integer> array = ArrayFactory.newConcurrentAtomicARSWLockArray(Integer.class);
        final long writeStamp = array.writeLock();
        try {

            array.addAll(ArrayFactory.toArray(9, 8, 7, 6, 5, 4, 3));
            array.sort(Integer::compareTo);

        } finally {
            array.writeUnlock(writeStamp);
        }

        final long readStamp = array.readLock();
        try {

            Assertions.assertArrayEquals(array.toArray(Integer.class),
                    ArrayFactory.toArray(3, 4, 5, 6, 7, 8, 9));

            final Integer first = array.first();
            final Integer last = array.last();

            Assertions.assertEquals(3, (int) first);
            Assertions.assertEquals(9, (int) last);

        } finally {
            array.readUnlock(readStamp);
        }

        final Integer last = ArrayUtils.getInReadLock(array, Array::last);
        final Integer result = ArrayUtils.getInReadLock(array, last,
                (arr, target) -> arr.search(target, Integer::equals));

        ArrayUtils.runInWriteLock(array, result + 1, Collection::add);

        Assertions.assertEquals(10, (int) array.last());
    }

    @Test
    public void testArrayCollector() {

        final Array<Integer> result = IntStream.range(0, 1000)
                .mapToObj(value -> value)
                .collect(ArrayCollectors.toArray(Integer.class));

        Assertions.assertEquals(1000, result.size());

        final ConcurrentArray<Integer> concurrentArray = IntStream.range(0, 1000)
                .parallel()
                .mapToObj(value -> value)
                .collect(ArrayCollectors.toConcurrentArray(Integer.class));

        Assertions.assertEquals(1000, concurrentArray.size());

        final Array<Number> numbers = IntStream.range(0, 1000)
                .mapToObj(value -> value)
                .collect(ArrayCollectors.toArray(Integer.class));

        Assertions.assertEquals(1000, numbers.size());

        final Array<Collection<?>> collections = IntStream.range(0, 1000)
                .mapToObj(value -> new ArrayList<>(1))
                .collect(ArrayCollectors.toArray(Collection.class));

        Assertions.assertEquals(1000, collections.size());
    }
}