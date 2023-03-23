package com.njks.demo1;

import java.util.*;

public class Game {
    private int bigblindpointer=1;
    private int smallblindpointer=0;
    private int smallblind;
    private int player;
    private CardDeck deck1 = new CardDeck();
    private int pot;
    private ArrayList<Player> playerlist= new ArrayList<>();
    private Scanner scan = new Scanner(System.in);
    private ArrayList<Player> playeringame = new ArrayList<>();
    private ArrayList<Card> communitycard = new ArrayList<>();
    private ArrayList<Player> winner=new ArrayList<>();
    private ArrayList<Player> winnerlist=new ArrayList<>();
    private boolean allIn = false;
    private int betnum=0;
    private int allincount=0;
    public Game(){
        System.out.println("请输入玩家数：");
        player=scan.nextInt();
        for(int i=1;i<=player;i++){
            System.out.println("请输入玩家"+i+"的初始金钱:");
            playerlist.add(new Player(i,scan.nextInt()));
        }
        System.out.println("请输入小盲：");
        smallblind= scan.nextInt();
        System.out.println("游戏开始");
    }
    public void play(){
        while(playerlist.size()>1){
            //清空上局数据
            deck1.reset();
            playeringame.clear();
            resetPlayer(playerlist);
            communitycard.clear();
            winnerlist.clear();
            betnum=smallblind*2;
            allincount=0;
            //发牌
            deliverCard();
            playeringame.addAll(playerlist);
            //下大小盲
            blind();
            //初始下注
            if(!allIn){
                betsession(playeringame);
            }
            if(!allIn){
                if(playeringame.size()!=1) {
                    //发三张公共牌
                    communitycard.add(deck1.drawCard());
                    communitycard.add(deck1.drawCard());
                    communitycard.add(deck1.drawCard());
                    betsession(playeringame);
                }else{
                    System.out.println("玩家"+playeringame.get(0).getPlayerid()+"赢得比赛");
                    playeringame.get(0).winpot(pot);
                    //判断是否有人淘汰
                    haveEnoughChips();
                    //下一个人小盲
                    smallblindpointer++;
                    if (smallblindpointer>=playeringame.size()){
                        smallblindpointer=0;
                    }
                    bigblindpointer=smallblindpointer+1;
                    if (bigblindpointer>=playeringame.size()){
                        bigblindpointer=0;
                    }
                    continue;
                }
            }
            if (!allIn){
                if(playeringame.size()!=1) {
                    //转牌下注
                    communitycard.add(deck1.drawCard());
                    betsession(playeringame);
                }else{
                    System.out.println("玩家"+playeringame.get(0).getPlayerid()+"赢得比赛");
                    playeringame.get(0).winpot(pot);
                    //判断是否有人淘汰
                    haveEnoughChips();
                    //下一个人小盲
                    smallblindpointer++;
                    if (smallblindpointer>=playeringame.size()){
                        smallblindpointer=0;
                    }
                    bigblindpointer=smallblindpointer+1;
                    if (bigblindpointer>=playeringame.size()){
                        bigblindpointer=0;
                    }
                    continue;
                }
            }
            if(!allIn){
                if(playeringame.size()!=1) {
                    //河牌下注
                    communitycard.add(deck1.drawCard());
                    betsession(playeringame);
                }else{
                    System.out.println("玩家"+playeringame.get(0).getPlayerid()+"赢得比赛");
                    playeringame.get(0).winpot(pot);
                    //判断是否有人淘汰
                    haveEnoughChips();
                    //下一个人小盲
                    smallblindpointer++;
                    if (smallblindpointer>=playeringame.size()){
                        smallblindpointer=0;
                    }
                    bigblindpointer=smallblindpointer+1;
                    if (bigblindpointer>=playeringame.size()){
                        bigblindpointer=0;
                    }
                    continue;
                }
            }
            //补齐5张公共牌
            while(communitycard.size()<5){
                communitycard.add(deck1.drawCard());
            }
            //显示所有公共牌
            System.out.println("公共牌为：");
            for(Card x:communitycard){
                x.print();
            }

            //分钱
            potdeliver(playeringame,communitycard);
            //判断是否有人淘汰
            haveEnoughChips();
            //下一个人小盲
            smallblindpointer++;
            if (smallblindpointer>=playeringame.size()){
                smallblindpointer=0;
            }
            bigblindpointer=smallblindpointer+1;
            if (bigblindpointer>=playeringame.size()){
                bigblindpointer=0;
            }

        }
        System.out.println("玩家"+playerlist.get(0).getPlayerid()+"是最终赢家");
        System.out.println("他的最终筹码是："+playerlist.get(0).getMoney());
    }

