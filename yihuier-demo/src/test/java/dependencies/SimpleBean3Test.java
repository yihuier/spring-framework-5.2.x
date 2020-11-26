package dependencies;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *  @author yihuier
 *  @Date 2020/11/26 11:08
 *  @Description
 */
class SimpleBean3Test {

	@Test
	public void test0() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(SimpleBean3.class);
		context.register(SimpleBean4.class);
		context.refresh();

		SimpleBean4 simpleBean4 = context.getBean(SimpleBean4.class);
		simpleBean4.useSimpleBean3();
		simpleBean4.useSimpleBean3();
	}

	@Test
	public void test1() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(SimpleBean3.class);
		context.register(SimpleBean5.class);
		context.refresh();

		SimpleBean5 simpleBean5 = context.getBean(SimpleBean5.class);
		simpleBean5.useSimpleBean3();
		simpleBean5.useSimpleBean3();
	}

}