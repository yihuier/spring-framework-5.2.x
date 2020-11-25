package importAnno;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;
import java.util.Objects;

/**
 *  @author yihuier
 *  @Date 2020/11/25 9:27
 *  @Description 这里registerBeanDefinitions中，我们可以自定义BeanDefinition，然后注册到容器中
 *  以便后面被实例化
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {


	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(MyMapperScan.class.getName());
		if (Objects.isNull(annotationAttributes)) {
			return;
		}
		Class[] classes = (Class[]) annotationAttributes.get("basePackageClasses");
		for (Class clazz : classes) {
			// 创建一个BeanDefinition
			// 这里不能创建一个，如UserDao这样的BeanDefinition，因为接口时没有办法实例化的
			// 所以这里借助一个FactoryBean来创建一个BeanDefinition，以便后面可以进行实例化
			// 我们知道FactoryBean是一个特殊的bean。可以用来创建一个bean，
			// 所以我们在FactoryBean内部使用JDK动态代理，创建了一个实现指定接口的对象
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DaoFactoryBean.class);
			AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
			// 由于DaoFactoryBean需要一个Class参数，所以这里传入参数
			beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(clazz);
			// 注册该bean，这里的beanName并没有去处理，比较随意
			registry.registerBeanDefinition(clazz.getSimpleName(), beanDefinition);
		}
	}
}
