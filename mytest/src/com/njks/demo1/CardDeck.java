package com.njks.demo1;

import java.util.ArrayList;
import java.util.Random;

public class CardDeck {
        private final ArrayList<Card> Deck = new ArrayList<>();
        public CardDeck() {
            for(int i = 1;i <= 13;i++){
                Deck.add(new Card("红桃",i));
                Deck.add(new Card("黑桃",i));
                Deck.add(new Card("梅花",i));
                Deck.add(new Card("方片",i));
            }
        }
        public Card drawCard(){
            Random random=new Random();
            Card result = Deck.get(random.nextInt(Deck.size()));
            Deck.remove(result);
            return result;
        }

        public void reset(){
            Deck.clear();
            for(int i = 1;i <= 13;i++){
                Deck.add(new Card("红桃",i));
                Deck.add(new Card("黑桃",i));
                Deck.add(new Card("梅花",i));
                Deck.add(new Card("方片",i));
            }
        }

}
