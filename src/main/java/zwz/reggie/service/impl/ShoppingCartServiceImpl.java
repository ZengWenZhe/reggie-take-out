package zwz.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import zwz.reggie.entity.ShoppingCart;
import zwz.reggie.mapper.ShoppingCartMapper;
import zwz.reggie.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
     implements ShoppingCartService {
}
