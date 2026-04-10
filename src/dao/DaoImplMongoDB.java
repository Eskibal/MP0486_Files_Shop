package dao;

import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import model.Employee;
import model.Product;

public class DaoImplMongoDB implements Dao {
	MongoCollection<Document> inventory;
	MongoCollection<Document> employees;
	MongoCollection<Document> historical;

	ObjectId id;

	@Override
	public void connect() {
		String uri = "mongodb://localhost:27017";
		MongoClientURI clientURI = new MongoClientURI(uri);
		MongoClient client = new MongoClient(clientURI);

		MongoDatabase db = client.getDatabase("shop");

		employees = db.getCollection("employees");
		inventory = db.getCollection("inventory");
		historical = db.getCollection("historical_inventory");
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;

		Document doc = employees.find(eq("employeeid", employeeId)).first();

		try {
			if (doc != null && password.equals(doc.getString("password"))) {
				employee = new Employee(doc.getInteger("employeeid"), doc.getString("name"), doc.getString("password"));
			}
			
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	public ArrayList<Product> getInventory() {
		ArrayList<Product> products = new ArrayList<>();
		
		Iterable<Document> docs = inventory.find();
		
		try {
			for (Document doc : docs) {
				Document priceDoc = (Document) doc.get("wholesalerprice");
				
				double value = priceDoc.getDouble("value");
				String currency = priceDoc.getString("currency");
				
				products.add(new Product(doc.getString("name"), doc.getBoolean("available"), value, doc.getInteger("stock")));
			}
			
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return products;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> list) {
		int counterProduct = 1;
		
		try {
			for (Product product : list) {
				Document priceDoc = new Document(
						"value", product.getPrice())
						.append("currency", "€");
				
				Date now = new Date();
				
				Document doc = new Document("_id", new ObjectId())
						.append("id", counterProduct)
						.append("name", product.getName())
						.append("wholesalerprice", priceDoc)
						.append("available", product.isAvailable())
						.append("stock", product.getStock())
						.append("created_at", now);
				
				historical.insertOne(doc);
				counterProduct++;
			}
			
			return true;
			
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void addProduct(Product product) {
		Document lastId = inventory.find()
				.sort(Sorts.descending("id"))
		        .first();
		    
		Document priceDoc = new Document(
				"value", product.getPrice())
				.append("currency", "€");			
			
		Document doc = new Document("_id", new ObjectId())
				.append("name", product.getName())
				.append("wholesalerprice", priceDoc)
				.append("available", true)
				.append("stock", product.getStock())
				.append("id", lastId.getInteger("id") + 1);
			
		inventory.insertOne(doc);
			
	}

	@Override
	public void updateProduct(Product product) {
		inventory.updateOne(eq("name", product.getName()), set("stock", product.getStock()));
		
	}

	@Override
	public void deleteProduct(Product product) {
		inventory.deleteOne(eq("name", product.getName()));
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

}
