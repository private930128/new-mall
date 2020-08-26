package ltd.newbee.mall.dao.base;

import org.springframework.data.domain.Auditable;

import java.io.Serializable;

/**
 * Created by zhanghenan on 2020/6/13.
 */
public interface MybatisAuditableBaseMapper<Domain, PK extends Serializable, Example> extends MybatisBaseMapper<Domain, PK, Example> {
}
