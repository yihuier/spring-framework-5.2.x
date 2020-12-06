package circle;


import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  @author yihuier
 *  @Date 2020/12/1 16:52
 *  @Description
 */
public class BaseTest {

	@Test
	public void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CircleBaseConfig.class);

	}
}
