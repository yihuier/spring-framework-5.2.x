package importAnno;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *  @author yihuier
 *  @Date 2020/11/25 9:36
 *  @Description
 */
@Configuration
@ComponentScan
@MyMapperScan(basePackageClasses = {UserDao.class, ArticleDao.class})
public class BaseConfig {
}
