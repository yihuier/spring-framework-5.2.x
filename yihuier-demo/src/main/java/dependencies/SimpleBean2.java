package dependencies;

import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 *  @author yihuier
 *  @Date 2020/11/26 10:19
 *  @Description
 */
@Component
@DependsOn({"simpleBean1"})
public class SimpleBean2 {

	private SimpleBean1 simpleBean1;

//	public SimpleBean2() {
//		System.out.println("simpleBean2 creating...");
//	}

	public SimpleBean2(SimpleBean1 simpleBean1) {
		System.out.println("simpleBean2 creating...");
		this.simpleBean1 = simpleBean1;
	}
}
