package config.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 *  @author yihuier
 *  @Date 2020/11/21 15:22
 *  @Description
 */
public class CustomBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("CustomBeanDefinitionRegistryPostProcessor postProcessBeanDefinitionRegistry executing...");
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("CustomBeanDefinitionRegistryPostProcessor postProcessBeanFactory executing...");
	}
}
