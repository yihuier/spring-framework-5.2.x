package importAnno;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *  @author yihuier
 *  @Date 2020/11/25 10:01
 *  @Description
 */
public class DaoFactoryBean implements FactoryBean, InvocationHandler {

	private Class clazz;

	public DaoFactoryBean(Class clazz) {
		this.clazz = clazz;
	}

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clazz }, this);
	}

	@Override
	public Class<?> getObjectType() {
		return clazz;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Select annotation = method.getAnnotation(Select.class);
		System.out.println(annotation.value());
		return null;
	}
}
