const db = require('./DB');
const sequelize = require('sequelize');
const user = db.define('users',{
  id:{
    type:sequelize.INTEGER,
    autoIncrement:true,
    allowNull:false,
    primaryKey:true
  },
  name:{
    type:sequelize.STRING,
    allowNull:false
  },
  email:{
    type:sequelize.STRING,
    unique:true,
    allowNull:false
  },
  password:{
    type:sequelize.TEXT,
    allowNull:false
  },
  address:{
    type:sequelize.STRING,
    allowNull:false
  },
  image : sequelize.TEXT,
  role:{
   type:sequelize.STRING,
   allowNull:false
  },
  token:sequelize.STRING,
  tokenMessage:sequelize.STRING,
  url:sequelize.TEXT
});
exports.user = user;