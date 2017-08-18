package com.tm.inventory.dl;
import java.sql.*;
public class DAOConnection
{
private DAOConnection()
{

}
public static Connection getConnection() throws DAOException
{
Class c=null;
try
{
Class.forName("org.sqlite.JDBC");
}catch(ClassNotFoundException classNotFoundException)
{
throw new DAOException("JDBC driver not found: org.sqlite.JDBC");
}
Connection connection=null;
try
{
connection=DriverManager.getConnection("jdbc:sqlite:c:/tomcat7/webapps/product/WEB-INF/data/product.db");
}catch(SQLException sqlException)
{
throw new DAOException("Cannot connect to database: product.db");
}
return connection;
}
}