package config.beans;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *  @author yihuier
 *  @Date 2020/11/18 13:42
 *  @Description
 */
@Component
public class TestBean extends BaseBean {

	public void test() {
		System.out.println("test");
	}

	public TestBean() {
		System.out.println();
	}
}
