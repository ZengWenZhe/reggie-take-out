package zwz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zwz.reggie.common.BaseContext;
import zwz.reggie.common.Result;
import zwz.reggie.entity.ShoppingCart;
import zwz.reggie.service.ShoppingCartService;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result<ShoppingCart> addToCart(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车中的数据:{}"+shoppingCart.toString());

        //设置用户id,指定当前是哪个用户的 购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // 查询当前菜品或套餐是否 在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);  // 根据登录用户的 userId去ShoppingCart表中查询该用户的购物车数据

        if (dishId != null){ // 添加进购物车的是菜品，且 购物车中已经添加过 该菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }


        ShoppingCart oneCart = shoppingCartService.getOne(queryWrapper);
        //  如果购物车中 已经存在该菜品或套餐，其数量+1，不存在，就将该购物车数据保存到数据库中
        if (oneCart != null){
            Integer number = oneCart.getNumber();
            oneCart.setNumber(number + 1);

            shoppingCartService.updateById(oneCart);
        }else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);

            oneCart = shoppingCart;
        }


        return Result.success(oneCart);
    }

    // 在购物车中删减订单
    @PostMapping("/sub")
    public Result<String> subToCart(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车中的数据:{}"+shoppingCart.toString());

        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询当前菜品或套餐是否 在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());  // 根据登录用户的 userId去ShoppingCart表中查询该用户的购物车数据

        if (dishId != null){ // 添加进购物车的是菜品，且 购物车中已经添加过 该菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }


        ShoppingCart oneCart = shoppingCartService.getOne(queryWrapper);
        //  如果购物车中 已经存在该菜品或套餐，其数量+1，不存在，就将该购物车数据保存到数据库中
        if (oneCart != null){
            Integer number = oneCart.getNumber();
            if (number != 0){
                oneCart.setNumber(number - 1);
                shoppingCartService.updateById(oneCart);
            }else {
                shoppingCartService.remove(queryWrapper);
            }

        }
        return Result.success("成功删减订单!");
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        // 最晚下单的 菜品或套餐在购物车中最先展示
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result<String> cleanCart(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // DELETE FROM shopping_cart WHERE (user_id = ?)
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());


        shoppingCartService.remove(queryWrapper);

        return Result.success("成功清空购物车！");
    }


}
