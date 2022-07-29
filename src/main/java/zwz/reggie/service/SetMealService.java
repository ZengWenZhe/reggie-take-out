package zwz.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import zwz.reggie.dto.SetmealDto;
import zwz.reggie.entity.Setmeal;

import java.util.List;


public interface SetMealService extends IService<Setmeal> {
    //主页分页和模糊查询
    public Page<SetmealDto> page(int  page,int pageSize,String name);

    //将套餐的基本信息以及关联的菜品信息保存到数据库
    public void saveWithDish(SetmealDto setmealDto);

    //修改套餐功能，回显数据到浏览器
    public SetmealDto meal(long id);

    //删除套餐功能
    public void DeleteSetmeal(List<Long> ids);
}
