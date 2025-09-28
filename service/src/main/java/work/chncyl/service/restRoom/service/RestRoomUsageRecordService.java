package work.chncyl.service.restRoom.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.chncyl.base.global.security.entity.JwtClaimDto;
import work.chncyl.base.global.service.GlobalService;
import work.chncyl.base.global.service.dto.Dictionary;
import work.chncyl.base.global.service.dto.DictionarySearchInfo;
import work.chncyl.service.restRoom.dto.*;
import work.chncyl.service.restRoom.entity.RestRoom;
import work.chncyl.service.restRoom.entity.RestRoomUser;
import work.chncyl.service.restRoom.mapper.RestRoomMapper;
import work.chncyl.service.restRoom.mapper.RestRoomUsageRecordMapper;
import work.chncyl.service.restRoom.entity.RestRoomUsageRecord;
import work.chncyl.service.restRoom.mapper.RestRoomUserMapper;

@Service
@RequiredArgsConstructor
public class RestRoomUsageRecordService extends ServiceImpl<RestRoomUsageRecordMapper, RestRoomUsageRecord> {

    // 性别常量定义
    private static final String MALE_SEX_CODE = "Sex-100";
    private static final String FEMALE_SEX_CODE = "Sex-101";

    // 签到有效期字典类型
    private static final String VALID_TIME_DICT_TYPE = "SignInValidTime";

    List<Date> defaultValidTime = Arrays.asList(
            Date.from(Instant.parse("2021-10-01T11:40:00.00Z")),
            Date.from(Instant.parse("2021-10-02T14:00:00.00Z"))
    );


    private final RestRoomMapper restRoomMapper;

    private final RestRoomUserMapper restRoomUserMapper;

    private final GlobalService globalService;

    public Page<UsageRecord> queryRestRoomUsageRecord(QueryRestRoomUsageRecordDto queryUsageInfo) {
        Page<UsageRecord> page = new Page<>(queryUsageInfo.getCurrentPage(), queryUsageInfo.getPageSize());
        return baseMapper.queryRestRoomUsageRecord(page, queryUsageInfo);
    }

    public Page<UsageRecord> queryCurrentUserUsageRecord(QueryCurrentUserUsageRecordDto queryUsageInfo, JwtClaimDto dto) {
        RestRoomUser user = restRoomUserMapper.selectOne(
                new LambdaQueryWrapper<>(RestRoomUser.class)
                        .eq(RestRoomUser::getUserId, dto.getUserId())
                        .eq(RestRoomUser::getDeleted, false)
        );
        if (user == null) {
            return new Page<>();
        }
        QueryRestRoomUsageRecordDto d = BeanUtil.copyProperties(queryUsageInfo, QueryRestRoomUsageRecordDto.class);
        d.setRestRoomUserId(user.getId());
        return queryRestRoomUsageRecord(d);
    }

