package br.com.letscode.screens;

import java.util.Scanner;

import br.com.letscode.model.system.Navigation;

public class SessionScreen implements ScreenInterface {
    public Navigation run(Scanner scanner, String[] args) {
        System.out.println("SessionScreen");
        Navigation navigate = new Navigation();
        navigate.setScreen(ScreensList.EXIT);
        return navigate;
    }
}
