package com.holomatic.someip;

import org.junit.Test;

import static org.junit.Assert.*;

import com.holomatic.someip.codec.PayloadCodec;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    PayloadCodec codec = PayloadCodec.getInstance();

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

}