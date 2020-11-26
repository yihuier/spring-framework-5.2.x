package dependencies;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  @author yihuier
 *  @Date 2020/11/26 10:23
 *  @Description
 */
class SimpleBean1Test {

	@Test
	public void test0() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig1.class);

	}
}