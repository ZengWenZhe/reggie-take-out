package zwz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import zwz.reggie.common.MyCustomException;
import zwz.reggie.common.Result;
import zwz.reggie.dto.SetmealDto;
import zwz.reggie.entity.Setmeal;
import zwz.reggie.entity.SetmealDish;
import zwz.reggie.service.SetMealService;
import zwz.reggie.service.SetmealDishService;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetmealDishService setmealDishService;

    //分页查询信息到主页,和模糊查询
    @RequestMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        Page<SetmealDto> totalPage = setMealService.page(page, pageSize, name);
        return Result.success(totalPage);
    }



    //将新增套餐的基本信息以及关联的菜品信息保存到数据库
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> addMeal(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setMealService.saveWithDish(setmealDto);
        return Result.success("成功新增套餐!");
    }

    //修改套餐功能，回显数据到浏览器
    @GetMapping("/{id}")
    public Result<SetmealDto> update(@PathVariable long id){
        SetmealDto mealAll = setMealService.meal(id);
        return Result.success(mealAll);
    }

    //将修改的套餐重新提交,逻辑时先将此表在数据库里面删除，再重新提交写回数据库
    @PutMapping
    public Result<String> submit(@RequestBody SetmealDto setmealDto){
        //删除逻辑
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(queryWrapper);
        setMealService.removeById(id);

        //再调用保存的方法
        setMealService.saveWithDish(setmealDto);
        return Result.success("修改成功！");
    }

    // 前端发送的请求：http://localhost:8181/setmeal/list?categoryId=1516353794261180417&status=1
    // 注意: 请求后的参数 是以key-value键值对的方式 传入，而非JSON格式，不需要使用@RequestBody 来标注，
    //   只需要用包含 参数(key)的实体对象接收即可

    @GetMapping("/list")  // 在消费者端 展示套餐信息
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_' +#setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();
        queryWrapper.eq(categoryId != null,Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(status != null,Setmeal::getStatus,status);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmeals = setMealService.list(queryWrapper);

        return Result.success(setmeals);
    }



    //删除套餐
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)   //  删除套餐，就要删除套餐相关的所有缓存数据
    public Result<String> delete(@RequestParam("ids") List<Long> ids){
        setMealService.DeleteSetmeal(ids);
        return Result.success("成功删除套餐！");
    }

    //修改套餐状态
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable("status") Integer status,@RequestParam("ids") List<Long> ids){

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Setmeal::getId,ids);

        List<Setmeal> list = setMealService.list(queryWrapper);
        if (list != null){
            for (Setmeal setmeal : list) {
                setmeal.setStatus(status);
                setMealService.updateById(setmeal);
            }
            return Result.success("套餐状态修改成功！");
        }

        return Result.error("套餐状态不能修改,请联系管理或客服！");
    }
}
