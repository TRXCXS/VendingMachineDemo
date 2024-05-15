package com.example.VendingMachineDemo;

import entity.Layer;
import exception.ExceptionEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import utils.SortUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
class VendingMachineDemoApplicationTests {

    @Test
    void exceptionTest() {
        System.out.println(ExceptionEnum.SENSOR_EXCEPTION);
    }

    @Test
    void layersSortTest() {
        List<Layer> layers = new ArrayList<>();
        layers.add(new Layer(3, 100));
        layers.add(new Layer(1, 500));
        layers.add(new Layer(2, 75));

        SortUtils.sortLayersByIndex(layers);

        for (Layer layer : layers) {
            System.out.println(layer);
        }
    }

    @Test
    void purchaseTest() {
        /*
        HashSet<Goods> goodsSet = new HashSet<>();
        goodsSet.add(new Goods("000001", 10));
        goodsSet.add(new Goods("000002", 30));
        goodsSet.add(new Goods("000003", 50));
        goodsSet.add(new Goods("000004", 100));
        goodsSet.add(new Goods("000005", 250));
        goodsSet.add(new Goods("000006", 300));
        goodsSet.add(new Goods("000007", 500));
        goodsSet.add(new Goods("000008", 600));
        goodsSet.add(new Goods("000009", 800));
        goodsSet.add(new Goods("000010", 1000));
        */

        HashMap<String, Integer> goodsMap = new HashMap<>();
        goodsMap.put("000001", 10);
        goodsMap.put("000002", 30);
        goodsMap.put("000003", 50);
        goodsMap.put("000004", 100);
        goodsMap.put("000005", 250);
        goodsMap.put("000006", 300);
        goodsMap.put("000007", 500);
        goodsMap.put("000008", 600);
        goodsMap.put("000009", 800);
        goodsMap.put("000010", 1000);
    }
}