    private void potdeliver(ArrayList<Player> playeringame,ArrayList<Card> communitycard){
        winnerlist.addAll(findWinner(playeringame,communitycard));
        for(Player x:winnerlist) {
            System.out.println("玩家"+x.getPlayerid()+"获得胜利");
        }
        System.out.println("底池为："+pot);

        // distribute the pot
        Map<Player, Integer> payouts = distributePot(winnerlist, communitycard, pot);
        for (Player winner : winnerlist) {
            int amountWon = payouts.get(winner);
            winner.winpot(amountWon);
            pot -= amountWon;
            System.out.println("玩家"+winner.getPlayerid()+"赢得"+amountWon);
        }
        System.out.println("一轮结束");
    }


    public static Map<Player, Integer> distributePot(ArrayList<Player> players, ArrayList<Card> communityCards, int pot) {
        // evaluate the strength of each player's hand
        Map<Player, Integer> playerRanks = new HashMap<>();
        for (Player player : players) {
            int rank = evaluateHand(player.getHand(), communityCards);
            playerRanks.put(player, rank);
        }

        // determine the highest rank among the players
        int highestRank = 1;
        for (int rank : playerRanks.values()) {
            if (rank > highestRank) {
                highestRank = rank;
            }
        }

        // find the players with the highest rank
        List<Player> winners = new ArrayList<>();
        for (Player player : playerRanks.keySet()) {
            if (playerRanks.get(player) == highestRank) {
                winners.add(player);
            }
        }

        // divide the pot among the winners
        int numWinners = winners.size();
        int share = pot / numWinners;
        int remainder = pot % numWinners;

        // determine which winners are all-in and which ones can call full share
        List<Player> allInWinners = new ArrayList<>();
        List<Player> normalWinners = new ArrayList<>();
        for (Player winner : winners) {
            if (winner.getTotalMoney() >= share) {
                normalWinners.add(winner);
            } else {
                allInWinners.add(winner);
            }
        }

        // distribute the pot among the normal winners first
        int normalShare = share;
        int normalRemainder = remainder;
        Map<Player, Integer> payouts = new HashMap<>();
        if (!normalWinners.isEmpty()) {
            normalShare = pot / normalWinners.size();
            normalRemainder = pot % normalWinners.size();
            for (int i = 0; i < normalWinners.size(); i++) {
                Player winner = normalWinners.get(i);
                winner.setTotalbet(winner.getTotalbet() + normalShare);
                payouts.put(winner, normalShare);
                if (i < normalRemainder) {
                    winner.setTotalbet(winner.getTotalbet() + 1);
                    payouts.put(winner, payouts.get(winner) + 1);
                }
            }
        }

        // distribute the remainder of the pot among the all-in winners
        int allInShare = 0;
        int allInRemainder = 0;
        if (!allInWinners.isEmpty()) {
            int totalAllInBet = 0;
            for (Player winner : allInWinners) {
                winner.setTotalbet(winner.getTotalbet() + winner.getTotalMoney());
                payouts.put(winner, winner.getTotalMoney());
                totalAllInBet += winner.getTotalMoney();
            }
            allInShare = (pot - totalAllInBet) / allInWinners.size();
            allInRemainder = (pot - totalAllInBet) % allInWinners.size();
            for (int i = 0; i < allInWinners.size(); i++) {
                Player winner = allInWinners.get(i);
                if (i < allInRemainder) {
                    winner.setTotalbet(winner.getTotalbet() + allInShare + 1);
                    payouts.put(winner, payouts.get(winner) + allInShare + 1);
                } else {
                    winner.setTotalbet(winner.getTotalbet() + allInShare);
                    payouts.put(winner, payouts.get(winner) + allInShare);
                }
            }
        }
        // return the payouts to the winners
        return payouts;
    }
    private void blind(){
        playeringame.get(smallblindpointer).bet(smallblind);
        playeringame.get(bigblindpointer).bet(smallblind*2);
        if (playeringame.get(bigblindpointer).getMoney()==0){
            playeringame.get(bigblindpointer).setStatus("ALLIN");
            allincount++;
        }
        pot+=smallblind*3;
    }


