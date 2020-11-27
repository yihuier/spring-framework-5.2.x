package importAnno;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Objects;

/**
 *  @author yihuier
 *  @Date 2020/11/25 9:35
 *  @Description
 */
public class Test {

	public static void main(String[] args) {
//		importBeanDefinitionRegistrarTest();

//		importSelectorTest();

		importOtherTest();

//		test();

//		componentScanTest();
	}

	public static void importBeanDefinitionRegistrarTest() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig.class);
		UserDao userDao = context.getBean(UserDao.class);
		ArticleDao articleDao = context.getBean(ArticleDao.class);

		userDao.selectUser();
		articleDao.selectArticle();
	}

	public static void importSelectorTest() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig2.class);
		SimpleBean simpleBean = context.getBean(SimpleBean.class);
		simpleBean.test();
	}

	public static void importOtherTest() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig4.class);
		SimpleBean2 simpleBean2 = context.getBean(SimpleBean2.class);

		simpleBean2.test();
	}

	public static void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig5.class);

	}

	public static void componentScanTest() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SimpleBean3.class);
	}
}
