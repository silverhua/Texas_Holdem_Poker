package com.njks.demo1;

import java.util.ArrayList;

public class Player implements Comparable<Player>{
    private final int playerId;
    private int money;
    private final ArrayList<Card> hand = new ArrayList<>();
    private int totalBet;
    private String status="default";
    public Player(int id,int money){
        this.playerId =id;
        this.money=money;
    }
    public void HandCard(CardDeck deck){
        hand.add(deck.drawCard());
        hand.add(deck.drawCard());
    }
    public void ClearHand(){
        hand.clear();
    }

    public void bet(int bet){
        money=money-bet;
        totalBet +=bet;
    }
    public void winPot(int pot){
        money=money+pot;
    }

    public int getMoney() {
        return money;
    }

    public int getPlayerId() {
        return playerId;
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

    public int getTotalBet() {
        return totalBet;
    }
    public void resetTotalBet(){
        totalBet =0;
    }

    public void setTotalBet(int totalBet) {
        this.totalBet = totalBet;
    }
    public int getTotalMoney() {
        return money + totalBet;
    }
    public int compareTo(Player otherPlayer) {
        if (this.totalBet > otherPlayer.getTotalBet()) {
            return 1;
        } else if (this.totalBet < otherPlayer.getTotalBet()) {
            return -1;
        } else {
            return 0;
        }
    }
}
