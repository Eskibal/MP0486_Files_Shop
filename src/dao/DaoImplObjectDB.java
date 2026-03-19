package dao;

import java.util.ArrayList;

import javax.persistence.*;

import model.Employee;
import model.Product;

public class DaoImplObjectDB implements Dao {
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("objects/users.odb");
	EntityManager em = emf.createEntityManager();

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		
		TypedQuery<Employee> query = 
				em.createQuery("SELECT p FROM Employee p WHERE p.employeeId = :employeeId AND p.password = :password",
								Employee.class);
		query.setParameter("employeeId", employeeId);
		query.setParameter("password", password);
		
		for (Employee e : query.getResultList()) {
			employee = new Employee(e.getEmployeeId(), e.getName(), e.getPassword());
			System.out.println("Logged In " + employee.toString());
		}
		
		return employee;
	}

	@Override
	public ArrayList<Product> getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> list) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addProduct(Product product) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProduct(Product product) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteProduct(Product product) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

}
