package zty.my_web_app.controller;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import zty.my_web_app.model.Room;
import zty.my_web_app.service.RoomService;

@Component
public class WebSocketController extends TextWebSocketHandler {

    private final RoomService roomService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ObjectNode msg = objectMapper.readValue(payload, ObjectNode.class);
        String action = msg.get("action").asText();
        String roomId;
        Long userid;

        switch (action) {
            case "createRoom":
                userid = msg.get("userId").asLong();
                roomId = roomService.createRoom(session, userid);
                ObjectNode createResponse = objectMapper.createObjectNode();
                createResponse.put("action", "createRoom");
                createResponse.put("roomId", roomId);
                session.sendMessage(new TextMessage(createResponse.toString()));
                break;
            case "joinRoom":
                roomId = msg.get("roomId").asText();
                userid = msg.get("userId").asLong();
                if (roomService.joinRoom(roomId, session, userid)) {
                    ObjectNode joinResponse = objectMapper.createObjectNode();
                    joinResponse.put("action", "joinRoom");
                    joinResponse.put("roomId", roomId);
                    session.sendMessage(new TextMessage(joinResponse.toString()));

                    // 检查房间中的会话数量
                    Room room = roomService.findById(roomId);
                    if (room != null && room.getSessions().size() == 2) {
                        ObjectNode startGameResponse = objectMapper.createObjectNode();
                        startGameResponse.put("action", "startGame");
                        TextMessage startGameMessage = new TextMessage(startGameResponse.toString());
                        for (WebSocketSession webSocketSession : room.getSessions()) {
                            webSocketSession.sendMessage(startGameMessage);
                        }
                    }
                } else {
                    session.sendMessage(new TextMessage("{\"action\":\"joinRoom\",\"error\":\"Room is full or not found\"}"));
                }
                break;
            case "deleteRoom":
                roomId = msg.get("roomId").asText();
                Room room = roomService.findById(roomId);
                if (room != null) {
                    ObjectNode deleteResponse = objectMapper.createObjectNode();
                    deleteResponse.put("action", "deleteRoom");
                    deleteResponse.put("roomId", roomId);
                    session.sendMessage(new TextMessage(deleteResponse.toString())); // 提前发送消息
                    room.closeAllSessions(); // 关闭所有与房间关联的会话
                    roomService.deleteRoom(roomId); // 从列表中删除房间
                } else {
                    session.sendMessage(new TextMessage("{\"action\":\"deleteRoom\",\"error\":\"Room not found\"}"));
                }
                break;
            default:
                session.sendMessage(new TextMessage("{\"action\":\"unknown\",\"error\":\"Unknown action: " + action + "\"}"));
                break;
        }
    }
}
