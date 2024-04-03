package com.acc.property;

import java.io.IOException;
import java.util.Scanner;

import com.acc.property.WebCrawler;
import com.acc.property.Input;

public class Main {

	public static void runWebCrawler() {
		WebCrawler.main(); // running the webcrawler, user should have the argument==0 if it wants to run
	}

	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("Shall we start with the implemention (y/n): ");
			String isStart = sc.nextLine();
			if (isStart.toLowerCase().equals("y")) {
				runWebCrawler();
				// add ids and remove empty objects from data
				PostProcessData.main(args);
				String fileName = "data.json";
				Input I = new Input();
				Input input = I.main(fileName);
				System.out.print(input);
			} else if (isStart.toLowerCase().equals("n")) {
				System.out.println("Program has been ended!");
			} else {
				System.out.println("Invalid Command!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
