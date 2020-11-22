package config;

import config.beans.CustomBeanDefinitionRegistryPostProcessor;
import config.beans.CustomBeanFactoryPostProcessor;
import config.beans.CustomBeanPostProcessor;
import config.beans.MyBeanFactoryPostProcessor;
import config.beans.TestBean;
import config.beans.TestBean2;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.AnnotationMetadata;

/**
 *  @author yihuier
 *  @Date 2020/11/18 13:57
 *  @Description
 */
public class Test {

	public static void main(String[] args) {
//		test();

//		AnnotationMetadataTest();

		postProcessorTest();
	}

	public static void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BaseConfig.class);
		TestBean bean = context.getBean(TestBean.class);
		bean.test();
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

		context.registerBean(TestBean2.class);
		System.out.println(context.getBean(TestBean2.class));
	}
}
