package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.sky.result.Result.success;


/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("登陆")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        log.info("员工id：" + employee.getId());
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return success(employeeLoginVO);
    }

    @PostMapping("/logout")
    @ApiOperation("退出登陆")
    public Result<String> logout() {
        return success();
    }

    @PostMapping()
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        return employeeService.save(employeeDTO);
    }


    @GetMapping("/page")
    @ApiOperation("分页/按名称查询员工")
    public Result pageQuery(EmployeePageQueryDTO pageQueryDTO) {
        PageResult pageResult = employeeService.pageQuery(pageQueryDTO);
        return success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("对账号的启动和禁用")
    public Result changeStatus(@PathVariable("status") Integer status,@RequestParam("id") Long id) {
        System.out.println("status:" + status + " id :" + id);
        Result result = employeeService.changeStatus(status,id);
        return success();
    }

    @GetMapping("{id}")
    @ApiOperation("根据id获取员工信息")
    public Result<Employee> getById(@PathVariable("id") Long id) {
        return employeeService.getById(id);
    }

    @PutMapping()
    @ApiOperation("修改员工信息")
    public Result editEmployee(@RequestBody EmployeeDTO employeeDTO) {
        System.out.println("employeeDTO:" + employeeDTO);
        return employeeService.editEmployee(employeeDTO);
    }

}
