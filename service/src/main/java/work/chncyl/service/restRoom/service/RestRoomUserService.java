package work.chncyl.service.restRoom.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.chncyl.base.global.security.entity.JwtClaimDto;
import work.chncyl.base.global.utils.ExcelUtil;
import work.chncyl.service.permission.dto.input.Associate;
import work.chncyl.service.permission.entity.Role;
import work.chncyl.service.permission.mapper.RoleMapper;
import work.chncyl.service.restRoom.dto.*;
import work.chncyl.service.restRoom.entity.RestRoomUser;
import work.chncyl.service.restRoom.mapper.RestRoomUserMapper;
import work.chncyl.service.user.entity.Account;
import work.chncyl.service.user.entity.User;
import work.chncyl.service.user.enums.EnumAccountStatus;
import work.chncyl.service.user.mapper.AccountMapper;
import work.chncyl.service.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class RestRoomUserService extends ServiceImpl<RestRoomUserMapper, RestRoomUser> {

    private final AccountMapper accountMapper;
    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    public Integer saveRestRoomUser(CreateRestRoomUser restRoomUser, JwtClaimDto dto) {
        if (StringUtils.isBlank(restRoomUser.getPhoneNumber())) {
            throw new IllegalArgumentException("参数缺失");
        }
        if (restRoomUser.getState() == null) {
            restRoomUser.setState((byte) 1);
        }

        QueryRestRoomUserDto queryInfo = new QueryRestRoomUserDto();
        queryInfo.setPhoneNumber(restRoomUser.getPhoneNumber());
        List<RestRoomUserInfo> listResponseEntity = queryRestRoomUser(queryInfo).getRecords();
        if (!listResponseEntity.isEmpty()) {
            throw new IllegalArgumentException("该手机号已存在");
        }

        // 保存用户账户信息
        Integer userId = registerAccount(restRoomUser);

        RestRoomUser user = new RestRoomUser();
        BeanUtil.copyProperties(restRoomUser, user);
        user.setCreateUserId(dto.getUserId());
        user.setCreateTime(new Date());
        user.setDeleted(false);
        user.setUserId(userId);

        baseMapper.insert(user);

        return user.getId();
    }

    public Boolean editRestRoomUser(EditRestRoomUser restRoomUser, JwtClaimDto dto) {
        if (restRoomUser.getUserId() == null
                || StringUtils.isBlank(restRoomUser.getPhoneNumber()))
            throw new IllegalArgumentException("参数缺失");

        RestRoomUser user = baseMapper.selectById(restRoomUser.getUserId());

        BeanUtil.copyProperties(restRoomUser, user);
        if (user.getState() == null)
            restRoomUser.setState((byte) 1);
        user.setUpdateUserId(dto.getUserId());
        user.setUpdateTime(new Date());

        return updateById(user);
    }

    public Boolean deleteRestRoomUser(Integer id, JwtClaimDto dto) {
        RestRoomUser user = baseMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("未找到该数据");
        }

        user.setDeleted(true);
        user.setDeletedTime(new Date());
        user.setDeletedUserId(dto.getUserId());

        return update(new LambdaUpdateWrapper<RestRoomUser>()
                .set(RestRoomUser::getDeleted, true)
                .set(RestRoomUser::getDeletedTime, new Date())
                .set(RestRoomUser::getDeletedUserId, dto.getUserId())
                .eq(RestRoomUser::getId, id)
        );

    }

    public Boolean associatedWx(Associated associated) {
        if (StringUtils.isBlank(associated.getPhoneNumber())
                || StringUtils.isBlank(associated.getWxOpenId())
        ) {
            throw new IllegalArgumentException("参数缺失");
        }

        QueryRestRoomUserDto queryDto = new QueryRestRoomUserDto();
        queryDto.setPhoneNumber(associated.getPhoneNumber());
        List<RestRoomUserInfo> listResponseEntity = queryRestRoomUser(queryDto).getRecords();
        if (listResponseEntity.isEmpty()) {
            throw new IllegalArgumentException("用户不存在");
        }
        return update(new LambdaUpdateWrapper<RestRoomUser>()
                .set(RestRoomUser::getWxOpenId, associated.getWxOpenId())
                .eq(RestRoomUser::getId, listResponseEntity.get(0).getId())
        );
    }

    public Page<RestRoomUserInfo> queryRestRoomUser(QueryRestRoomUserDto queryInfo) {
        Page<RestRoomUserInfo> page = new Page<>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        return baseMapper.queryRestRoomUser(page, queryInfo);
    }

    /**
     * 导出休息房用户数据到Excel
     *
     * @param response  HttpServletResponse
     * @param queryInfo 查询条件
     * @throws IOException IO异常
     */
    public void exportRestRoomUser(HttpServletResponse response, QueryRestRoomUserDto queryInfo) throws IOException {
        Page<RestRoomUserInfo> page = new Page<>(1, Integer.MAX_VALUE);
        Page<RestRoomUserInfo> result = baseMapper.queryRestRoomUser(page, queryInfo);

        List<ImportRestRoomUser> exportData = result.getRecords().stream().map(user -> {
            ImportRestRoomUser exportUser = new ImportRestRoomUser();
            exportUser.setUserName(user.getUserName());
            exportUser.setPhoneNumber(user.getPhoneNumber());
            // 转换性别
            String sex = null;
            if ("Sex-100".equals(user.getSex())) {
                sex = "男";
            } else if ("Sex-101".equals(user.getSex())) {
                sex = "女";
            }
            exportUser.setSex(sex);
            exportUser.setState(user.getState() == 1 ? "启用" : "禁用");
            exportUser.setWxOpenId(user.getWxOpenId());
            return exportUser;
        }).collect(Collectors.toList());

        ExcelUtil.exportExcel(response, "休息房用户数据", "用户列表", ImportRestRoomUser.class, exportData);
    }

    /**
     * 从Excel导入休息房用户数据
     *
     * @param file Excel文件
     * @param dto  当前用户信息
     * @return 导入结果信息
     * @throws IOException IO异常
     */
    public String importRestRoomUser(MultipartFile file, JwtClaimDto dto) throws IOException {
        List<ImportRestRoomUser> importData = ExcelUtil.importExcel(file, ImportRestRoomUser.class);

        if (importData == null || importData.isEmpty()) {
            throw new IllegalArgumentException("Excel文件中没有数据");
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMsg = new StringBuilder();

        for (int i = 0; i < importData.size(); i++) {
            ImportRestRoomUser importUser = importData.get(i);
            try {
                // 验证必填字段
                if (StringUtils.isBlank(importUser.getPhoneNumber())) {
                    throw new IllegalArgumentException("手机号不能为空");
                }

                // 检查手机号是否已存在
                QueryRestRoomUserDto queryInfo = new QueryRestRoomUserDto();
                queryInfo.setPhoneNumber(importUser.getPhoneNumber());
                List<RestRoomUserInfo> existingUsers = queryRestRoomUser(queryInfo).getRecords();
                if (!existingUsers.isEmpty()) {
                    throw new IllegalArgumentException("手机号已存在");
                }

                // 转换状态
                byte state = 1; // 默认启用
                if ("禁用".equals(importUser.getState())) {
                    state = 0;
                }
                // 转换性别
                String sex = null;
                if ("男".equals(importUser.getSex())) {
                    sex = "Sex-100";
                } else if ("女".equals(importUser.getSex())) {
                    sex = "Sex-101";
                }

                // 创建用户DTO
                CreateRestRoomUser userDto = new CreateRestRoomUser();
                userDto.setUserName(importUser.getUserName());
                userDto.setPhoneNumber(importUser.getPhoneNumber());
                userDto.setSex(sex);
                userDto.setState(state);
                userDto.setWxOpenId(importUser.getWxOpenId());

                // 保存用户
                saveRestRoomUser(userDto, dto);
                successCount++;

            } catch (Exception e) {
                failCount++;
                errorMsg.append("第").append(i + 1).append("行数据导入失败: ").append(e.getMessage()).append("; ");
            }
        }
        if (errorMsg.length() == 0) {
            return "导入完成，成功" + successCount + "条";
        }

        return String.format("导入完成，成功%d条，失败%d条。失败原因：%s", successCount, failCount, errorMsg.toString());
    }

    private Integer registerAccount(CreateRestRoomUser input) {
        //根据用户名查询用户基本信息
        Account acc = accountMapper.selectOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getAccountName, input.getPhoneNumber())
                .eq(Account::getIsDeleted, false)
                .last("LIMIT 1")
        );
        if (acc != null) {
            return acc.getUserId();
        }

        // 用户信息
        User user = BeanUtil.copyProperties(input, User.class);
        user.setCreateTime(new Date());
        user.setIsDeleted(false);
        user.setPhoneNum(input.getPhoneNumber());
        userMapper.insert(user);

        // 获取默认角色
        List<Role> role = roleMapper.getDefaultRole();
        if (role != null && !role.isEmpty()) {
            Associate associate = new Associate(user.getId(), role.stream().map(Role::getId).collect(Collectors.toList()));
            roleMapper.associateRole(associate);
        }

        // 账号信息
        Account account = BeanUtil.copyProperties(input, Account.class);
        account.setAccountName(input.getPhoneNumber());
        account.setAccountStatus(EnumAccountStatus.ENABLE.getValue().byteValue());
        account.setAccountType((byte) 0);
        account.setUserId(user.getId());
        if (StringUtils.isNotBlank(input.getWxOpenId())) {
            account.setWechatOpenId(input.getWxOpenId());
        }
        // 保存md5加密的密码
        account.setAccountPwd(new Digester(DigestAlgorithm.MD5).digestHex("123456"));
        accountMapper.insert(account);

        return user.getId();
    }

    public Integer getRestRoomUserId(JwtClaimDto dto) {
        Integer userId = dto.getUserId();
        RestRoomUser user = baseMapper.selectOne(new LambdaQueryWrapper<RestRoomUser>()
                .eq(RestRoomUser::getUserId, userId)
                .eq(RestRoomUser::getDeleted, false)
                .eq(RestRoomUser::getState, 1)
        );
        if (user != null) {
            return user.getId();
        }
        return null;
    }
}
