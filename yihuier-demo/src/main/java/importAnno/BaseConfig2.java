package importAnno;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *  @author yihuier
 *  @Date 2020/11/25 10:27
 *  @Description
 */
@Configuration
@Import(MyImportSelector.class)
@ComponentScan
public class BaseConfig2 {
}
