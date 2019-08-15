package ut.com.atlassian.bitbucket;

import org.junit.Test;
import com.atlassian.bitbucket.api.MyPluginComponent;
import com.atlassian.bitbucket.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}