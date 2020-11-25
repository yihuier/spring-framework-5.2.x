package importAnno;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *  @author yihuier
 *  @Date 2020/11/25 11:04
 *  @Description
 */
@Component
public class SimpleBean3 {

	/**
	 * 注解@Bean并不一定要作用在@Configuration注解的类中
	 * @return
	 */
	@Bean
	public SimpleBean simpleBean() {
		return new SimpleBean();
	}
}
