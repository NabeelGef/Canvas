const express = require('express');
const user_model = require('./models/user');
const roles_model = require('./models/roles');
const role_user = require('./models/role_user');
const chat_model = require('./models/chat');
const path = require('path');
const http = require('http');
const db = require('./models/DB');
db.sync();
const app = express();
app.use(express.json());
 app.use('/',require('./routes/auth'));
 app.use(express.static(path.join(__dirname, 'public')));
var admin = require("firebase-admin");
var FCM = require('fcm-node');
    var serverKey = 'AAAAw8AcH_o:APA91bHfW67oo_ocAund_15hJTqUO2V0vls-u3XxZ3kex09GpXUNMDVcQKmOVthUJLMtlPwfuJ1RdZZpPQ33ImbQZ9eZLMI2tnpqErh9WSS3SLTvNxGOArqzqys6pzDz3QV6BWqnKDoS ';
    var fcm = new FCM(serverKey);

const server = http.createServer(app);
const io = require('socket.io')(server);
io.on('connection',function(socket){
    console.log('Connected');
    socket.on('sendmessage', (myImage,toToken,to,messageContent) => {
        console.log(messageContent)       
       let  message = {
                        "message":messageContent,
                        "id":to
                    }
var message2 = {
  to:toToken,
  notification: {
      title: 'message',
      body: messageContent,
      image: myImage
  },
};
fcm.send(message2, function(err, response) {
  if (err) {
      console.log("Something has gone wrong!"+err);
      console.log("Respponse:! "+response);
  } else {
      console.log("Successfully sent with response: ", response);
  }
});                    
   socket.broadcast.emit('message', message );
   
       });   
});

    


//Send a message to devices subscribed to the provided topic.










server.listen(3000,()=>{
    console.log('Server Is connected In Port 3000');
});
