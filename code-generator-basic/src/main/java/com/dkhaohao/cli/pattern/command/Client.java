package com.dkhaohao.cli.pattern.command;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description: 客户端（client）
 * @date 2024/3/916:02
 */
public class Client {
    public static void main(String[] args) {
        Device tv = new Device("TV");
        Device ps = new Device("PS");
        TurnOnCommand turnOnCommand = new TurnOnCommand(tv);
        TurnOnCommand turnOnCommand1 = new TurnOnCommand(ps);
        RemoteControl remoteControl = new RemoteControl();
        remoteControl.setCommand(turnOnCommand);
        remoteControl.pressButton();
    }
}
