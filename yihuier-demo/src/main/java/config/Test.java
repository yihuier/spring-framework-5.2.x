package config;

import config.beans.TestBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *  @author yihuier
 *  @Date 2020/11/18 13:57
 *  @Description
 */
public class Test {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig.class);

		TestBean bean = context.getBean(TestBean.class);

		bean.test();
	}
}
