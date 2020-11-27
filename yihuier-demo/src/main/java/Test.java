import beans.CustomBeanDefinitionRegistryPostProcessor;
import beans.CustomBeanFactoryPostProcessor;
import beans.CustomBeanPostProcessor;
import beans.TestBean;
import beans.TestBean2;
import config.BaseConfig;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.core.type.AnnotationMetadata;

/**
 *  @author yihuier
 *  @Date 2020/11/18 13:57
 *  @Description
 */
public class Test {

	public static void main(String[] args) {
		test();

//		AnnotationMetadataTest();

//		postProcessorTest();
	}

	public static void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig.class);
//		TestBean bean = context.getBean(TestBean.class);
//		bean.test();
	}

	public static void AnnotationMetadataTest() {
		AnnotationMetadata metadata = AnnotationMetadata.introspect(TestBean.class);
		System.out.println(metadata.getAnnotationAttributes(Qualifier.class.getName(), false));
	}

	public void AnnotatedGenericBeanDefinitionTest() {
		AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(TestBean.class);
		ScopeMetadata scopeMetadata = new AnnotationScopeMetadataResolver().resolveScopeMetadata(abd);
		System.out.println(scopeMetadata.getScopeName());
	}

	public static void postProcessorTest() {
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig.class);

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(BaseConfig.class);
		context.addBeanFactoryPostProcessor(new CustomBeanDefinitionRegistryPostProcessor());
		context.addBeanFactoryPostProcessor(new CustomBeanFactoryPostProcessor());
		context.register(CustomBeanPostProcessor.class);
		context.refresh();

		System.out.println("==================");

		// 当refresh之后，再注册Bean该bean是不会被BeanFactoryPostProcessor处理到的
		// 因为此时的BeanFactoryPostProcessor已经都执行完回调了
		context.registerBean(TestBean2.class);
		System.out.println(context.getBean(TestBean2.class));
	}
}
