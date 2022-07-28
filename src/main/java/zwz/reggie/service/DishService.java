package zwz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zwz.reggie.dto.DishDto;
import zwz.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //  新增菜品，同时插入菜品对应的数据，需要操作两张表: dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据dishId查询 菜品信息和口味信息
    public DishDto getByDishIdWithFlavor(Long dishId);

    // 更新 菜品及对应的口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品信息
    void batchDeleteByIds(List<Long> ids);


}
