package config.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 *  @author yihuier
 *  @Date 2020/11/21 15:20
 *  @Description
 */
@Component
public class MyBeanDefinitionRegistryPostProcessor2 implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("MyBeanDefinitionRegistryPosyProcessor(PriorityOrdered) postProcessBeanDefinitionRegistry executing...");
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("MyBeanDefinitionRegistryPosyProcessor(PriorityOrdered) postProcessBeanFactory executing...");
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
