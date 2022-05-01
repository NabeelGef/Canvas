const mysql = require('../models/DB');
const webPush = require('web-push');
global.nameUser = undefined;
global.nameCategory = undefined;
const connect = mysql.connection;
exports.AddProduct = (req,res)=>{
const {user_id , Cid , Pname , photo ,info_connect , quantity , price , exp_date} = req.body;
if(!(user_id&&Cid&&Pname&&info_connect&&quantity&&price&&exp_date)){
    res.status(400).send("All Field Required");
}
var Inserted = "INSERT INTO products(user_id,Cid,Pname,photo,info_connect,quantity,price,exp_date)"+
"VALUES('"+user_id+"','"+Cid+"','"+Pname+"','"+photo+"','"+info_connect+"','"+quantity+"','"+price+"','"+exp_date+"')";
//console.log("USER_ID = " + user_id +"\t Cid = " + Cid);
connect.query(Inserted,(error,result)=>{
 if(error) throw error; 
//  const subscribe = req.body;
//      const payload =JSON.stringify({title:'Notification'});
//      webPush.sendNotification(subscribe,payload).catch(err=>console.error(err));
    res.status(200).send("SuccessFully Products");
});

}
exports.getProduct =(req,res)=>{
    let name_user = "SELECT Users.name FROM Users INNER JOIN products ON products.user_id=Users.id"
    connect.query(name_user,(error,result)=>{
     if(error) throw error
     nameUser = result;
    });
    let name_Category = "SELECT categories.Cname FROM categories INNER JOIN products ON products.Cid = categories.Cid"
    connect.query(name_Category,(error,result)=>{
        if(error) throw error;
        nameCategory = result;
    });
    let getAllProducts = "SELECT Pname , photo , info_connect , quantity , price , exp_date FROM products";
    connect.query(getAllProducts,(error,result)=>{
     if(error) throw error;
     res.json({
         name_Users : nameUser,
         name_Categories : nameCategory,
         result : result
        });
    });
}
exports.searchByProduct=(req,res)=>{
    const {name ,product_name,category_name}=req.body;
    if(!(name||product_name||category_name)){
        res.status(400).send("Not Found!! ");
    }
    else
    {
        if(name){
        var GETuser = 'SELECT id FROM Users WHERE name = ?';
        connect.query(GETuser,name,(error,result)=>{
         if(error)throw error;
         if(result.length>0){
                GETuser=result[0].id;
                var GETuser2 = "SELECT Cid, Pname , photo , info_connect , quantity , price , exp_date FROM products WHERE user_id=?";
                connect.query(GETuser2,GETuser,(error,result)=>{
                if(error) throw error;
                let GETcategory = "SELECT Cname FROM categories INNER JOIN products ON categories.Cid=?";
                connect.query(GETcategory,result[0].Cid,(error,result2)=>{
                    if(error) throw error;
                    res.json({
                        user_name : name,
                        nameCategory : result2[0].Cname,
                        result
                     });
                });
              });
            }
            else{
                res.status(400).send('Not Found Any Products');
            }
       });
    }
    if(product_name){
        var GETuser = "SELECT user_id , Cid,Pname , photo , info_connect , quantity , price , exp_date FROM products WHERE Pname='"+product_name+"'";
        connect.query(GETuser,(error,result)=>{
        if(error) throw error;
        if(result.length>0)
            {
        let getUsers = "SELECT Users.name FROM Users INNER JOIN products ON Users.id= ?";
        connect.query(getUsers,result[0].user_id,(error,result2)=>{
            if(error)throw error;
            let GETcategory = "SELECT categories.Cname FROM categories INNER JOIN products ON categories.Cid = ?";
            connect.query(GETcategory,result[0].Cid,(error,result3)=>{
              if(error) throw error;
              res.json({
                user_name : result2[0].name,
                nameCategory : result3[0].Cname,
                result
            });
            });
        }); 
    }      else{
        res.status(400).send('Not Found Any Products');
    }
       });
    }
    if(category_name){
        var GETcategory = "SELECT Cid FROM categories WHERE Cname = ?";
        connect.query(GETcategory,category_name,(error,result)=>{
         if(error)throw error;
         if(result.length>0)
         {
             GETcategory = result[0].Cid;
             var GETcategory2 = "SELECT user_id,Pname , photo , info_connect , quantity , price , exp_date FROM products WHERE Cid=?";
             connect.query(GETcategory2,GETcategory,(error,result)=>{
             if(error) throw error;
             let getUsers = "SELECT name FROM Users INNER JOIN products ON Users.id = ? ";
             connect.query(getUsers,result[0].user_id,(error,result2)=>{
               if(error) throw error;
               res.json({
                user_name : result2[0].name,
                category_name : category_name,
                result
             });
             });
          });
        }
    else{
        res.status(400).send('Not Found Any Products');
    }
       });

    }
}
    
}
exports.deleteProduct=(req,res)=>{
    const Pid = req.body.Pid;
    const user_id = req.body.user_id;
    if(!(Pid&&user_id)){
        res.status(400).send("The Field Is Empty");
    }
    let DELETED ="DELETE FROM products WHERE Pid = ? and user_id = ?";
    connect.query(DELETED,[Pid,user_id],(error,result)=>{
      if(error) throw error;
      if(result.affectedRows===0){
        res.send('No Item DELETED!!');
      }
      res.status(200).send('DELETED SUCCESS');
    }); 
}