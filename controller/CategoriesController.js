const mysql = require('../models/DB');
const connect = mysql.connection;
global.name_product = undefined;
exports.AddCategories = (req,res)=>{
    console.log('CATEGORIES');
const Cname = req.body.Cname;
console.log(Cname);
if(!Cname){
    res.status(400).send("All Field Required");
}
var Inserted = "INSERT INTO categories (Cname) VALUES('"+Cname+"')";
connect.query(Inserted,(error,result)=>{
 if(error) {
     res.send("ERRRROOOORRRR"+error);
     return
 }
 res.status(200).send(result);
});
}
exports.getCategories = (req,res)=>{
 name_product =  "SELECT products.Pname FROM Products INNER JOIN categories ON categories.Cid = products.Cid"  
 connect.query(name_product,(error,result)=>{
   if(error)throw error;
   name_product = result;
 });
 var getAllCategories = "SELECT * FROM categories";
 connect.query(getAllCategories,(error,result)=>{
     if(error)throw error;
     res.json({
         name_product:name_product,
         result:result
     });
 });
}