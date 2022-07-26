package zwz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zwz.reggie.entity.Category;


public interface CategoryService extends IService<Category> {
    public void remove(long ids);
}
