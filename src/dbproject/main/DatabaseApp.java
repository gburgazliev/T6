package dbproject.main;

import dbproject.controller.CommandProcessor;
public class DatabaseApp {
    public static void main(String[] args) {
        CommandProcessor processor = new CommandProcessor();
        processor.run();
    }
}