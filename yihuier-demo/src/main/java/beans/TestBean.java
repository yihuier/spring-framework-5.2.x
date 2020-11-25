package beans;

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
