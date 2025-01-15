package zty.my_web_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import zty.my_web_app.dao.RoomDao;
import zty.my_web_app.model.Room;

@Service
public class RoomService {

    @Autowired
    private RoomDao roomDao;

    public String createRoom(WebSocketSession session, Long userid) {
        String roomId;
        do {
            roomId = generateRoomId();
        } while (roomDao.existsById(roomId));  // 确保房间ID是唯一的
        roomDao.save(new Room(roomId));
        Room room = roomDao.findById(roomId);
        room.addSession(session);
        room.addUserid(userid);
        return roomId;
    }

    public boolean joinRoom(String roomId, WebSocketSession session, Long userid) {
        Room room = roomDao.findById(roomId);
        if (room != null && room.addSession(session)) {
            room.addUserid(userid);
            return true;
        } else {
            return false;
        }
    }

    public Room findById(String roomId) {
        return roomDao.findById(roomId);
    }

    public void deleteRoom(String roomId) {
        roomDao.deleteById(roomId);
    }

    private String generateRoomId() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}

