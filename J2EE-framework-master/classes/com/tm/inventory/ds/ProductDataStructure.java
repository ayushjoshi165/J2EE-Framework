package com.tm.inventory.ds;
import java.util.*;
import java.sql.*;
import com.tm.inventory.dl.*;
public class ProductDataStructure implements java.io.Serializable
{
private int productCode=0;
private HashMap<Integer,Product> productsHashMap;
private TreeSet<Product> productsTreeSet;
public ProductDataStructure()
{
loadDataStructure();
System.out.println(productsHashMap.size());
}
private void loadDataStructure()
{
productsHashMap=new HashMap();
productsTreeSet=new TreeSet();
Connection connection=null;
try
{
connection=DAOConnection.getConnection();
Statement statement=connection.createStatement();
ResultSet resultSet=statement.executeQuery("select * from product");
Product product;
while(resultSet.next())
{
product=new Product();
product.setCode(resultSet.getInt("code"));
product.setName(resultSet.getString("name").trim());
product.setSalePrice(resultSet.getBigDecimal("sale_price"));
product.setPurchasePrice(resultSet.getBigDecimal("purchase_price"));
productsHashMap.put(product.getCode(),product);
productsTreeSet.add(product);
if(product.getCode()>this.productCode)
{
this.productCode=product.getCode();
}
}
resultSet.close();
statement.close();
connection.close();
}catch(DAOException daoException)
{
System.out.println(daoException);
}catch(SQLException sqlException)
{
System.out.println(sqlException);
}catch(Exception exception)
{
System.out.println(exception);
}
}
synchronized public int getNextProductCode()
{
this.productCode++;
return this.productCode;
}
synchronized public void addProduct(Product product)
{
productsHashMap.put(product.getCode(),product);
productsTreeSet.add(product);
}
synchronized public void updateProduct(Product product)
{
removeProduct(product.getCode());
addProduct(product);
}
synchronized public void removeProduct(int code)
{
Product product=productsHashMap.get(code);
productsHashMap.remove(code);
productsTreeSet.remove(product);
}
synchronized public TreeSet getProducts()
{
return this.productsTreeSet;
}
synchronized public Product getProductByCode(int code)
{
return productsHashMap.get(code);
}
synchronized public Product getProductByName(String name)
{
Product product=new Product();
product.setName(name);
Product dsProduct;
try
{
dsProduct=productsTreeSet.tailSet(product).first();
if(dsProduct.compareTo(product)==0)
{
return dsProduct;
}
else
{
return null;
}
}catch(NoSuchElementException noSuchElementException)
{
return null;
}
}
synchronized public boolean codeExists(int code)
{
return productsHashMap.get(code)!=null;
}
synchronized public boolean nameExists(String name)
{
Product product=new Product();
product.setName(name);
return productsTreeSet.contains(product);

}
synchronized public int getProductCount()
{
return productsHashMap.size();
}
}