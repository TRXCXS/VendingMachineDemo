package exception;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    SENSOR_EXCEPTION(101, "传感器异常!"),
    FOREIGN_OBJECT_EXCEPTION(102, "检测到外来物品!"),
    UNRECOGNIZED_EXCEPTION(103, "商品组合识别异常!"),
    UNKNOWN_EXCEPTION(104, "未知异常!");

    private Integer exceptionCode;
    private String exceptionMessage;

    ExceptionEnum(Integer exceptionCode, String exceptionMessage) {
        this.exceptionCode = exceptionCode;
        this.exceptionMessage = exceptionMessage;
    }
}