package work.chncyl.service.restRoom.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.chncyl.base.global.security.entity.JwtClaimDto;
import work.chncyl.service.restRoom.dto.CreateRestRoomDto;
import work.chncyl.service.restRoom.dto.QueryRestRoomDto;
import work.chncyl.service.restRoom.dto.UpdateRestRoomDto;
import work.chncyl.service.restRoom.mapper.RestRoomMapper;
import work.chncyl.service.restRoom.entity.RestRoom;


@Service
@RequiredArgsConstructor
public class RestRoomService extends ServiceImpl<RestRoomMapper, RestRoom> {
    private final RestRoomMapper restRoomMapper;

    public RestRoom getRestRoom(Integer id) {
        return restRoomMapper.selectById(id);
    }

    public Integer saveRestRoom(CreateRestRoomDto restRoomInfo, JwtClaimDto dto) {

        if (restRoomInfo.getRestrictions() == null) {
            restRoomInfo.setRestrictions(false);
        }
        if (restRoomInfo.getRestrictions()) {
            if (restRoomInfo.getDistance() == null || restRoomInfo.getDistance() <= 0) {
                throw new IllegalArgumentException("签到限制距离不能为空且必须大于0");
            }
            if (restRoomInfo.getLongitude() == null || restRoomInfo.getLatitude() == null)
                throw new IllegalArgumentException("签到定位信息不能为空");
        }

        RestRoom room = new RestRoom();

        BeanUtil.copyProperties(restRoomInfo, room);
        room.setCreateUserId(dto.getUserId());
        room.setCreateTime(new Date());
        room.setDeleted(false);

        restRoomMapper.insert(room);
        return room.getId();
    }

    public Boolean deleteRestRoom(Integer id, JwtClaimDto dto) {
        return update(new UpdateWrapper<RestRoom>().set("Deleted", true).set("DeletedTime", new Date()).set("DeletedUserId", dto.getUserId()).eq("Id", id));
    }

    public Boolean updateRestRoom(UpdateRestRoomDto info, JwtClaimDto dto) {

        RestRoom restRoom = restRoomMapper.selectById(info.getId());
        if (restRoom == null) {
            throw new IllegalArgumentException("未找到该数据");
        }
        BeanUtil.copyProperties(info, restRoom);
        restRoom.setUpdateTime(new Date());
        restRoom.setUpdateUserId(dto.getUserId());

        return updateById(restRoom);
    }

    public Page<RestRoom> queryRestRoom(QueryRestRoomDto queryInfo) {
        IPage<RestRoom> page = new Page<>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        return restRoomMapper.queryRestRoom(page, queryInfo);
    }
}
