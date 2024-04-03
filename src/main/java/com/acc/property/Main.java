package com.acc.property;

import java.io.IOException;

public class Main {

    public static void runWebCrawler(String[] args) {
        if (args.length != 1) {
            System.err.println("Incorrect number of arguments");
            return;
        }
        int option;
        try {
            option = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Argument must be an integer");
            return;
        }

        if (option != 0) {
            WebCrawler.main(args); // running the webcrawler, user should have the argument==1 if it wants to run
                                   // with
                                   // webcrawler
        }
    }

    public static void main(String[] args) {
       runWebCrawler(args);

        try {
            PostProcessData.main(args); // add ids and remove empty objects from data
            String fileName = "data.json";
			Input I = new Input();
			Input input = I.main(fileName);
			System.out.print(input);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}