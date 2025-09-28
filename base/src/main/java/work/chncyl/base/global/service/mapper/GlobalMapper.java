package work.chncyl.base.global.service.mapper;

import org.apache.ibatis.annotations.Param;
import work.chncyl.base.global.security.entity.RoleInfo;
import work.chncyl.base.global.service.dto.Dictionary;
import work.chncyl.base.global.service.dto.DictionarySearchInfo;

import java.util.List;

public interface GlobalMapper {
    List<Dictionary> searchDictionarys(@Param("info") DictionarySearchInfo info);

    List<RoleInfo> getDefaultRole();

}
