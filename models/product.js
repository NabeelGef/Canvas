
mysql = require('mysql');
db = require('./DB');
connection = db.connection;
exports.product_model = function(){
    var createTable =`CREATE Table IF NOT EXISTS products
    (Pid int(10) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id int(10) NOT NULL,
    Cid int(10) NOT NULL,
    Pname varchar(50) NOT NULL,
    photo varchar(255),
    info_connect varchar(255) NOT NULL,
    quantity int(10) NOT NULL,
    price DOUBLE(10,2) NOT NULL,
    exp_date DATE , 
    CONSTRAINT FOREIGN KEY(user_id)
    REFERENCES Users(id),
    CONSTRAINT FOREIGN KEY(Cid) 
    REFERENCES categories(Cid))`;
connection.query(createTable,(err,result)=>{
    if(err)throw err;
    console.log("products table created!!");
});
}
