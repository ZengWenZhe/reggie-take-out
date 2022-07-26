package zwz.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zwz.reggie.common.GlobalExceptionHander;
import zwz.reggie.common.MyCustomException;
import zwz.reggie.entity.Category;
import zwz.reggie.entity.Dish;
import zwz.reggie.entity.Setmeal;
import zwz.reggie.mapper.CategoryMapper;
import zwz.reggie.service.CategoryService;
import zwz.reggie.service.DishService;
import zwz.reggie.service.SetMealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private SetMealService setMealService;

    @Autowired
    private DishService dishService;


    @Override
    public void remove(long ids) {
        //通过id查看Setmeal里面是否有东西
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,ids);
        int count1 = setMealService.count(queryWrapper);
        if(count1>0){
            //已经关联套餐，抛出业务异常
            throw new MyCustomException("已经关联了套餐，无法删除！");
        }

        //通过id查看Dish里面是否有东西
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count2 = dishService.count(dishLambdaQueryWrapper);
        if(count2>0){
            //已经关联菜品，抛出业务异常
            throw new MyCustomException("已经关联了菜品，无法删除！");
        }

        //正常删除  super就是CategoryService因为这里时实现过来的
        super.removeById(ids);
    }
}
