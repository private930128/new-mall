package ltd.newbee.mall.dao.base;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhanghenan on 2020/6/13.
 */
public interface MybatisBaseMapper<Domain, PK extends Serializable, Example> {
    int countByExample(Example var1);

    int deleteByExample(Example var1);

    int deleteByPrimaryKey(PK var1);

    int insert(Domain var1);

    int insertSelective(Domain var1);

    List<Domain> selectByExample(Example var1);

    Domain selectByPrimaryKey(PK var1);

    int updateByExampleSelective(@Param("record") Domain var1, @Param("example") Example var2);

    int updateByExample(@Param("record") Domain var1, @Param("example") Example var2);

    int updateByPrimaryKeySelective(Domain var1);

    int updateByPrimaryKey(Domain var1);
}