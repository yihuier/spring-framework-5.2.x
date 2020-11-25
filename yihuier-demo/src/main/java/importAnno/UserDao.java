package importAnno;

/**
 *  @author yihuier
 *  @Date 2020/11/25 9:29
 *  @Description
 */
public interface UserDao {

	@Select("select * from user")
	void selectUser();
}
