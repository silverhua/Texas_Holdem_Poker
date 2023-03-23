package com.njks.demo1;

import java.util.ArrayList;

public class Player implements Comparable<Player>{
    private int playerid;
    private int money;
    private ArrayList<Card> hand = new ArrayList<>();
    private int totalbet;
    private String status="default";
    public Player(int id,int money){
        this.playerid=id;
        this.money=money;
    }
    public void handcard(CardDeck deck){
        hand.add(deck.drawCard());
        hand.add(deck.drawCard());
    }
    public void clearhand(){
        hand.clear();
    }
    public void showhand(){
        hand.get(0).print();
        hand.get(1).print();
    }
    public void bet(int bet){
        money=money-bet;
        totalbet+=bet;
    }
    public void winpot(int pot){
        money=money+pot;
    }

    public int getMoney() {
        return money;
    }

    public int getPlayerid() {
        return playerid;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void resetStatus(){
        status="default";
    }

    public int getTotalbet() {
        return totalbet;
    }
    public void resettotalbet(){
        totalbet=0;
    }

    public void setTotalbet(int totalbet) {
        this.totalbet = totalbet;
    }
    public int getTotalMoney() {
        return money + totalbet;
    }
    public int compareTo(Player otherPlayer) {
        if (this.totalbet > otherPlayer.getTotalbet()) {
            return 1;
        } else if (this.totalbet < otherPlayer.getTotalbet()) {
            return -1;
        } else {
            return 0;
        }
    }
}
