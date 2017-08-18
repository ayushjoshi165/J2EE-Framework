package com.tm.inventory.managers;
import static com.tm.inventory.utility.ConsoleUtility.*;
import java.util.*;
import java.io.*;
import java.math.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.tm.inventory.ds.*;
import com.tm.inventory.dl.*;
import com.tm.inventory.processor.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
public class ProductManager implements ProcessManager
{
static private ProductManager productManager;
private ProductDataStructure productDataStructure;
private ProductManager()
{
System.out.println("Product Manager instantiated...");
}
public static ProductManager getInstance()
{
if(productManager==null) productManager=new ProductManager();
return productManager; 
}
public void process(ServletContext servletContext,HttpServletRequest request,HttpServletResponse response,String nameOfOperation,JSONObject jsonObject)
{
if(this.productDataStructure==null)
{
this.productDataStructure=((ApplicationDataStructure)servletContext.getAttribute("applicationDataStructure")).getProductDataStructure();
}
if(nameOfOperation.equals("getAll"))
{
System.out.println("GetAll request processing...");
System.out.println("--------------------------------------------------------------------");
JSONObject productsJSONObject=getProducts();
writeResponse(response,productsJSONObject);
return;
}
if(nameOfOperation.equals("add"))
{
System.out.println("Add request processing...");
System.out.println("--------------------------------------------------------------------");
JSONObject addResultJSONObject=addProduct(jsonObject.toString());
writeResponse(response,addResultJSONObject);
return;
}
if(nameOfOperation.equals("update"))
{
System.out.println("Update request processing...");
System.out.println("--------------------------------------------------------------------");

JSONObject updateResultJSONObject=updateProduct(jsonObject.toString());
writeResponse(response,updateResultJSONObject);
return;
}
if(nameOfOperation.equals("delete"))
{
System.out.println("Delete request processing...");
System.out.println("--------------------------------------------------------------------");

JSONObject deleteResultJSONObject=deleteProduct(jsonObject.toString());
writeResponse(response,deleteResultJSONObject);
return;
}
if(nameOfOperation.equals("getByCode"))
{
System.out.println("GetByCode request processing...");
System.out.println("--------------------------------------------------------------------");
JSONObject getProductByCodeResultJSONObject=getProductByCode(jsonObject.toString());
writeResponse(response,getProductByCodeResultJSONObject);
return;
}
}
private JSONObject validateBeforeAdding(Product product)
{
boolean success=true;
JSONObject jsonObject=new JSONObject();
JSONObject jsonAttribute;
if(product.getName()==null || product.getName().length()==0)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","Name required");
jsonObject.put("name",jsonAttribute);
}
if(product.getSalePrice()==null) product.setSalePrice(new BigDecimal("0.00"));
if(product.getPurchasePrice()==null) product.setPurchasePrice(new BigDecimal("0.00"));
BigDecimal bigDecimal=product.getSalePrice().setScale(2,RoundingMode.FLOOR);
product.setSalePrice(bigDecimal);
bigDecimal=product.getPurchasePrice().setScale(2,RoundingMode.FLOOR);
product.setPurchasePrice(bigDecimal);
if(product.getSalePrice().signum()<0)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","sale price cannot be less than zero");
jsonObject.put("salePrice",jsonAttribute);
}
if(product.getPurchasePrice().signum()<0)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","purchase price cannot be less than zero");
jsonObject.put("purchasePrice",jsonAttribute);
}
if(productDataStructure.nameExists(product.getName()))
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","Name exists");
jsonObject.put("name",jsonAttribute);
}
jsonObject.put("success",success);
return jsonObject;
}
private JSONObject addProduct(String jsonString)
{
JSONObject jsonObject;
ObjectMapper objectMapper=new ObjectMapper();
Product product;
try
{
product=objectMapper.readValue(jsonString,Product.class);
}catch(IOException ioException)
{
jsonObject=new JSONObject();
jsonObject.put("success",false);
jsonObject.put("error","Invalid data in request");
return jsonObject;
}
jsonObject=validateBeforeAdding(product);
if(!((Boolean)jsonObject.get("success")))
{
return jsonObject;
}
product.setCode(productDataStructure.getNextProductCode());
productDataStructure.addProduct(product);
Runnable runnable=new Runnable(){
public void run()
{
Connection connection=null;
try
{
connection=DAOConnection.getConnection();
connection.setAutoCommit(false);
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("insert into product values(?,?,?,?)");
preparedStatement.setInt(1,product.getCode());
preparedStatement.setString(2,product.getName());
preparedStatement.setBigDecimal(3,product.getSalePrice());
preparedStatement.setBigDecimal(4,product.getPurchasePrice());
preparedStatement.executeUpdate();
preparedStatement.close();
connection.commit();
}catch(SQLException sqlException)
{
log(sqlException.getMessage());
}catch(DAOException daoException)
{
log(daoException.getMessage());
}
finally
{
try
{
if(connection!=null) connection.close();
}catch(SQLException sqlException)
{
log(sqlException.getMessage());
}
}
}
};
Thread thread=new Thread(runnable);
thread.start();
jsonObject.put("message","Product added");
jsonObject.put("responseType","json");
jsonObject.put("result","message");
return jsonObject;
}
private JSONObject validateBeforeUpdating(Product product)
{
boolean success=true;
JSONObject jsonObject=new JSONObject();
JSONObject jsonAttribute;
if(productDataStructure.getProductByCode(product.getCode())==null)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","Invalid product code");
jsonObject.put("code",jsonAttribute);
}
if(product.getName()==null || product.getName().length()==0)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","product name required");
jsonObject.put("name",jsonAttribute);
}
if(product.getSalePrice()==null) product.setSalePrice(new BigDecimal("0.00"));
if(product.getPurchasePrice()==null) product.setPurchasePrice(new BigDecimal("0.00"));
BigDecimal bigDecimal=product.getSalePrice().setScale(2,RoundingMode.FLOOR);
product.setSalePrice(bigDecimal);
bigDecimal=product.getPurchasePrice().setScale(2,RoundingMode.FLOOR);
product.setPurchasePrice(bigDecimal);
if(product.getSalePrice().signum()<0)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","Sale price cannot be less than zero");
jsonObject.put("salePrice",jsonAttribute);
}
if(product.getPurchasePrice().signum()<0)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","Purchase price cannot be less than zero");
jsonObject.put("purchasePrice",jsonAttribute);
}
Product productDS=productDataStructure.getProductByName(product.getName());
if(productDS!=null && productDS.getCode()!=product.getCode())
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","Item name exists");
jsonObject.put("name",jsonAttribute);
}
jsonObject.put("success",success);
return jsonObject;
}

