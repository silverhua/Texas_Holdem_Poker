package com.njks.demo1;

public class Card implements Comparable<Card>{
        private String color;
        private int num;
        private String x;
        public Card(){
        }
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

        public void setColor(String color) {
            this.color = color;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
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
