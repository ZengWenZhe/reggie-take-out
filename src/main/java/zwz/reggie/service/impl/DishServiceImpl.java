package zwz.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zwz.reggie.entity.Dish;
import zwz.reggie.mapper.DishMapper;
import zwz.reggie.service.DishService;
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
