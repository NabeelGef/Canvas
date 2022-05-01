const db = require('./DB');
const sequelize = require('sequelize');
console.log('ROOOOOOOOOOOOOOOOUSERUSERUSRERUSEROOOOOOOOL')
const roleUser = db.define('rolesusers',{
role_id:{
  type:sequelize.INTEGER,
  // references:{
  //   model:'roles',
  //   key:'role_id',
  // },
   allowNull:false
},
user_id:{
  type:sequelize.INTEGER,
  allowNull:false
  // references:{
  // model:'users',
  // key:'id',
  // }  
}
});
exports.roleUser = roleUser;