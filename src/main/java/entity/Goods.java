package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {
    private String id; // 6 位的商品编号，每个商品唯一
    private int weight; // 商品单件重量，单位 g
}
