package importAnno;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  @author yihuier
 *  @Date 2020/11/25 8:17
 *  @Description 目前只实现扫描指定类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MyImportBeanDefinitionRegistrar.class)
public @interface MyMapperScan {

	Class<?>[] basePackageClasses() default {};
}
