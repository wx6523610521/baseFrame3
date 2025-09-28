package work.chncyl.base.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.chncyl.base.global.security.entity.RoleInfo;
import work.chncyl.base.global.service.dto.Dictionary;
import work.chncyl.base.global.service.dto.DictionarySearchInfo;
import work.chncyl.base.global.service.mapper.GlobalMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalService {
    private final GlobalMapper globalMapper;

    public List<Dictionary> searchDictionarys(DictionarySearchInfo info) {
        return globalMapper.searchDictionarys(info);
    }


    public List<RoleInfo> getDefaultRole() {

        return globalMapper.getDefaultRole();
    }
}
