package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendingMachine {
    private int id;
    private String type;
    private int layerNumber;
    private List<Layer> layers;
}
