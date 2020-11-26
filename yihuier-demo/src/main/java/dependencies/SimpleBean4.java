package dependencies;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

/**
 *  @author yihuier
 *  @Date 2020/11/26 11:01
 *  @Description
 */
@Component
public abstract class SimpleBean4 {

	private SimpleBean3 simpleBean3;

	public void useSimpleBean3() {
		System.out.println(getSimpleBean3());
	}

	@Lookup
	protected abstract SimpleBean3 getSimpleBean3();
}
