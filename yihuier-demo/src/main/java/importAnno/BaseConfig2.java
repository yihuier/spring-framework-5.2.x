package importAnno;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *  @author yihuier
 *  @Date 2020/11/25 10:27
 *  @Description
 */
@Configuration
@Import(MyImportSelector.class)
public class BaseConfig2 {
}
