package unittest;

import net.lyrt.Registry;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayDeque;

/**
 * Created by nguonly on 6/14/17.
 */
public abstract class BaseTest {

    public Registry _reg = Registry.getRegistry();

    @Before
    public void beforeEachTest(){
        //_reg.setRelations(new ArrayDeque<>());
    }

    @After
    public void afterEachTest(){
        _reg.getRollbackStacks().clear();
    }
}
