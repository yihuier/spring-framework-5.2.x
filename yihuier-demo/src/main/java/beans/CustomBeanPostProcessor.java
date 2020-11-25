package beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 *  @author yihuier
 *  @Date 2020/11/21 8:20
 *  @Description
 */
public class CustomBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("CustomBeanPostProcessor executing for " + beanName);
		return bean;
	}
}
