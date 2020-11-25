package importAnno;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 *  @author yihuier
 *  @Date 2020/11/25 10:27
 *  @Description 这里返回的字符串数组中的className对应的类将会被注册成BeanDefinition
 *  然后实例化出bean
 */
public class MyImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[] { SimpleBean.class.getName() };
	}
}
