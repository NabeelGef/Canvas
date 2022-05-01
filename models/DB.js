const Sequelize = require('sequelize');
const connection = new Sequelize(
  'myData','root','',
 {
       dialect:'mysql',
       host:'localhost',
       port:'3307'
 }
  );
  module.exports = connection;
