const db = require('./DB');
const sequelize = require('sequelize');
console.log('ROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL')
const role = db.define('roles',{
  role_id:{
    type:sequelize.INTEGER,
    allowNull:false,
    autoIncrement:true,
    primaryKey:true
  },
  name_role:{
    type:sequelize.STRING,
    allowNull:false
  }
});
// role.create({s
//   name_role:'admin'
// }).then((result,err)=>{
//   if(err) throw err;
//   console.log(`result = ${result}`);
// });
exports.role = role;