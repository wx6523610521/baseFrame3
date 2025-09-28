package work.chncyl.service.restRoom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import work.chncyl.base.global.aspect.annotation.AllowAnonymous;
import work.chncyl.base.global.aspect.annotation.CurrentUser;
import work.chncyl.base.global.pojo.IntPKDto;
import work.chncyl.base.global.result.ApiResult;
import work.chncyl.base.global.security.entity.JwtClaimDto;
import work.chncyl.service.restRoom.dto.*;
import work.chncyl.service.restRoom.entity.RestRoom;
import work.chncyl.service.restRoom.entity.RestRoomUser;
import work.chncyl.service.restRoom.service.RestRoomService;
import work.chncyl.service.restRoom.service.RestRoomUsageRecordService;
import work.chncyl.service.restRoom.service.RestRoomUserService;

@RestController
@RequestMapping("/restRoom")
@Api(tags = "休息房")
@RequiredArgsConstructor
public class RestRoomController {
    private final RestRoomService restRoomService;
    private final RestRoomUserService restRoomUserService;
    private final RestRoomUsageRecordService restRoomUsageRecordService;

    @PostMapping("/saveRestRoom")
    @ApiOperation(value = "新增休息房")
    public ApiResult<Integer> saveRestRoom(@RequestBody CreateRestRoomDto restRoomInfo, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomService.saveRestRoom(restRoomInfo, dto));
    }

    @PostMapping("/deleteRestRoom")
    @ApiOperation(value = "删除休息房")
    public ApiResult<Boolean> deleteRestRoom(@RequestBody IntPKDto id, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomService.deleteRestRoom(id.getId(), dto));
    }


    @PostMapping("/updateRestRoom")
    @ApiOperation(value = "更新休息房")
    public ApiResult<Boolean> updateRestRoom(@RequestBody UpdateRestRoomDto restRoomInfo, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomService.updateRestRoom(restRoomInfo, dto));
    }

    @GetMapping("/queryRestRoom")
    @ApiOperation(value = "查询休息房")
    public ApiResult<Page<RestRoom>> queryRestRoom(QueryRestRoomDto queryInfo) {
        return ApiResult.success(restRoomService.queryRestRoom(queryInfo));
    }

    @GetMapping("/getRestRoom")
    @ApiOperation(value = "休息房信息")
    public ApiResult<RestRoom> getRestRoom(IntPKDto id) {
        return ApiResult.success(restRoomService.getRestRoom(id.getId()));
    }

    @ApiOperation(value = "新增休息房用户")
    @PostMapping("/saveRestRoomUser")
    public ApiResult<Integer> saveRestRoomUser(@RequestBody CreateRestRoomUser restRoomUser, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUserService.saveRestRoomUser(restRoomUser,  dto));
    }

    @ApiOperation(value = "删除休息房用户")
    @PostMapping("/deleteRestRoomUser")
    public ApiResult<Boolean> deleteRestRoomUser(@RequestBody IntPKDto id, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUserService.deleteRestRoomUser(id.getId(), dto));
    }

    @ApiOperation(value = "新增休息房用户")
    @PostMapping("/editRestRoomUser")
    public ApiResult<Boolean> editRestRoomUser(@RequestBody EditRestRoomUser restRoomUser, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUserService.editRestRoomUser(restRoomUser,  dto));
    }

    @ApiOperation(value = "关联休息房用户微信信息")
    @PostMapping("/associatedWx")
    public ApiResult<Boolean> associatedWx(@RequestBody Associated associated) {
        return ApiResult.success(restRoomUserService.associatedWx(associated));
    }

    @ApiOperation(value = "查询休息房用户")
    @GetMapping("/queryRestRoomUser")
    public ApiResult<Page<RestRoomUserInfo>> queryRestRoomUser(QueryRestRoomUserDto queryInfo) {
        return ApiResult.success(restRoomUserService.queryRestRoomUser(queryInfo));
    }


    @GetMapping("/queryRestRoomUsageRecord")
    @ApiOperation(value = "查询休息房使用记录")
    public ApiResult<Page<UsageRecord>> queryRestRoomUsageRecord(QueryRestRoomUsageRecordDto queryUsageInfo) {
        return ApiResult.success(restRoomUsageRecordService.queryRestRoomUsageRecord(queryUsageInfo));
    }

    @GetMapping("/queryCurrentUserUsageRecord")
    @ApiOperation(value = "查询休息房使用记录")
    public ApiResult<Page<UsageRecord>> queryCurrentUserUsageRecord(QueryCurrentUserUsageRecordDto queryUsageInfo,@CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUsageRecordService.queryCurrentUserUsageRecord(queryUsageInfo,dto));
    }

    @PostMapping("/saveRestRoomUsageRecord")
    @ApiOperation(value = "保存休息房使用记录")
    @AllowAnonymous
    public ApiResult<Integer> saveRestRoomUsageRecord(@RequestBody CreateRestRoomUsageRecordDto restRoomInfo, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUsageRecordService.saveRestRoomUsageRecord(restRoomInfo, dto));
    }

    @PostMapping("/managerSaveRestRoomUsageRecord")
    @ApiOperation(value = "管理员手动添加保存休息房使用记录")
    public ApiResult<Integer> managerSaveRestRoomUsageRecord(@RequestBody CreateRestRoomUsageRecordByManageDto restRoomInfo, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUsageRecordService.managerSaveRestRoomUsageRecord(restRoomInfo, dto));
    }

    @PostMapping("/deleteRestRoomUsageRecord")
    @ApiOperation(value = "删除休息房使用记录")
    public ApiResult<Boolean> deleteRestRoomUsageRecord(@RequestBody IntPKDto id, @CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUsageRecordService.deleteRestRoomUsageRecord(id.getId(), dto));
    }

    @ApiOperation(value = "用房记录统计")
    @GetMapping("/countRestRoomUsageRecord")
    public ApiResult<RecordStatisticsOutput> countRestRoomUsageRecord(RecordStatisticsInput statistics) {
        return ApiResult.success(restRoomUsageRecordService.countRestRoomUsageRecord(statistics));
    }

    @ApiOperation(value = "当前用户用房统计")
    @GetMapping("/userUsageRecordStatistics")
    public ApiResult<UserRecordStatisticsOutput> userUsageRecordStatistics(@CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUsageRecordService.userUsageRecordStatistics(dto));
    }

    @ApiOperation(value = "导出休息房用户数据")
    @GetMapping("/exportRestRoomUser")
    public void exportRestRoomUser(HttpServletResponse response, QueryRestRoomUserDto queryInfo) throws IOException {
        restRoomUserService.exportRestRoomUser(response, queryInfo);
    }

    @ApiOperation(value = "导入休息房用户数据")
    @PostMapping("/importRestRoomUser")
    public ApiResult<String> importRestRoomUser(@RequestParam("file") MultipartFile file, @CurrentUser JwtClaimDto dto) throws IOException {
        String result = restRoomUserService.importRestRoomUser(file, dto);
        return ApiResult.success(result);
    }

    @ApiOperation(value = "获取当前用户信息ID")
    @GetMapping("/getUserId")
    public ApiResult<Integer> getRestRoomUserId(@CurrentUser JwtClaimDto dto) {
        return ApiResult.success(restRoomUserService.getRestRoomUserId(dto));
    }

}
