package com.njks.demo1;

import java.util.Scanner;

public class Poker {
    public static void main(String[] args) {
        loop:while(true) {
            System.out.println("新的一局开始！");
            Game game = new Game();
            game.play();
            Scanner sc = new Scanner(System.in);
            loop2:while(true) {
                System.out.println("是否进行下一局（Y/N)");
                switch (sc.next()) {
                    case "Y":
                        break loop2;
                    case "N":
                        break loop;
                    default:
                        System.out.println("输入错误请重新输入");
                }
            }
        }
    }
}

