package zwz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zwz.reggie.common.Result;
import zwz.reggie.entity.Employee;
import zwz.reggie.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWraper = new LambdaQueryWrapper<>();
        queryWraper.eq(Employee::getUsername,employee.getUsername());

        Employee emp=employeeService.getOne(queryWraper);
        if(emp==null){
            return  Result.error("用户名不存在！");
        }

        if(!emp.getPassword().equals(password)){
            return Result.error("用户名或密码错误!");
        }

        if (emp.getStatus()!=1){
            return Result.error("账号被禁用，请联系管理员或者客服！");
        }

        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }


    @RequestMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功！");
    }


    //新增员工
    @PostMapping
    public Result<String> employee(HttpServletRequest request,@RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));//设置初始密码,用md5加密
        employee.setCreateTime(LocalDateTime.now());        //获取当前的时间
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee"); //获得创建人，上面已经把登录的信息放在session中了

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        log.info(employee.toString()+"employee的信息-------------");
        employeeService.save(employee);
        return Result.success("新增员工成功！");
    }
}
