package importAnno;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *  @author yihuier
 *  @Date 2020/11/25 10:37
 *  @Description
 */
@Configuration
@Import(SimpleBean4.class)
public class BaseConfig4 {
}
