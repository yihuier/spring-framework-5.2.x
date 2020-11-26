package config.beans;

import beans.TestBean;
import config.BaseConfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  @author yihuier
 *  @Date 2020/11/18 13:43
 *  @Description
 */
class TestBeanTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig.class);

		TestBean bean = context.getBean(TestBean.class);

		assertNotNull(bean);
	}
}