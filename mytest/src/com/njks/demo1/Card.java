package com.njks.demo1;

public class Card implements Comparable<Card>{
        private final String color;
        private final int num;
        private final String x;
        public Card(String n,int m){
            color =n;
            num=m;
            if (num==1){
                x="A";
            }else if(num==11){
                x="J";
            }else if(num==12){
                x="Q";
            }else if(num==13){
                x="K";
            }else{
                x=Integer.toString(num);
            }
        }

        public String getColor() {
            return color;
        }

        public int getNum() {
            return num;
        }

        public void print(){
            System.out.println(color+x);
        }
        public int compareTo(Card otherCard) {
            if (this.num > otherCard.getNum()) {
                return 1;
            } else if (this.num < otherCard.getNum()) {
                return -1;
            } else {
                return 0;
            }
        }
}
