package zwz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zwz.reggie.common.Result;
import zwz.reggie.dto.DishDto;
import zwz.reggie.dto.SetmealDto;
import zwz.reggie.entity.Category;
import zwz.reggie.entity.Dish;
import zwz.reggie.entity.Setmeal;
import zwz.reggie.service.CategoryService;
import zwz.reggie.service.DishFlavorService;
import zwz.reggie.service.DishService;
import zwz.reggie.service.SetMealService;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/dish")
@Slf4j
@RestController
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetMealService setMealService;

    //  分页展示菜品信息
    // dish/page?page=1&pageSize=10&name=122334,name 是搜索框中的输入值
    @GetMapping("/page")
    public Result<Page> pageShow(int page,int pageSize,String name){

        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);

        //  执行分页查询
        dishService.page(dishPage,queryWrapper);

        //  将 dish 中的属性值复制到 dtoPage，但是忽略 records
        //  records需要另外去设置
        BeanUtils.copyProperties(dishPage,dtoPage,"records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> dtoList = records.stream().map((dish) -> {  // dish 为每个菜品对象
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(dish,dishDto);

            Long categoryId = dish.getCategoryId();  // 菜品的分类id

            Category category = categoryService.getById(categoryId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }

    //修改菜品信息
     @GetMapping("/{id}")
    public Result<DishDto> getId(@PathVariable long id){
         DishDto dishDto = dishService.getByDishIdWithFlavor(id);
         return Result.success(dishDto);

    }


    //添加菜品
    @PostMapping
    public Result<String> add(@RequestBody DishDto dishDto) {
        log.info("菜品的名字是={}", dishDto.getName());
        dishService.saveWithFlavor(dishDto);
        return Result.success("成功添加");
    }


    //修改菜品信息
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);
        return Result.success("修改菜品操作成功！");
    }

    @PostMapping("/status/{status}")
    //  菜品具体的售卖状态 由前端修改并返回，该方法传入的status是 修改之后的售卖状态，可以直接根据一个或多个菜品id进行查询并修改售卖即可
    public Result<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        log.info("状态为={},id为={}",status,ids);
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Dish::getId,ids);   //当前端传过来的参数和表字段不匹配，则用 queryWrapper.in方法

        List<Dish> list = dishService.list(queryWrapper);
        if(list!=null){
            for (Dish dish : list) {
                dish.setStatus(status);
                dishService.updateById(dish);

            }
            return Result.success("菜品的售卖状态已更改！");
        }
        return Result.error("售卖状态不可更改,请联系管理员或客服！");
    }


    @DeleteMapping
    public Result<String> batchDelete(@RequestParam("ids") List<Long> ids){
        dishService.batchDeleteByIds(ids);

        return Result.success("成功删除菜品！");
    }


    //新增套餐时添加菜品下拉框数据映射
    @GetMapping("/list")
    public Result<List<Dish>> addMeal(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(dish !=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return Result.success(list);

    }


}
