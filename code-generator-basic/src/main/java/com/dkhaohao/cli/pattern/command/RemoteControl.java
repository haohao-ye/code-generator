package com.dkhaohao.cli.pattern.command;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description: 调用者/请求者（invoker）
 * @date 2024/3/916:06
 */
public class RemoteControl {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton(){
        command.execute();
    }
}
