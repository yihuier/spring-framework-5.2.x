package dependencies;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 *  @author yihuier
 *  @Date 2020/11/26 10:19
 *  @Description
 */
@Component
//@DependsOn({"simpleBean2"})
public class SimpleBean1 {

//	private SimpleBean2 simpleBean2;

	public SimpleBean1() {
		System.out.println("simpleBean1 creating...");
	}

//	public SimpleBean1(SimpleBean2 simpleBean2) {
//		System.out.println("simpleBean1 creating...");
//		this.simpleBean2 = simpleBean2;
//	}
}
