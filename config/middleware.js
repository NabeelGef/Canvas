const jwt = require('jsonwebtoken');
const config = require('./auth');
const User = require('../models/user');
const categoriesControllers = require('../controller/CategoriesController');
verifyToken = (req, res, next) => {
    let token = req.headers["x-access-token"];
    if (!token) {
      return res.status(403).send({
        message: "No token provided!"
      });
    }
    jwt.verify(token, config.secret, (err, decoded) => {
      if (err) {
        return res.status(401).send({
          message: "Unauthorized!"
        });
      }
      req.id= decoded.id;
      next();
    });
  };
  isAdmin = (req,res,next)=>{ 
    User.connections.query("SELECT role FROM Users WHERE email = '"+req.id+"'",(err,result)=>{
   if(err) throw err;
    if(result[0].role!="admin"){
      res.status(403).send('You arenot admin!!');
      return;
    }
    next();
    });
  }
    module.exports = {
      verifyToken:verifyToken,
      isAdmin:isAdmin
    };