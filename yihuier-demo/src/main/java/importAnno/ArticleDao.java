package importAnno;

/**
 *  @author yihuier
 *  @Date 2020/11/25 10:12
 *  @Description
 */
public interface ArticleDao {

	@Select("select * from article")
	void selectArticle();
}
