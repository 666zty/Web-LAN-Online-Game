// websocket.js
var socket;
var roomId; // 用于存储 RoomID
var userid;

function initWebSocket() {
    fetch('http://localhost:8443/users/userInfo', {
        method: 'GET',
        credentials: 'include' // 确保携带 Cookie
    })
        .then(response => response.json())
        .then(data => {
            userid = data.id;
        })
        .catch(error => {
            console.error('Error:', error);
        });
    socket = new WebSocket("ws://localhost:8443/game");

    socket.onopen = function () {
        console.log("WebSocket connection established.");
    };

    socket.onmessage = function (event) {
        var message = JSON.parse(event.data);
        handleMessage(message);
    };

    socket.onerror = function (error) {
        console.log("WebSocket error: " + error.message);
    };

    socket.onclose = function () {
        console.log("WebSocket connection closed.");
    };
}

function handleMessage(message) {
    console.log("Received message: ", message);
    if (message.action === "createRoom") {
        roomId = message.roomId;
        console.log("Room ID stored: " + roomId);
        document.getElementById('RoomIDShow').innerText = "等待用户连接...房间已创建，ID: " + roomId;
        $('#alertModal1').modal('show');
    } else if (message.action === "joinRoom") {
        console.log("Joined room: " + message.roomId);
    } else if (message.error) {
        console.error("Error: " + message.error);
    } else if (message.action === "startGame") {
        console.log("Game started.");
        window.location.href = "gamePage/game.html";
    }
    else {
        console.log("Unknown message action: " + message.action);
    }
}

function createRoom() {
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify({ action: "createRoom" , userId: userid}));
    } else {
        console.log("WebSocket is not open. Call initWebSocket() first.");
    }
}

function joinRoom(roomId) {
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify({ action: "joinRoom", roomId: roomId , userId: userid}));
    } else {
        console.log("WebSocket is not open. Call initWebSocket() first.");
    }
}