    private void resetPlayer(ArrayList<Player> playerlist) {
        for (Player x :playerlist){
            x.resetStatus();
            x.resettotalbet();
            x.clearhand();
        }
    }


    private void betsession(ArrayList<Player> playerlist) {
        //显示公共牌
        System.out.println("公共牌为：");
        for(Card x:communitycard){
            x.print();
        }
        //从小盲开始轮流下注
        int count=0;
        for (int i =smallblindpointer;
             true;
             i++
        ){

            if(i>=playerlist.size()){
                i=0;
            }
            //判断是否allin了

            count++;
            //判断不在ALLIN状态
            if (playerlist.get(i).getStatus()!="ALLIN") {
                //显示手牌
                System.out.println("玩家" + playerlist.get(i).getPlayerid() + "你的手牌是：");
                playerlist.get(i).getHand().get(0).print();
                playerlist.get(i).getHand().get(1).print();
                //显示筹码
                System.out.println( "你的剩余筹码为：" + playerlist.get(i).getMoney());
                //下注并修改状态
                if(betnum>playerlist.get(i).getTotalbet()){
                    if(playerlist.get(i).getMoney()+playerlist.get(i).getTotalbet()<=betnum){
                        while(true){
                            System.out.println("请选择你的行动（allin或者fold）");
                            String action = scan.next();
                            if(action.equals("allin")){
                                playerlist.get(i).setStatus("ALLIN");
                                pot += playerlist.get(i).getMoney();
                                playerlist.get(i).bet(playerlist.get(i).getMoney());
                                allincount++;
                                break;
                            }else if(action.equals("fold")){
                                playerlist.get(i).setStatus("FOLD");
                                playeringame.remove(playerlist.get(i));
                                count--;
                                i--;
                                break;
                            }
                            System.out.println("输入错误，请重新输入");
                        }
                    }else {
                        while (true) {
                            System.out.println("请选择你的行动（bet，allin或者fold）");
                            String action = scan.next();
                            if (action.equals("bet")) {
                                while (true) {
                                    System.out.println("请下注：");
                                    int playerbet = scan.nextInt();
                                    if (playerbet <= playerlist.get(i).getMoney()&&playerbet+playerlist.get(i).getTotalbet()>=betnum) {
                                        playerlist.get(i).bet(playerbet);
                                        pot += playerbet;
                                        if (playerlist.get(i).getMoney() == playerbet) {
                                            playerlist.get(i).setStatus("ALLIN");
                                            allincount++;
                                        }
                                        break;
                                    }
                                    System.out.println("下注不合法，请重新下注");
                                }
                                break;
                            } else if (action.equals("allin")) {
                                playerlist.get(i).setStatus("ALLIN");
                                pot += playerlist.get(i).getMoney();
                                playerlist.get(i).bet(playerlist.get(i).getMoney());
                                allincount++;
                                break;
                            } else if (action.equals("fold")) {
                                playerlist.get(i).setStatus("FOLD");
                                playeringame.remove(playerlist.get(i));
                                count--;
                                i--;
                                break;
                            }
                            System.out.println("输入错误，请重新输入");
                        }
                    }
                } else{
                    while(true) {
                        System.out.println("请选择你的行动（hold,bet，allin或者fold）");
                        String action = scan.next();
                        if (action.equals("bet")) {
                            while(true) {
                                System.out.println("请下注：");
                                int playerbet = scan.nextInt();
                                if (playerbet <= playerlist.get(i).getMoney()&&playerbet+playerlist.get(i).getTotalbet()>=betnum){
                                    playerlist.get(i).bet(playerbet);
                                    pot+=playerbet;
                                    break;
                                }
                                System.out.println("下注不合法，请重新下注");
                            }
                            break;
                        } else if (action.equals("allin")) {
                            playerlist.get(i).setStatus("ALLIN");
                            pot+=playerlist.get(i).getMoney();
                            playerlist.get(i).bet(playerlist.get(i).getMoney());
                            break;
                        } else if (action.equals("fold")) {
                            playerlist.get(i).setStatus("FOLD");
                            playeringame.remove(playerlist.get(i));
                            count--;
                            i--;
                            break;
                        } else if (action.equals("hold")){
                            break;
                        }
                        System.out.println("输入错误，请重新输入");
                    }
                }
                if(betnum<playerlist.get(i).getTotalbet()){
                    betnum=playerlist.get(i).getTotalbet();
                }
                int n=i+1;
                if(n>=playerlist.size()){
                    n=0;
                }
                if(allincount>=playerlist.size()-1&&playerlist.get(n).getStatus().equals("ALLIN")){
                    allIn=true;
                    break;
                }
                //一轮以后 判断是否要继续下注
                if(count>=playerlist.size()){
                    if(i+1>=playerlist.size()){
                        if(playerlist.get(i).getTotalbet()==playerlist.get(0).getTotalbet()){
                            break;
                        }
                    } else {
                        if(playerlist.get(i).getTotalbet()==playerlist.get(i+1).getTotalbet()){
                            break;
                        }
                    }
                }
            }
        }

    }
    private void deliverCard(){
        for(Player x:playerlist){
            x.handcard(deck1);
        }
    }
    private void haveEnoughChips(){
        for (int i=0;i<playerlist.size();i++) {
            if(playerlist.get(i).getMoney()<smallblind*2){
                System.out.println("玩家"+playerlist.get(i).getPlayerid()+"的筹码不足，被淘汰");
                playerlist.remove(i);
                i--;
            }
        }
    }
    private ArrayList<Player> findWinner(ArrayList<Player> playerlist,ArrayList<Card> communityCards){
        ArrayList<Player> winnerList=new ArrayList<>();
        winnerList.add(playerlist.get(0));
        for(int i=1;i<playerlist.size();i++){
            if (compareHands(playerlist.get(i).getHand(),winnerList.get(0).getHand(),communityCards)==1){
                winnerList.clear();
                winnerList.add(playerlist.get(i));
            } else if(compareHands(playerlist.get(i).getHand(),winnerList.get(0).getHand(),communityCards)==-1){
            } else {
                winnerList.add(playerlist.get(i));
            }
        }
        return winnerList;
    }
    public static int compareHands(ArrayList<Card> player1Hand, ArrayList<Card> player2Hand, ArrayList<Card> communityCards) {
        int player1Rank = evaluateHand(player1Hand, communityCards);
        int player2Rank = evaluateHand(player2Hand, communityCards);

        if (player1Rank > player2Rank) {
            return 1;
        } else if (player1Rank < player2Rank) {
            return -1;
        } else {
            ArrayList<Card> player1Combined = new ArrayList<>(player1Hand);
            player1Combined.addAll(communityCards);
            Collections.sort(player1Combined, Collections.reverseOrder());

            ArrayList<Card> player2Combined = new ArrayList<>(player2Hand);
            player2Combined.addAll(communityCards);
            Collections.sort(player2Combined, Collections.reverseOrder());

            switch (player1Rank) {
                case 10: // Royal Flush
                    return 0; // Royal Flushes will always be a tie
                case 9: // Straight Flush
                    return compareStraightFlushes(player1Combined, player2Combined);
                case 8: // Four of a Kind
                    return compareFourOfAKind(player1Combined, player2Combined);
                case 7: // Full House
                    return compareFullHouses(player1Combined, player2Combined);
                case 6: // Flush
                    return compareFlushes(player1Combined, player2Combined);
                case 5: // Straight
                    return compareStraights(player1Combined, player2Combined);
                case 4: // Three of a Kind
                    return compareThreeOfAKind(player1Combined, player2Combined);
                case 3: // Two Pair
                    return compareTwoPairs(player1Combined, player2Combined);
                case 2: // One Pair
                    return comparePairs(player1Combined, player2Combined);
                case 1: // High Card
                    return compareHighCard(player1Combined, player2Combined);
                default:
                    throw new IllegalStateException("Unexpected hand rank: " + player1Rank);
            }
        }
    }

