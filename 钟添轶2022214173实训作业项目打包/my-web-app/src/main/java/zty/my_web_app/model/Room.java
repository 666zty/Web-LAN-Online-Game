package zty.my_web_app.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    private String roomId;
    private List<WebSocketSession> sessions = new ArrayList<>();
    private List<Long> userids = new ArrayList<>();
    private gameInfo RoomGameInfo;

    public Room(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public List<WebSocketSession> getSessions() {
        return sessions;
    }

    public List<Long> getUserids(){
        return userids;
    }

    public void addUserid(Long userid){
        userids.add(userid);
    }

    public boolean addSession(WebSocketSession session) {
        if (sessions.size() < 2) {
            sessions.add(session);
            if (sessions.size() == 2){
                RoomGameInfo = new gameInfo();
            }
            return true;
        } else {
            return false;
        }
    }

    public void closeAllSessions() throws Exception {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.close();
            }
        }
    }
}

class gameInfo {
    private playerInfo playerInfo1 = new playerInfo();
    private playerInfo playerInfo2 = new playerInfo();
    private int round = 0;
    private int randomMax = 6;

    public gameInfo() {
        round++;
        playerInfo1.updatePlayerInfo(0,0, randomMax);
        playerInfo2.updatePlayerInfo(0,0, randomMax);
    }

    public playerInfo getPlayerInfo1() {
        return playerInfo1;
    }

    public playerInfo getPlayerInfo2() {
        return playerInfo2;
    }

    public ObjectNode endRound(ObjectNode msgFromPlayer1, ObjectNode msgFromPlayer2) {
        int player1HP = msgFromPlayer1.get("HP").asInt();
        int player1MP = msgFromPlayer1.get("MP").asInt();
        int player1Action = msgFromPlayer1.get("action").asInt();
        int player1ActionDamage = msgFromPlayer1.get("damage").asInt();
        boolean player1Skill1 = msgFromPlayer1.get("skill1").asBoolean();
        boolean player1Skill2 = msgFromPlayer1.get("skill2").asBoolean();
        boolean player1Skill3 = msgFromPlayer1.get("skill3").asBoolean();
        boolean player1Skill4 = msgFromPlayer1.get("skill4").asBoolean();
        boolean player1Surrender = msgFromPlayer1.get("surrender").asBoolean();
        int player2HP = msgFromPlayer2.get("HP").asInt();
        int player2MP = msgFromPlayer2.get("MP").asInt();
        int player2Action = msgFromPlayer2.get("action").asInt();
        int player2ActionDamage = msgFromPlayer2.get("damage").asInt();
        boolean player2Skill1 = msgFromPlayer2.get("skill1").asBoolean();
        boolean player2Skill2 = msgFromPlayer2.get("skill2").asBoolean();
        boolean player2Skill3 = msgFromPlayer2.get("skill3").asBoolean();
        boolean player2Skill4 = msgFromPlayer2.get("skill4").asBoolean();
        boolean player2Surrender = msgFromPlayer2.get("surrender").asBoolean();

        return msgFromPlayer1;
    }
}

class playerInfo {
    private int HP = 50;
    private int MP = 20;
    private int firstAttackNumber;
    private int secondAttackNumber;
    private int thirdAttackNumber;

    public int getHP() {
        return HP;
    }

    public int getMP() {
        return MP;
    }

    public int getFirstAttackNumber() {
        return firstAttackNumber;
    }

    public int getSecondAttackNumber() {
        return secondAttackNumber;
    }

    public int getThirdAttackNumber() {
        return thirdAttackNumber;
    }

    public boolean updatePlayerInfo(int changeHP, int changeMP, int randomMax) {
        Random rand = new Random();

        if ((this.HP -= changeHP) <= 0) {
            return true;
        }

        this.firstAttackNumber = rand.nextInt(randomMax + 1);
        this.secondAttackNumber = rand.nextInt(randomMax + 1);
        this.thirdAttackNumber = rand.nextInt(randomMax + 1);
        return false;
    }
}
