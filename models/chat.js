const db = require('./DB');
const sequelize = require('sequelize');
const chat = db.define('chat',{
Chid:{
    allowNull:false,
    type:sequelize.INTEGER,
    autoIncrement:true,
    primaryKey:true
},
idme:{
    type:sequelize.INTEGER,
    allowNull:false
},
idother:{
    type:sequelize.INTEGER,
    allowNull:false
},
message:{
    type:sequelize.STRING,
    allowNull:false,
}
});
exports.chat=chat;