    private static int compareHighCard(ArrayList<Card> player1, ArrayList<Card> player2) {
        for (int i = 0; i < player1.size(); i++) {
            if (player1.get(i).getNum() > player2.get(i).getNum()) {
                return 1;
            } else if (player1.get(i).getNum() < player2.get(i).getNum()) {
                return -1;
            }
        }
        return 0;
    }

    private static int comparePairs(ArrayList<Card> player1, ArrayList<Card> player2) {
        Card pair1 = getPair(player1);
        Card pair2 = getPair(player2);

        if (pair1.getNum() > pair2.getNum()) {
            return 1;
        } else if (pair1.getNum() < pair2.getNum()) {
            return -1;
        } else {
            ArrayList<Card> kickers1 = removePair(player1, pair1);
            ArrayList<Card> kickers2 = removePair(player2, pair2);
            return compareHighCard(kickers1, kickers2);
        }
    }
    private static Card getPair(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getNum() == cards.get(i + 1).getNum()) {
                return cards.get(i);
            }
        }
        return null;
    }
    private static ArrayList<Card> removePair(ArrayList<Card> cards, Card pair) {
        ArrayList<Card> newCards = new ArrayList<>(cards);
        newCards.remove(pair);
        return newCards;
    }

    private static int compareTwoPairs(ArrayList<Card> player1, ArrayList<Card> player2) {
        List<Card> pairs1 = getTwoPairs(player1);
        List<Card> pairs2 = getTwoPairs(player2);
        for (int i = 0; i < pairs1.size(); i++) {
            if (pairs1.get(i).getNum() > pairs2.get(i).getNum()) {
                return 1;
            } else if (pairs1.get(i).getNum() < pairs2.get(i).getNum()) {
                return -1;
            }
        }

        ArrayList<Card> kickers1 = removeTwoPairs(player1, pairs1);
        ArrayList<Card> kickers2 = removeTwoPairs(player2, pairs2);
        return compareHighCard(kickers1, kickers2);
    }
    private static List<Card> getTwoPairs(ArrayList<Card> cards) {
        List<Card> pairs = new ArrayList<>();
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getNum() == cards.get(i + 1).getNum()) {
                pairs.add(cards.get(i));
                i++;
            }
        }
        return pairs;
    }
    private static ArrayList<Card> removeTwoPairs(ArrayList<Card> cards, List<Card> pairs) {
        ArrayList<Card> newCards = new ArrayList<>(cards);
        for (Card pair : pairs) {
            newCards.remove(pair);
            newCards.remove(pair);
        }
        return newCards;
    }
    private static int compareThreeOfAKind(ArrayList<Card> player1, ArrayList<Card> player2) {
        Card three1 = getThreeOfAKind(player1);
        Card three2 = getThreeOfAKind(player2);
        if (three1.getNum() > three2.getNum()) {
            return 1;
        } else if (three1.getNum() < three2.getNum()) {
            return -1;
        }
        return 0;
    }
    private static Card getThreeOfAKind(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 2; i++) {
            if (cards.get(i).getNum() == cards.get(i + 1).getNum() &&
                    cards.get(i + 1).getNum() == cards.get(i + 2).getNum()) {
                return cards.get(i);
            }
        }
        return null;
    }
    private static int compareStraights(ArrayList<Card> player1, ArrayList<Card> player2) {
        Card high1 = getStraightHighCard(player1);
        Card high2 = getStraightHighCard(player2);
        if (high1.getNum() > high2.getNum()) {
            return 1;
        } else if (high1.getNum() < high2.getNum()) {
            return -1;
        }
        return 0;
    }
    private static Card getStraightHighCard(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 4; i++) {
            if (cards.get(i).getNum() == cards.get(i + 1).getNum() + 1 &&
                    cards.get(i + 1).getNum() == cards.get(i + 2).getNum() + 1 &&
                    cards.get(i + 2).getNum() == cards.get(i + 3).getNum() + 1 &&
                    cards.get(i + 3).getNum() == cards.get(i + 4).getNum() + 1) {
                return cards.get(i);
            }
        }
        return null;
    }
    private static int compareFlushes(ArrayList<Card> player1, ArrayList<Card> player2) {
        return compareHighCard(player1, player2);
    }
    private static int compareFullHouses(ArrayList<Card> player1, ArrayList<Card> player2) {
        Card three1 = getThreeOfAKind(player1);
        Card three2 = getThreeOfAKind(player2);
        Card pair1 = getPair(removeThreeOfAKind(player1, three1));
        Card pair2 = getPair(removeThreeOfAKind(player2, three2));
        if (three1.getNum() > three2.getNum()) {
            return 1;
        } else if (three1.getNum() < three2.getNum()) {
            return -1;
        } else if (pair1.getNum() > pair2.getNum()) {
            return 1;
        } else if (pair1.getNum() < pair2.getNum()) {
            return -1;
        }
        return 0;
    }
    private static ArrayList<Card> removeThreeOfAKind(ArrayList<Card> cards, Card three) {
        ArrayList<Card> newCards = new ArrayList<>(cards);
        for (int i = 0; i < 3; i++) {
            newCards.remove(three);
        }
        return newCards;
    }
    private static int compareFourOfAKind(ArrayList<Card> player1, ArrayList<Card> player2) {
        Card four1 = getFourOfAKind(player1);
        Card four2 = getFourOfAKind(player2);
        if (four1.getNum() > four2.getNum()) {
            return 1;
        } else if (four1.getNum() < four2.getNum()) {
            return -1;
        }
        return 0;
    }
    private static Card getFourOfAKind(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 3; i++) {
            if (cards.get(i).getNum() == cards.get(i + 1).getNum() &&
                    cards.get(i + 1).getNum() == cards.get(i + 2).getNum() &&
                    cards.get(i + 2).getNum() == cards.get(i + 3).getNum()) {
                return cards.get(i);
            }
        }
        return null;
    }
    private static int compareStraightFlushes(ArrayList<Card> player1, ArrayList<Card> player2) {
        Card high1 = getStraightHighCard(player1);
        Card high2 = getStraightHighCard(player2);
        if (high1.getNum() > high2.getNum()) {
            return 1;
        } else if (high1.getNum() < high2.getNum()) {
            return -1;
        }
        return 0;
    }
    private static int evaluateHand(ArrayList<Card> hand, ArrayList<Card> communityCards) {
        ArrayList<Card> allCards = new ArrayList<>();
        allCards.addAll(hand);
        allCards.addAll(communityCards);

        // sort the cards in descending order by rank
        Collections.sort(allCards, Collections.reverseOrder());

        // check for the different hand combinations in descending order of strength
        if (isRoyalFlush(allCards)) {
            return 10;
        } else if (isStraightFlush(allCards)) {
            return 9;
        } else if (isFourOfAKind(allCards)) {
            return 8;
        } else if (isFullHouse(allCards)) {
            return 7;
        } else if (isFlush(allCards)) {
            return 6;
        } else if (isStraight(allCards)) {
            return 5;
        } else if (isThreeOfAKind(allCards)) {
            return 4;
        } else if (isTwoPair(allCards)) {
            return 3;
        } else if (isOnePair(allCards)) {
            return 2;
        } else {
            return 1;
        }

    }
    private static boolean isRoyalFlush(ArrayList<Card> cards) {
        return isStraightFlush(cards) && cards.get(0).getNum() == 14;
    }

    private static boolean isStraightFlush(ArrayList<Card> cards) {
        return isFlush(cards) && isStraight(cards);
    }

    private static boolean isFourOfAKind(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 3; i++) {
            if (cards.get(i).getNum() == cards.get(i+1).getNum() &&
                    cards.get(i+1).getNum() == cards.get(i+2).getNum() &&
                    cards.get(i+2).getNum() == cards.get(i+3).getNum()) {
                return true;
            }
        }
        return false;
    }
    private static boolean isFullHouse(ArrayList<Card> cards) {
        return isThreeOfAKind(cards) && isOnePair(cards);
    }
    private static boolean isFlush(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getColor() != cards.get(i + 1).getColor())
                return false;
        }
    return true;
    }
    private static boolean isStraight(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getNum() != cards.get(i+1).getNum() + 1) {
                return false;
            }
        }
        return true;
    }
    private static boolean isThreeOfAKind(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 2; i++) {
            if (cards.get(i).getNum() == cards.get(i+1).getNum() &&
                    cards.get(i+1).getNum() == cards.get(i+2).getNum()) {
                return true;
            }
        }
        return false;
    }
    private static boolean isTwoPair(ArrayList<Card> cards) {
        int countPairs = 0;
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getNum() == cards.get(i+1).getNum()) {
                countPairs++;
                i++;
            }
        }
        return countPairs == 2;
    }

    public static boolean isOnePair(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getNum() == cards.get(i+1).getNum()) {
                return true;
            }
        }
        return false;
    }
}
