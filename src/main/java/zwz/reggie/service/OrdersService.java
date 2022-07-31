package zwz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zwz.reggie.entity.OrderDetail;
import zwz.reggie.entity.Orders;


import java.util.List;

public interface OrdersService extends IService<Orders> {

    public void submit(Orders orders);

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId);
}
