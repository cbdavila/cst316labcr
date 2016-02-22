package banking.primitive.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

import banking.primitive.core.Account.State;

class ServerSolution implements AccountServer {

	static String fileName = "accounts.ser";

	Map<String,Account> accountMap = null;
/**
 * The Constructor initializes the HashMap with string and account type, and file location to
 * record the accounts when saved.
 */
	public ServerSolution() {
		accountMap = new HashMap<String,Account>();
		File file = new File(fileName);
		ObjectInputStream in = null;
		try {
			if (file.exists()) {
				System.out.println("Reading from file " + fileName + "...");
				in = new ObjectInputStream(new FileInputStream(file));

				Integer sizeI = (Integer) in.readObject();
				int size = sizeI.intValue();
				for (int i=0; i < size; i++) {
					Account acc = (Account) in.readObject();
					if (acc != null)
						accountMap.put(acc.getName(), acc);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	/**
	 * This method will check if the account name exists, and add it to the Map if the account name does not exist
	 * @param type The type of account, Checking or Savings
	 * @param name The new account name
	 * @param balance The starting account balance
	 * @return True if account is added to the Map
	 * @throws IllegalArgumentException
	 */
	private boolean newAccountFactory(String type, String name, float balance)
		throws IllegalArgumentException {
		
		if (accountMap.get(name) != null) return false;
		
		Account acc;
		if ("Checking".equals(type)) {
			acc = new Checking(name, balance);

		} else if ("Savings".equals(type)) {
			acc = new Savings(name, balance);

		} else {
			throw new IllegalArgumentException("Bad account type:" + type);
		}
		try {
			accountMap.put(acc.getName(), acc);
		} catch (Exception exc) {
			return false;
		}
		return true;
	}
	/**
	 * This method will create an account if given the appropriate parameters
	 * @param Account type Checking or Savings
	 * @name  name The name of the new account
	 * @param balance The Starting balance
	 * @return True if account is created successfully
	 */
	public boolean newAccount(String type, String name, float balance) 
		throws IllegalArgumentException {
		
		if (balance < 0.0f) throw new IllegalArgumentException("New account may not be started with a negative balance");
		
		return newAccountFactory(type, name, balance);
	}
	/**
	 * This method will close an account if the given paramter is found
	 * @param name the account name to close
	 * @return boolean if the name of the account is found, return true to close
	 */
	public boolean closeAccount(String name) {
		Account acc = accountMap.get(name);
		if (acc == null) {
			return false;
		}
		acc.setState(State.CLOSED);
		return true;
	}
	/**This method returns an Account with the given name
	 * @param name The name of the account
	 * @return The account wanted
	 */
	public Account getAccount(String name) {
		return accountMap.get(name);
	}
	/**
	 * This method will return a List object of all Accounts.
	 * @return  A list of all accounts currently created.
	 */
	public List<Account> getAllAccounts() {
		return new ArrayList<Account>(accountMap.values());
	}
	/**
	 * This method returns a list of current accounts in use
	 * @return A List of all currently active accounts
	 */
	public List<Account> getActiveAccounts() {
		List<Account> result = new ArrayList<Account>();

		for (Account acc : accountMap.values()) {
			if (acc.getState() != State.CLOSED) {
				result.add(acc);
			}
		}
		return result;
	}
	/**
	 * This method writes all current accounts to accounts.ser file
	 * @return void
	 * @throws IOException if file is incorrect
	 */
	public void saveAccounts() throws IOException {
		ObjectOutputStream out = null; 
		try {
			out = new ObjectOutputStream(new FileOutputStream(fileName));

			out.writeObject(Integer.valueOf(accountMap.size()));
			for (int i=0; i < accountMap.size(); i++) {
				out.writeObject(accountMap.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Could not write file:" + fileName);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

}
