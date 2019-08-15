package ut.com.atlassian.refapp;

import org.junit.Test;
import com.atlassian.refapp.api.MyPluginComponent;
import com.atlassian.refapp.impl.MyPluginComponentImpl;

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