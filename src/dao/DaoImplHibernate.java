package dao;

import java.text.DateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import model.Employee;
import model.Product;
import model.ProductHistory;

public class DaoImplHibernate implements Dao {
	private Transaction tr;
	private Session session;

	@Override
	public void disconnect() {
		session.close();
	}
	
	@Override
	public void connect() {
		Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
		org.hibernate.SessionFactory sessionFactory = configuration.buildSessionFactory();
    	session = sessionFactory.openSession();		
    	
	}
	
	@Override
	public ArrayList<Product> getInventory() {
		ArrayList<Product> inventory = new ArrayList<Product>();
		try {
			tr = session.beginTransaction();

			Query query = session.createQuery("select u from Product u");
			List<Product> list = query.list();

			inventory.addAll(list);

			tr.commit();
		} catch (HibernateException e) {
			if (tr != null)
				tr.rollback();
			e.printStackTrace();
		}
		return inventory;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {		
		try {
			tr = session.beginTransaction();
			
			for (Product product : inventory) {
				ProductHistory ph = new ProductHistory(
						product.isAvailable(), 
						product.getId(), 
						product.getName(), 
						product.getPrice(), 
						product.getStock()
						);
				
				session.save(ph);
			}
			tr.commit();
			return true;
		} catch (HibernateException e) {
			if (tr != null)
				tr.rollback();
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void addProduct(Product product) {
		try {
			tr = session.beginTransaction();
			
			session.save(product);
			
			tr.commit();
		} catch (HibernateException e) {
			if (tr != null)
				tr.rollback();
			e.printStackTrace();
		}

	}

	@Override
	public void updateProduct(Product product) {
		product.setStock(product.getStock());
		
		try {
			tr = session.beginTransaction();
			
			session.save(product);
			
			tr.commit();
		} catch (HibernateException e) {
			if (tr != null)
				tr.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void deleteProduct(Product productName) {			
		try {
			tr = session.beginTransaction();
			
			session.remove(productName);
			
			tr.commit();
		} catch (HibernateException e) {
			if (tr != null)
				tr.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		try {
			tr = session.beginTransaction();
			
			Employee employee = session.get(Employee.class, employeeId);
			
			tr.commit();
			
			return employee;
			
		} catch (HibernateException e) {
			if (tr != null)
				tr.rollback();
			e.printStackTrace();
		}
		return null;
	}


}