    public Integer saveRestRoomUsageRecord(CreateRestRoomUsageRecordDto recordDto, JwtClaimDto claimDto) {
        if (recordDto == null
                || recordDto.getRestRoomId() == null
                || (recordDto.getRestRoomUserId() == null && StrUtil.isAllBlank(recordDto.getPhone()))) {
            throw new IllegalArgumentException("参数错误");
        }

        // 查询要关联的信息
        RestRoom restRoom = restRoomMapper.selectById(recordDto.getRestRoomId());
        RestRoomUser roomUser = restRoomUserMapper.selectOne(
                new LambdaQueryWrapper<>(RestRoomUser.class)
                        .eq(StrUtil.isNotBlank(recordDto.getOpenId()), RestRoomUser::getWxOpenId, recordDto.getOpenId())
                        .eq(StrUtil.isNotBlank(recordDto.getPhone()), RestRoomUser::getPhoneNumber, recordDto.getPhone())
                        .eq(recordDto.getRestRoomUserId() != null, RestRoomUser::getId, recordDto.getRestRoomUserId())
                        .eq(RestRoomUser::getDeleted, false)
                        .eq(RestRoomUser::getState, 1)
                        .last("LIMIT 1")
        );
        if (restRoom == null || roomUser == null) {
            RestRoomUsageRecord usageRecord = new RestRoomUsageRecord();
            BeanUtil.copyProperties(recordDto, usageRecord);
            usageRecord.setUseTime(new Date());
            usageRecord.setCreateTime(new Date());
            usageRecord.setDeleted(false);
            usageRecord.setState((byte) 4);
            baseMapper.insert(usageRecord);

            throw new IllegalArgumentException("未找到匹配数据,请联系管理员");
        }

        recordDto.setRestRoomUserId(roomUser.getId());

        // 判断签到位置
        if (restRoom.getRestrictions() && (recordDto.getLongitude() == null || recordDto.getLatitude() == null)) {
            throw new IllegalArgumentException("签到定位信息缺失");
        }
        // tocheck 判断签到位置是否在房间范围内，因使用坐标系不同，交给前端自定判断
        /*if (restRoom.getRestrictions()
                && (Math.abs(restRoom.getLongitude().subtract(recordDto.getLongitude()).doubleValue()) > restRoom.getDistance() || Math.abs(restRoom.getLatitude().subtract(recordDto.getLatitude()).doubleValue()) > restRoom.getDistance())) {
            RestRoomUsageRecord usageRecord = new RestRoomUsageRecord();
            usageRecord.setRestRoomId(recordDto.getRestRoomId());
            usageRecord.setRestRoomUserId(roomUser.getId());
            usageRecord.setUseTime(new Date());
            usageRecord.setCreateTime(new Date());
            usageRecord.setDeleted(false);
            usageRecord.setState((byte) 2);
            int insert = baseMapper.insert(usageRecord);

            throw new IllegalArgumentException("签到位置不在房间范围内");
        }*/

        //判断休息房使用状态
        // 从字典表获取有效时间
        DictionarySearchInfo searchInfo = new DictionarySearchInfo();
        searchInfo.setType(VALID_TIME_DICT_TYPE);
        List<Dictionary> dictionaries = globalService.searchDictionarys(searchInfo);
        List<Date> collect;
        if (dictionaries == null || dictionaries.isEmpty()) {
            collect = defaultValidTime;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            collect = dictionaries.stream().map(dictionary -> {
                try {
                    return dateFormat.parse(dictionary.getCode());
                } catch (ParseException e) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if (collect.isEmpty()) {
            collect = defaultValidTime;
        }
        // 超过两个有效时间，取最小和最大为开始和结束时间
        if (collect.size() > 2) {
            collect.sort(Comparator.comparing(Date::getTime));
        }
        Date startTime = collect.get(0);
        Date endTime = collect.get(collect.size() - 1);
        if (!startTime.before(new Date()) || !endTime.after(new Date())) {
            RestRoomUsageRecord usageRecord = new RestRoomUsageRecord();
            BeanUtil.copyProperties(recordDto, usageRecord);
            usageRecord.setUseTime(new Date());
            usageRecord.setCreateTime(new Date());
            usageRecord.setDeleted(false);
            usageRecord.setState((byte) 2);
            baseMapper.insert(usageRecord);

            throw new IllegalArgumentException("不在有效时间内");
        }

        // 当天是否已经使用过
        QueryRestRoomUsageRecordDto queryInfo = new QueryRestRoomUsageRecordDto();
        queryInfo.setRestRoomUserId(roomUser.getId());
        queryInfo.setState((byte) 1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        queryInfo.setStartTime(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        queryInfo.setEndTime(calendar.getTime());
        List<UsageRecord> usageRecords = queryRestRoomUsageRecord(queryInfo).getRecords();
        if (usageRecords.size() > 0) {
            RestRoomUsageRecord usageRecord = new RestRoomUsageRecord();
            BeanUtil.copyProperties(recordDto, usageRecord);
            usageRecord.setUseTime(new Date());
            usageRecord.setCreateTime(new Date());
            usageRecord.setDeleted(false);
            usageRecord.setState((byte) 3);
            baseMapper.insert(usageRecord);
            throw new IllegalArgumentException("重复签到");
        }

        // 保存签到记录
        RestRoomUsageRecord usageRecord = new RestRoomUsageRecord();
        BeanUtil.copyProperties(recordDto, usageRecord);
        usageRecord.setUseTime(new Date());
        usageRecord.setCreateTime(new Date());
        usageRecord.setDeleted(false);
        usageRecord.setState((byte) 1);
        if (claimDto != null) {
            usageRecord.setCreateUserId(claimDto.getUserId());
        }
        baseMapper.insert(usageRecord);
        return usageRecord.getId();
    }

    public Integer managerSaveRestRoomUsageRecord(CreateRestRoomUsageRecordByManageDto restRoomInfo, JwtClaimDto dto) {
        RestRoomUsageRecord record = new RestRoomUsageRecord();
        BeanUtil.copyProperties(restRoomInfo, record);
        record.setCreateTime(new Date());
        record.setCreateUserId(dto.getUserId());
        record.setDeleted(false);
        if (restRoomInfo.getState() == null) {
            record.setState((byte) 1);
        }
        baseMapper.insert(record);
        return record.getId();
    }

    public Boolean deleteRestRoomUsageRecord(Integer id, JwtClaimDto dto) {
        RestRoomUsageRecord usageRecord = baseMapper.selectById(id);
        if (usageRecord == null) {
            throw new IllegalArgumentException("未找到该数据");
        }

        return update(new LambdaUpdateWrapper<>(RestRoomUsageRecord.class)
                .set(RestRoomUsageRecord::getDeleted, true)
                .set(RestRoomUsageRecord::getDeletedTime, new Date())
                .set(RestRoomUsageRecord::getDeletedUserId, dto.getUserId())
                .eq(RestRoomUsageRecord::getId, id)
        );

    }

    /**
     * 统计使用记录
     *
     * @param statistics
     * @return
     */
    public RecordStatisticsOutput countRestRoomUsageRecord(RecordStatisticsInput statistics) {
        // 查询统计数据
        List<UsageRecordStatistics> records = baseMapper.selectList(statistics);
        if (records == null || records.isEmpty()) {
            return createEmptyStatisticsOutput();
        }

        RecordStatisticsOutput output = new RecordStatisticsOutput();
        output.setTotalCheckInCount(records.size());

        // 按房间和性别进行分组统计
        Map<Integer, RecordStatisticsOutput.UsageRecordStatisticsByRoom> roomMap = processRoomStatistics(records);
        output.setUsageRecordStatisticsByRoom(new ArrayList<>(roomMap.values()));

        // 设置总体性别统计
        setOverallGenderStatistics(output, records);

        // 按日期统计
        output.setUsageRecordStatisticsByDate(processDateStatistics(records));

        return output;
    }

    /**
     * 创建空的统计输出对象
     */
    private RecordStatisticsOutput createEmptyStatisticsOutput() {
        RecordStatisticsOutput output = new RecordStatisticsOutput();
        output.setTotalCheckInCount(0);
        output.setMaleCheckInCount(0);
        output.setFemaleCheckInCount(0);
        output.setUnknownGenderCheckInCount(0);
        output.setUsageRecordStatisticsByRoom(new ArrayList<>());
        output.setUsageRecordStatisticsByDate(new ArrayList<>());
        return output;
    }

    /**
     * 处理房间统计数据
     */
    private Map<Integer, RecordStatisticsOutput.UsageRecordStatisticsByRoom> processRoomStatistics(List<UsageRecordStatistics> records) {
        Map<Integer, RecordStatisticsOutput.UsageRecordStatisticsByRoom> roomMap = new ConcurrentHashMap<>();

            // 按房间分组
            Map<Integer, List<UsageRecordStatistics>> recordsByRoom = records.stream()
                    .collect(Collectors.groupingBy(UsageRecordStatistics::getRoomId));

            recordsByRoom.forEach((roomId, roomRecords) -> {
            RecordStatisticsOutput.UsageRecordStatisticsByRoom roomStats = createRoomStatistics(roomRecords.get(0));

                // 按性别统计
                Map<String, Long> genderCounts = roomRecords.stream()
                        .collect(Collectors.groupingBy(
                                record -> record.getSex() != null ? record.getSex() : "unknown",
                                Collectors.counting()
                        ));

                roomStats.setUnknownGenderCheckInCount(0);
                genderCounts.forEach((gender, count) -> {
                    switch (gender) {
                        case MALE_SEX_CODE:
                        roomStats.setMaleCheckInCount(count.intValue());
                            break;
                        case FEMALE_SEX_CODE:
                        roomStats.setFemaleCheckInCount(count.intValue());
                            break;
                        default:
                        roomStats.setUnknownGenderCheckInCount(roomStats.getUnknownGenderCheckInCount() + count.intValue());
                            break;
                    }
                });

                roomStats.setTotalCheckInCount(roomStats.getMaleCheckInCount() +
                        roomStats.getFemaleCheckInCount() +
                        roomStats.getUnknownGenderCheckInCount());

            roomMap.put(roomId, roomStats);
            });

        return roomMap;
    }

    /**
     * 创建房间统计对象
     */
    private RecordStatisticsOutput.UsageRecordStatisticsByRoom createRoomStatistics(UsageRecordStatistics record) {
        RecordStatisticsOutput.UsageRecordStatisticsByRoom roomStats = new RecordStatisticsOutput.UsageRecordStatisticsByRoom();
        roomStats.setRoomName(record.getRoomName());
        roomStats.setMaleCheckInCount(0);
        roomStats.setFemaleCheckInCount(0);
        roomStats.setUnknownGenderCheckInCount(0);
        roomStats.setTotalCheckInCount(0);
        return roomStats;
    }

    /**
     * 设置总体性别统计
     */
    private void setOverallGenderStatistics(RecordStatisticsOutput output, List<UsageRecordStatistics> records) {
        Map<String, Long> genderCounts = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getSex() != null ? record.getSex() : "unknown",
                        Collectors.counting()
                ));

        output.setUnknownGenderCheckInCount(0);
        genderCounts.forEach((gender, count) -> {
            switch (gender) {
                case MALE_SEX_CODE:
                    output.setMaleCheckInCount(count.intValue());
                    break;
                case FEMALE_SEX_CODE:
                    output.setFemaleCheckInCount(count.intValue());
                    break;
                default:
                    output.setUnknownGenderCheckInCount(count.intValue() + output.getUnknownGenderCheckInCount());
                    break;
            }
        });
    }

    /**
     * 处理日期统计数据
     */
    private List<RecordStatisticsOutput.UsageRecordStatisticsByDate> processDateStatistics(List<UsageRecordStatistics> records) {
        // 使用线程安全的日期格式化
        ThreadLocal<SimpleDateFormat> dateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

        // 按日期分组
        Map<String, List<UsageRecordStatistics>> recordsByDate = records.stream()
                    .collect(Collectors.groupingBy(record -> dateFormat.get().format(record.getUseTime())));

        return recordsByDate.entrySet().stream()
                .map(entry -> createDateStatistics(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 创建日期统计对象
     */
    private RecordStatisticsOutput.UsageRecordStatisticsByDate createDateStatistics(String date, List<UsageRecordStatistics> dateRecords) {
        RecordStatisticsOutput.UsageRecordStatisticsByDate dateStats = new RecordStatisticsOutput.UsageRecordStatisticsByDate();
        dateStats.setDate(date);
        dateStats.setTotalCheckInCount(dateRecords.size());
        dateStats.setRoomRecords(createRoomRecordsForDate(dateRecords));
        return dateStats;
    }

    /**
     * 创建日期下的房间记录
     */
    private List<RecordStatisticsOutput.BaseRoomRecord> createRoomRecordsForDate(List<UsageRecordStatistics> dateRecords) {
            // 按房间分组
            Map<Integer, List<UsageRecordStatistics>> recordsByRoom = dateRecords.stream()
                    .collect(Collectors.groupingBy(UsageRecordStatistics::getRoomId));

        return recordsByRoom.entrySet().stream()
                .map(entry -> {
                    RecordStatisticsOutput.BaseRoomRecord roomRecord = new RecordStatisticsOutput.BaseRoomRecord();
                    roomRecord.setRoomId(entry.getKey());
                    roomRecord.setRoomName(entry.getValue().get(0).getRoomName());
                    roomRecord.setTotalCheckInCount(entry.getValue().size());
                    return roomRecord;
                })
                .collect(Collectors.toList());
    }


    public UserRecordStatisticsOutput userUsageRecordStatistics(JwtClaimDto dto) {

        return baseMapper.selectUserUsageRecordStatistics(dto.getUserId());
    }
}
