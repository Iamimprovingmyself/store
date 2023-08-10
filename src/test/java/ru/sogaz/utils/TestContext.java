package ru.sogaz.utils;


public class TestContext { //утилити класс для сохранения даннных в контекст и передачи между шагами

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    private Integer orderId;


}
