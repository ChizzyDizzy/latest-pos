package com.syos.cli.commands;

import com.syos.application.interfaces.Command;

public class NullCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Invalid command. Please try again.");
    }

    @Override
    public String getDescription() {
        return "Invalid Command";
    }
}