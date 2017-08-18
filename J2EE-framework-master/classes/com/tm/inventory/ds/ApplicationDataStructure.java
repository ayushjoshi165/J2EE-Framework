package com.tm.inventory.ds;
import java.util.*;
import java.sql.*;
import com.tm.inventory.dl.*;
public class ApplicationDataStructure implements java.io.Serializable
{
private ProductDataStructure productDataStructure;
public ApplicationDataStructure()
{
System.out.println("Loading data structure...");
loadDataStructure();
System.out.println("All data structure loaded...");
}
public void loadDataStructure()
{
productDataStructure=new ProductDataStructure();
}
public ProductDataStructure getProductDataStructure()
{
return this.productDataStructure;
}
}