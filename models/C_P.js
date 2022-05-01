mysql = require('mysql');
db = require('./DB');
connection = db.connection;
exports.C_P_model =()=>{
var createTable = `CREATE TABLE IF NOT EXISTS C_P(
    c_i_id int(10) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    Cid int(10) ,
    Pid int(10),
    FOREIGN KEY(Cid) REFERENCES categories(Cid),
    FOREIGN KEY(Pid) REFERENCES products(Pid))`;
    connection.query(createTable,(err,result)=>{
        if(err)throw err;
        console.log("C_P Created !!");
    });
}