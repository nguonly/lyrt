package dummy;

import org.junit.Assert;
import org.junit.Test;

public class PowerLogTest {

    @Test
    public void testLog(){
        int NUM = 6;
        int[] val = new int[NUM];

        int[] valLog = new int[NUM];

        for(int i=0; i<NUM; i++){
            val[i] = (int)Math.pow(10, i+1);

            valLog[i] = (int)Math.log(val[i]) / (int)Math.log(10);

            System.out.println(valLog[i]);
            Assert.assertEquals(i+1, valLog[i]);
        }


    }
}
