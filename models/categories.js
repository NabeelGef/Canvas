mysql = require('mysql');
db = require('./DB');
connection = db.connection;
exports.category_model = ()=>{
    var createTable = `CREATE TABLE IF NOT EXISTS categories
    (Cid int(10) NOT NULL PRIMARY KEY AUTO_INCREMENT ,
     Cname varchar(50) NOT NULL)`;
     connection.query(createTable,(err,result)=>{
         if(err) throw err;
         console.log("Category Created!!");
     });
}