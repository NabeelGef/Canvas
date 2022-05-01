const bcrypt = require('bcryptjs');
const db = require('../models/DB');
const user = require('../models/user');
const jwt = require('jsonwebtoken');
const config = require('../config/auth');
const multer = require('multer');
const path = require('path');
const sequileze = require('sequelize');
const roleUser = require('../models/role_user');
// exports.test = (req,res)=>{
//   var {title , content, HI} = req.body;
//   var {title,content ,HI} = changingFormString(req.body);
//   res.send(`Title is : ${title} , ${content} , ${HI}`);
// }
exports.edit = async(req,res)=>{
  var {name , email , password , address , role} = changingFormString(req.body);
  const image = req.file;
  console.log(`name : ${name} , ${email} , ${address}`);
  if(!email){
    return res.send('Error in account!!');
  }    if(image)
      {
        var imgsrc = "http://192.168.1.106:3000/images/"+image.filename;
        console.log('There Is Image!!');
      }
      if(password)
      {
        var hashpassword = await bcrypt.hash(password,10);
        console.log('There Is Password!!');
      }
        await user.user.update({
        name:name,
        image:imgsrc,
        address:address,
      },{
        where: {
          email : email 
        }
      }).then((result2)=>{
        if(result2==1){
          user.user.findOne({
            where:{
              email:email
            }
          }).then(result =>{
            res.send(result);
          }).catch(err =>{
            throw err;
          });
        }else{
          console.log('NOT FOUNDING');
          res.send('Not Founding');
        }
      }).catch((err)=>{
        console.log(`ERROR : ${err}`);
      });
      // res.json({
      //   name:name,
      //   password:password,
      //   address : address,
      //   role :role,
      //   image:imgsrc
      // });
}
function changingFormString(object){
  var data;
  for(i in object){
     object[i] = object[i].replace(/\"/g,"");;
    }
return object;
}
exports.register = async(req,res)=>{  
  const {name , email , address , role , password , url } = changingFormString(req.body);
  console.log(`Password = ${password}`);
    if(!(name&&email&&password&&address)){
        return res.status(400).send("ALL Input is required");
    }
    const hashpassword =await bcrypt.hash(password, 10);
    user.user.findOne({
      where:{
        email:email
      }
    }).then((result)=>{
            if(result){
        return res.send("The Email IS Used by other User");
      }else{
        if(req.file)
        {
          if(req.file.filename){
        var imgsrc = 'http://192.168.1.106:3000/images/'+req.file.filename
        console.log(imgsrc);
         }
        }
        user.user.create({
         name:name,
         email:email,
         password:hashpassword,
         address:address,
         image:imgsrc,
         url:url,
         role:role
        }).then(()=>{
            if(role=='user'){
              user.user.findOne({
                where:{
                  email:[email]
                }
              }).then((result)=>{
                var id = result.id;
                roleUser.roleUser.create({
                  role_id:2,
                  user_id:id
                }).catch(err=>{
                  console.log(`Error ${err}`);
                });    
              }).catch(err=>{
                console.log(`Error ${err}`);
              });
            }else if(role =='admin'){
              user.user.findOne({
                where:{
                  email:email
                }
              }).then((result)=>{
                var id = result.id;
                roleUser.roleUser.create({
                  role_id:1,
                  user_id:id
                }).catch(err=>{
                  console.log(`Error2 ${err}`);
                });    
              });
            }
          }
        ).catch(err=>{
          console.log(`Error ${err}`);
        });
      } res.send("Success Register"); 
    }).catch(err=>{
      console.log(`ERROR : ${err}`);
    }); 
  }
exports.getUsers= (req,res)=>{
    user.user.findAll({
       where : {
         id:{
           [sequileze.Op.not]:req.body.id
         } 
       }
    }).then(result=>{
      return res.send(result);
    }).catch(err=>{
      return res.status(400).send(err);
    });
}
exports.login=(req,res)=>{
  const{email,password,tokenMessage}=changingFormString(req.body);
    user.user.findOne({
      where:{
        email:[email]
      }
    }).then (async(result)=>{
      if(result){
        console.log("Password = "+password+"password user="+result.password);
        if(await bcrypt.compare(password,result.password)){
          user.user.findOne({
            where:{
              email:[email]
            }
          }).then(result2=>{
            const token = jwt.sign(
              {id:email},config.secret,{
                  expiresIn:"1h",
              });
              user.user.update({
                tokenMessage:tokenMessage,
                token:token        
              },{
                where:{
                  id:result2.id
                }
              }).then(()=>{
                 res.status(200).json({
                  id:result2.id,
                  name:result2.name,
                  address:result2.address,
                  image:result2.image,
                  url:result2.url,
                  email:result2.email,
                  tokenMessage : tokenMessage,
                  token:token
              });
              }).catch(err=>{
                throw err;
              });
          }).catch(err=>{
            throw err;
          });
        }else{
          res.status(404).send("ERROR IN PASSWORD");
        }
      }else{
        res.status(404).send("ERROR IN EMAIL");
      }
    }).catch(err=>{
      throw err;
    });
}
