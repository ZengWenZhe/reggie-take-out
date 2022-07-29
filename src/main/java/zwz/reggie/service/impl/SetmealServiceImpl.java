package zwz.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zwz.reggie.common.MyCustomException;
import zwz.reggie.dto.SetmealDto;
import zwz.reggie.entity.Category;
import zwz.reggie.entity.Setmeal;
import zwz.reggie.entity.SetmealDish;
import zwz.reggie.mapper.SetmealMapper;
import zwz.reggie.service.CategoryService;
import zwz.reggie.service.SetMealService;
import zwz.reggie.service.SetmealDishService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    //主页分页和模糊查询
    @Override
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        //分页模糊查询
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapperJ = new LambdaQueryWrapper<>();
        queryWrapperJ.like(name != null, Setmeal::getName, name);
        queryWrapperJ.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(pageInfo, queryWrapperJ);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        //细划成页面每条数据，为起赋值category的名字
        List<Setmeal> records =pageInfo.getRecords();
        List dtoList = records.stream().map((setmeal) -> {   //setmeal为是每个套餐对象
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            Long categoryId = setmeal.getCategoryId();  //菜品分类的id
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }

            return setmealDto;
        }).collect(Collectors.toList());
        Page pageTotal = setmealDtoPage.setRecords(dtoList);
        return pageTotal;
    }


    //将套餐的基本信息以及关联的菜品信息保存到数据库
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //将基本的信息保存到setmeal表中，setmealdto继承setmeal
        this.save(setmealDto);

        //获取到的时lit集合，将集合的信息要保存到setmeal_dish表中，但是没有保存setmealId

        //可以使用stream流的形式，也可以使用for循环的形式
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    //修改套餐功能，回显数据到浏览器
    @Override
    public SetmealDto meal(long id) {
  /*      LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getId,id);*/
        Setmeal setmeal = setMealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    //删除套餐功能
    @Transactional
    @Override
    public void DeleteSetmeal(List<Long> ids) {
        for (Long id : ids) {
            //删之前收先判断套餐状态
            LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(Setmeal::getId,id);
            wrapper.eq(Setmeal::getStatus,1);
            int count = setMealService.count(wrapper);
            if(count>0){
                throw new MyCustomException("有套餐在售卖中，无法删除!");
            }else {
                LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
                queryWrapper.eq(SetmealDish::getSetmealId,id);
                setmealDishService.remove(queryWrapper);
                setMealService.removeById(id);
            }

        }

    }
}
