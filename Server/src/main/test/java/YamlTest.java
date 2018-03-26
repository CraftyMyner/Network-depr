
import me.itsmas.network.server.task.NetworkTask;
import org.junit.Test;

public class ClassTest
{
    @Test
    public void rankTest()
    {
        Class<?> clazz = NetworkTask.class;

        System.out.println(clazz.getCanonicalName());
    }
}
