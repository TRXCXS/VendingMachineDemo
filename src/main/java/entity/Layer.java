package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Layer {
    private int index; // 编号，从 1 开始
    private int weight; // 重量传感器数值，单位 g
}
