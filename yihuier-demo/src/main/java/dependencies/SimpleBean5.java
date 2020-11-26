package dependencies;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *  @author yihuier
 *  @Date 2020/11/26 11:04
 *  @Description
 */
@Component
public class SimpleBean5 {

	private ApplicationContext context;

	public SimpleBean5(ApplicationContext context) {
		this.context = context;
	}

	public void useSimpleBean3() {
		SimpleBean3 simpleBean3 = context.getBean(SimpleBean3.class);
		System.out.println(simpleBean3);
	}
}
