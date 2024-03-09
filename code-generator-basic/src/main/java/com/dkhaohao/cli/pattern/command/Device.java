package com.dkhaohao.cli.pattern.command;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description: 接收者（receiver）
 * @date 2024/3/915:57
 */
public class Device {
    String name;

    public Device(String name) {
        this.name = name;
    }

    public void turnOff() {
        System.out.println("关机");
    }

    public void turnOn() {
        System.out.println("开机");
    }
}
