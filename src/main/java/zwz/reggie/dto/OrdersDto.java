package zwz.reggie.dto;


import lombok.Data;
import zwz.reggie.entity.OrderDetail;
import zwz.reggie.entity.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

}
