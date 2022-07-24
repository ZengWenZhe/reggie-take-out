package zwz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.AllArgsConstructor;
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
}
