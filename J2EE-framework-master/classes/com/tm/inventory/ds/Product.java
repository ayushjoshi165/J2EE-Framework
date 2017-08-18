package com.tm.inventory.ds;
import java.math.*;
public class Product implements Comparable<Product>,java.io.Serializable
{
private int code;
private String name;
private BigDecimal salePrice;
private BigDecimal purchasePrice;
public void setCode(int code)
{
this.code=code;
}
public int getCode()
{
return this.code;
}
public void setName(String name)
{ 
this.name=name.trim();
}
public String getName()
{
return this.name;
}
public void setSalePrice(BigDecimal salePrice)
{ 
this.salePrice=salePrice;
}
public BigDecimal getSalePrice()
{
return this.salePrice;
}
public void setPurchasePrice(BigDecimal purchasePrice)
{ 
this.purchasePrice=purchasePrice;
}
public BigDecimal getPurchasePrice()
{
return this.purchasePrice;
}
public boolean equals(Object object)
{ 
if(!(object instanceof Product))
{
return false;
}
Product product=(Product)object;
return this.code==product.getCode();
}
public int compareTo(Product product)
{
return this.name.compareToIgnoreCase(product.name);
}
}