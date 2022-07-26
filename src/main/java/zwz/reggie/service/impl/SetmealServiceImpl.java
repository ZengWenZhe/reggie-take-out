package zwz.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zwz.reggie.entity.Setmeal;
import zwz.reggie.mapper.SetmealMapper;
import zwz.reggie.service.SetMealService;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetMealService {
}
