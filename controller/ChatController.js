const mysql = require('../models/DB');
const chat = require('../models/chat');
const sequelize = require('sequelize');
exports.AddMessages = (req,res)=>{
    const {idme,idother,message}=req.body;
    if(!(idme&&idother&&message)){
        res.status(400).send('All Field required!!');
    }
    chat.chat.create({
        idme:idme,
        idother:idother,
        message:message
    }).then(()=>{
       return res.status(200).send('successfull!!');
    }).catch(err=>{
        console.log(`Error : ${err}`);
        if(err) throw err;
    });
}
exports.getMessagesBetweenTwoUsers = (req,res)=>{
    const {idme,idother} = req.body;
    if(!(idme&&idother)){
        res.status(400).send('All Field required!!');
    }
    chat.chat.findAll({
        where:{
            [sequelize.Op.or]:[{
                [sequelize.Op.and]:[{
                    idme:idme,
                    idother:idother
                }]},{
                 [sequelize.Op.and]:[{
                     idme:idother,
                     idother:idme
                 }]
            }]
                
        }
    }).then(result=>{
        res.status(200).send(result);
    }).catch(err=>{
        if(err) throw err;
    });
}