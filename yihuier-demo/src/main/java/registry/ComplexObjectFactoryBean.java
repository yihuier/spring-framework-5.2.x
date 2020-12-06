package registry;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 *  @author yihuier
 *  @Date 2020/12/4 15:08
 *  @Description
 */
@Component
public class ComplexObjectFactoryBean implements FactoryBean<ComplexObject> {

	@Override
	public ComplexObject getObject() throws Exception {
		return new ComplexObject();
	}

	@Override
	public Class<?> getObjectType() {
		return ComplexObject.class;
	}
}
