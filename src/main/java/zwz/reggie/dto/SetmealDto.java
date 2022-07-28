package zwz.reggie.dto;


import lombok.Data;
import zwz.reggie.entity.Setmeal;
import zwz.reggie.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    //菜品管理新增菜品提交时，设计到多表，需要dto来保存数据
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
