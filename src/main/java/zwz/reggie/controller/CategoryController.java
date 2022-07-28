package zwz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zwz.reggie.common.Result;
import zwz.reggie.entity.Category;
import zwz.reggie.entity.Dish;
import zwz.reggie.service.CategoryService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/category")

public class CategoryController {

    @Autowired
    CategoryService categoryService;

    //主页分类
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        log.info("page={}和pageSize={}",page,pageSize);
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByDesc(Category::getUpdateTime);
        categoryService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }

    //新增菜品信息
    @PostMapping
    public Result<String> insert(@RequestBody Category category){

        categoryService.save(category);
        return Result.success("已成功添加菜系！");
    }
    //修改菜品信息
    @PutMapping
    public Result<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.success("已成功修改菜系！");
    }


    //删除菜品信息
    @DeleteMapping
    public Result<String> delete(long ids){
        log.info("id={}",ids);
        categoryService.remove(ids);
        return Result.success("已经成功删除菜品信息！");
    }

    //新增菜品，菜品分类的下拉框
    @GetMapping("/list")
    public Result<List> list(Category category){  //这里本来接受的时type类型参数，但是转化成category好操作
        //log.info("type={}",type);
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //非空等值查询
        categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //按排序升序，如果排序一样按照更新时间降序
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //封装到list集合
        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
        return Result.success(list);

    }


}
