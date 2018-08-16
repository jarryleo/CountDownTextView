package cn.leo.countdowntextview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String reg = "^\\w{1,40}@\\w{1,40}\\.[A-Za-z]{2,3}$";
        String email1 = "1234567_8@qq.com";
        String email2 = "12345678qq.com";
        String email3 = "12345678@qq..com";
        String email4 = "12345!678@qq.com";
        boolean b1 = email1.matches(reg);
        boolean b2 = email2.matches(reg);
        boolean b3 = email3.matches(reg);
        boolean b4 = email4.matches(reg);
        assertEquals(true, b1);
        assertEquals(false, b2);
        assertEquals(false, b3);
        assertEquals(false, b4);
    }
}