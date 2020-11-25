package importAnno;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *  @author yihuier
 *  @Date 2020/11/25 10:55
 *  @Description
 */
@Configuration
@Import(SimpleBean.class)
public class BaseConfig5 {
}