private JSONObject updateProduct(String jsonString)
{
ObjectMapper objectMapper=new ObjectMapper();
Product product;
try
{
product=(Product)objectMapper.readValue(jsonString,Product.class);
}catch(IOException ioException)
{
JSONObject jsonObject=new JSONObject();
jsonObject.put("success",false);
jsonObject.put("error","Invalid data in request");
return jsonObject;
}
JSONObject jsonObject=validateBeforeUpdating(product);
if(!((Boolean)jsonObject.get("success")))
{
return jsonObject;
}
productDataStructure.updateProduct(product);
Runnable runnable=new Runnable()
{
public void run()
{
Connection connection=null;
try
{
connection=DAOConnection.getConnection();
connection.setAutoCommit(false);
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("update product set name=?,sale_price=?,purchase_price=? where code=?");
preparedStatement.setString(1,product.getName());
preparedStatement.setBigDecimal(2,product.getSalePrice());
preparedStatement.setBigDecimal(3,product.getPurchasePrice());
preparedStatement.setInt(4,product.getCode());
preparedStatement.executeUpdate();
preparedStatement.close();
connection.commit();
}catch(SQLException sqlException)
{
log(sqlException.getMessage());
}
catch(DAOException daoException)
{
log(daoException.getMessage());
}
finally
{
try
{
if(connection!=null) connection.close();
}catch(SQLException sqlException)
{
log(sqlException.getMessage());
}
}
}
};
Thread thread=new Thread(runnable);
thread.start();
jsonObject.put("message","product updated");
jsonObject.put("responseType","json");
jsonObject.put("result","message");
return jsonObject;
}

