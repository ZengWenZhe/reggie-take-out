package zwz.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zwz.reggie.common.MyCustomException;
import zwz.reggie.dto.DishDto;
import zwz.reggie.entity.Dish;
import zwz.reggie.entity.DishFlavor;
import zwz.reggie.mapper.DishMapper;
import zwz.reggie.service.DishFlavorService;
import zwz.reggie.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override//新增菜品，同时保存口味数据
    //保存菜品的基本信息到菜品表

    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        //保存口味数据
        // dishFlavorService.saveBatch(dishDto.getFlavors()); 只这样保存的话，只会将口味信息保存起来，不会保存id
        Long dishId = dishDto.getId(); //获取到菜品的ID
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品的口味到口味表 dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByDishIdWithFlavor(Long dishId) {
        // 只是关联查询两张表，没有涉及事务，不用加 @Transactional
        // 从dish表中查询 菜品的基本信息
        Dish dish = this.getById(dishId);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //  从dish_flavor表查询 当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);

        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);
        return dishDto;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表
        this.updateById(dishDto);

        // 删除当前菜品对应的口味数据，dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 添加 前端提交过来的口味数据，insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> flavorList = flavors.stream().map((flavor) -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavorList);
    }



    // 更新 菜品及对应的口味信息
    public DishDto updateWithFlavor(long id) {
        // 只是关联查询两张表，没有涉及事务，不用加 @Transactional

        DishDto dishDto = new DishDto();
        // 从dish表中查询 菜品的基本信息
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);
        //  从dish_flavor表查询 当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);
        return dishDto;
    }


    @Override
    public void batchDeleteByIds(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);

        //  mybatisplus提供了 list方法，故 this.list(queryWrapper); -->dishService.list(queryWrapper);
        List<Dish> list = this.list(queryWrapper);

        if (list != null){
            for (Dish dish : list) {
                if (dish.getStatus() == 0){
                    this.removeByIds(ids);
                }else {
                    throw new MyCustomException("有菜品正在售卖，无法全部删除！");
                }
            }
        }


    }




}
