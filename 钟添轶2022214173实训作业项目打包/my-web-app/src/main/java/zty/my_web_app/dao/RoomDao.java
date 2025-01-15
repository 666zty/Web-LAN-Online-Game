package zty.my_web_app.dao;

import org.springframework.stereotype.Repository;
import zty.my_web_app.model.Room;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class RoomDao {
    private ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();

    public String save(Room room) {
        rooms.put(room.getRoomId(), room);
        return room.getRoomId();
    }

    public Room findById(String roomId) {
        return rooms.get(roomId);
    }

    public void deleteById(String roomId) {
        rooms.remove(roomId);
    }

    public boolean existsById(String roomId) {
        return rooms.containsKey(roomId);
    }
    // 其他可能的操作，例如删除房间或获取所有房间列表
}