private JSONObject validateBeforeDeleting(int code)
{
boolean success=true;
JSONObject jsonObject=new JSONObject();
JSONObject jsonAttribute;
if(productDataStructure.getProductByCode(code)==null)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","Invalid product code");
jsonObject.put("code",jsonAttribute);
}
jsonObject.put("success",success);
return jsonObject;
}
private JSONObject deleteProduct(String jsonString)
{
JSONParser jsonParser=new JSONParser();
Integer codeWrapper;
JSONObject codeJSONObject;
try
{
codeJSONObject=(JSONObject)jsonParser.parse(jsonString);
String code=(String)codeJSONObject.get("code");
codeWrapper=new Integer(Integer.parseInt(code));
}catch(ParseException parseException)
{
JSONObject jsonObject=new JSONObject();
jsonObject.put("success",false);
jsonObject.put("error","Invalid request data");
return jsonObject;
}
JSONObject jsonObject=validateBeforeDeleting(codeWrapper);
if(!((Boolean)jsonObject.get("success")))
{
return jsonObject;
}
productDataStructure.removeProduct(codeWrapper.intValue());
Runnable runnable=new Runnable()
{
public void run()
{
Connection connection=null;
try
{
connection=DAOConnection.getConnection();
connection.setAutoCommit(false);
PreparedStatement preparedStatement=connection.prepareStatement("delete from product where code=?");
preparedStatement.setInt(1,codeWrapper.intValue());
preparedStatement.executeUpdate();
preparedStatement.close();
connection.commit();
}catch(SQLException sqlException)
{
log(sqlException.getMessage());
}catch(DAOException daoException)
{
log(daoException.getMessage());
}
finally
{
try
{
if(connection!=null) connection.close();
}catch(SQLException sqlException)
{
log(sqlException.getMessage());
}
}
}
};
Thread thread=new Thread(runnable);
thread.start();
jsonObject.put("message","product deleted");
jsonObject.put("responseType","json");
jsonObject.put("result","message");
return jsonObject;
}

private JSONObject getProducts()
{
boolean success=true;
try
{
ObjectMapper objectMapper=new ObjectMapper();
String jsonString=objectMapper.writeValueAsString(productDataStructure.getProducts());
JSONObject jsonObject=new JSONObject();
JSONParser jsonParser=new JSONParser();
JSONArray jsonArray=(JSONArray)jsonParser.parse(jsonString);
jsonObject.put("success",success);
jsonObject.put("products",jsonArray);
jsonObject.put("responseType","json");
jsonObject.put("result","products");
return jsonObject;
}catch(IOException | ParseException exception)
{
success=false;
JSONObject jsonObject=new JSONObject();
jsonObject.put("success",success);
jsonObject.put("error","Unable to fetch data, try after some time");
return jsonObject;
}
}
private JSONObject validateBeforeGetProductByCode(int code)
{
boolean success=true;
JSONObject jsonObject=new JSONObject();
JSONObject jsonAttribute;
if(productDataStructure.getProductByCode(code)==null)
{
success=false;
jsonAttribute=new JSONObject();
jsonAttribute.put("error","Invalid product code");
jsonObject.put("code",jsonAttribute);
}
jsonObject.put("success",success);
return jsonObject;
}
private JSONObject getProductByCode(String jsonString)
{
ObjectMapper objectMapper=new ObjectMapper();
JSONParser jsonParser=new JSONParser();
Integer codeWrapper;
JSONObject codeJSONObject;
try
{
codeJSONObject=(JSONObject)jsonParser.parse(jsonString);
String code=(String)codeJSONObject.get("code");
codeWrapper=new Integer(Integer.parseInt(code));
}catch(ParseException parseException)
{
JSONObject jsonObject=new JSONObject();
jsonObject.put("success",false);
jsonObject.put("error","Invalid request data");
return jsonObject;
}
JSONObject jsonObject=validateBeforeGetProductByCode(codeWrapper.intValue());
if(!((Boolean)jsonObject.get("success")))
{
return jsonObject;
}
Product product=productDataStructure.getProductByCode(codeWrapper.intValue());
try
{
String productString=objectMapper.writeValueAsString(product);
JSONObject productJSONObject=(JSONObject)jsonParser.parse(productString);
jsonObject.put("product",productJSONObject);
jsonObject.put("responseType","json");
jsonObject.put("result","product");
return jsonObject;
}catch(IOException | ParseException exception)
{
JSONObject errorJSONObject=new JSONObject();
errorJSONObject.put("success",false);
errorJSONObject.put("error","Unable to fetch data try after some time");
return errorJSONObject;
}
}
public boolean equals(Object object)
{
if(!(object instanceof ProcessManager)) return false;
return true;
}
public int compareTo(ProcessManager processManager)
{
return 0;
}
private void writeResponse(HttpServletResponse response,JSONObject jsonObject)
{
try
{
response.setContentType("application/json");
PrintWriter pw=response.getWriter();
pw.print(jsonObject);
}catch(IOException ioException)
{
log(ioException.getMessage());
}
}
}