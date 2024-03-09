package com.dkhaohao.cli.pattern.command;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description: ConcreteCommand（具体命令）
 * @date 2024/3/915:59
 */
public class TurnOffCommand implements Command{
    private Device device;

    public TurnOffCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOff();
    }
}
