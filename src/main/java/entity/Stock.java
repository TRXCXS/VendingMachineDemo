package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    private String goodsId; // 库存对应的商品
    private int layer; // 库存对应的层架
    private int num; // 库存数量
}
