package com.hit.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class tablePanel extends JPanel {

	private static final long serialVersionUID = -4507927243069560742L;

	JTable dataTable;
	DefaultTableModel dm;
	/*String[][] data = {
			 {"1", "King Arthur", "Some guy", "Some pub guy", "1687"},
			 {"2", "The muffin man", "Same guy", "Same pub guy", "1087"}, 
			 {"3", "Shrek", "Some cool guy", "Some pub guy", "2003"}, 
			 {"4", "Cat in boots", "Some author", "Some pub guy", "1845"}, 
			 {"5", "Giants slayer", "Some auth guy", "Some pub guy", "2007"} 
	 };*/
	ArrayList<Vector<Object>> booksData = new ArrayList<>();
	
	public tablePanel() {
		super();
		
		String[] columnNames = { "Serial", "Author", "Title", "Publisher", "Date"};
	  
		dataTable = new JTable();
		dm = new DefaultTableModel(columnNames, 0);
		
	    
	    dm.setColumnIdentifiers(columnNames);
	    dataTable.setModel(dm);
	    try {
			getData();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    for (int count = 0; count < booksData.size(); count++) {
	        dm.addRow(booksData.get(count));
	    }
		
		super.add(new JScrollPane(dataTable));
	}
	
	public void addNewBook() {
		Vector<Object> dataVector = new Vector<Object>();
        dataVector.add(dataTable.getRowCount() + 1);
        dataVector.add("");
        dataVector.add("");
        dataVector.add("");
        dataVector.add("");
        dm.addRow(dataVector);
	}
	
	public void saveSelected() {
		Vector<Object> dataVector = new Vector<Object>();
		int rowIndex = dataTable.getSelectedRow();
		dataVector.add(dataTable.getModel().getValueAt(rowIndex, 0));
		dataVector.add(dataTable.getModel().getValueAt(rowIndex, 1));
		dataVector.add(dataTable.getModel().getValueAt(rowIndex, 2));
		dataVector.add(dataTable.getModel().getValueAt(rowIndex, 3));
		
		try {
			Date dateinCell = (Date) dataTable.getModel().getValueAt(rowIndex, 4);
			dataVector.add(dateinCell.toString());
			System.out.println("date of book to remove: " + dateinCell.toString());
			
		}catch (Exception e) {
			java.util.Date tmpDate;
			try {
				tmpDate = new SimpleDateFormat("dd/MM/yyyy").parse((String) dataTable.getModel().getValueAt(rowIndex, 4));
				Calendar cal = Calendar.getInstance();
				cal.setTime(tmpDate);
				String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				dataVector.add(formatedDate);
				
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		
		
		boolean newBook = true;
		
		for(Vector<Object> iter : booksData) {
			if (iter.get(0) == dataVector.get(0)) {
				newBook = false;
				break;
			}
		}
		
		if(newBook) {
			try {
				saveNewBook(dataVector);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				updateExistingBook(dataVector);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		JOptionPane.showMessageDialog(this, "saved Book id: " + dataVector.get(0));
	}
	
	public void removeSelected() throws UnknownHostException, IOException, InterruptedException, ParseException {
		
		final String host = "localhost";
		final int portNumber = 8024;
		
		Socket socket = new Socket(host, portNumber);
		socket.setSoTimeout(2500);

		PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		
		JSONObject myJSON = new JSONObject();
		myJSON.putOnce("service", "delete");
		
		int rowIndex = dataTable.getSelectedRow();
		
		JSONObject book = new JSONObject(); 
		book.putOnce("author", dataTable.getModel().getValueAt(rowIndex, 1));
		book.putOnce("publisher", dataTable.getModel().getValueAt(rowIndex, 3));
		book.putOnce("title",dataTable.getModel().getValueAt(rowIndex, 2));
		
		book.putOnce("modelID", dataTable.getModel().getValueAt(rowIndex, 0));
		
		try {
			Date dateinCell = (Date) dataTable.getModel().getValueAt(rowIndex, 4);
			book.putOnce("date", dateinCell.toString());
			System.out.println("date of book to remove: " + dateinCell.toString());
			
		}catch (Exception e) {
			java.util.Date tmpDate = new SimpleDateFormat("dd/MM/yyyy").parse((String) dataTable.getModel().getValueAt(rowIndex, 4));
			Calendar cal = Calendar.getInstance();
			cal.setTime(tmpDate);
			String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
			book.putOnce("date", formatedDate);
		} 
		
		  
		JSONObject[] booksArray = {book};  
		myJSON.putOnce("data", booksArray);
		 

		outStream.println(myJSON.toString());
		System.out.println("client sent: " + myJSON.toString());

		Thread.sleep(5000);
		String sockRes = "";

		try {
			sockRes = inStream.readLine().toString();
			System.out.println("got message from server: " + sockRes);

		} catch (SocketTimeoutException e) {
			System.out.println("timeout on client");
			e.printStackTrace();
		}
		socket.close();
		
		int index = dataTable.getSelectedRow();
		dm.removeRow(index);
		JOptionPane.showMessageDialog(this, "Removed Book.");
	}
	
	private void getData() throws UnknownHostException, IOException, InterruptedException {
		
		final String host = "localhost";
		final int portNumber = 8024;

		Socket socket = new Socket(host, portNumber);
		socket.setSoTimeout(2500);

		PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	
		JSONObject myJSON = new JSONObject();
		myJSON.putOnce("service", "all");
		  

		outStream.println(myJSON.toString());
		System.out.println("client sent: " + myJSON.toString());

		Thread.sleep(5000);
		String sockRes = "";

		try {
			sockRes = inStream.readLine().toString();
			JSONArray outputJson = new JSONArray(sockRes);
			
			for (int i = 0; i < outputJson.length(); i++) {
				System.out.println("got JSON from server: " + outputJson.toString());
				BookPojo hlpr = new BookPojo(outputJson.getJSONObject(i).getString("author"),
						outputJson.getJSONObject(i).getString("publisher"),
						outputJson.getJSONObject(i).getString("title"),
						outputJson.getJSONObject(i).getString("date"));
				
				Long thisId = outputJson.getJSONObject(i).getLong("modelID");
				
				Vector<Object> tmp = new Vector<Object>();
				tmp.add(thisId);
				tmp.add(hlpr.getAuthor());
				tmp.add(hlpr.getTitle());
				tmp.add(hlpr.getPublisher());
				tmp.add(hlpr.getDate());
				
				booksData.add(tmp);
			}
			

		} catch (SocketTimeoutException e) {
			System.out.println("timeout on client");
			e.printStackTrace();
		}
		socket.close();
	}
	
	
	private void saveNewBook(Vector<Object> dataVector) throws InterruptedException, UnknownHostException, IOException {
		final String host = "localhost";
		final int portNumber = 8024;

		System.out.println("Starting save new!!");
		
		Socket socket = new Socket(host, portNumber);
		socket.setSoTimeout(2500);

		PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	
		JSONObject myJSON = new JSONObject();
		myJSON.putOnce("service", "create");
		  
		  
		JSONObject book1 = new JSONObject();
		book1.putOnce("author", dataVector.get(1));
		book1.putOnce("publisher", dataVector.get(3));
		book1.putOnce("title",dataVector.get(2));
		book1.putOnce("date", dataVector.get(4));
		book1.putOnce("modelID", dataVector.get(0));
		
		  
		JSONObject[] booksArray = {book1};  
		myJSON.putOnce("data", booksArray);
		
		outStream.println(myJSON.toString());
		System.out.println("client sent: " + myJSON.toString());

		Thread.sleep(5000);
		String sockRes = "";

		try {
			sockRes = inStream.readLine().toString();
			System.out.println("got message from server: " + sockRes);

		} catch (SocketTimeoutException e) {
			System.out.println("timeout on client");
			e.printStackTrace();
		}
		socket.close();
	}
	
	private void updateExistingBook(Vector<Object> dataVector) throws UnknownHostException, IOException, InterruptedException {
		final String host = "localhost";
		final int portNumber = 8024;

		System.out.println("Starting update!");
		
		
		Socket socket = new Socket(host, portNumber);
		socket.setSoTimeout(2500);

		PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		
		JSONObject myJSON = new JSONObject();
		myJSON.putOnce("service", "update");
		  
		JSONObject book1 = new JSONObject();
		book1.putOnce("author", dataVector.get(1));
		book1.putOnce("publisher", dataVector.get(3));
		book1.putOnce("title",dataVector.get(2));
		book1.putOnce("date", dataVector.get(4));
		book1.putOnce("modelID", dataVector.get(0));
		  
		JSONObject[] booksArray = {book1};  
		myJSON.putOnce("data", booksArray);
		 

		outStream.println(myJSON.toString());
		System.out.println("client sent: " + myJSON.toString());

		Thread.sleep(5000);
		String sockRes = "";

		try {
			sockRes = inStream.readLine().toString();
			System.out.println("got message from server: " + sockRes);

		} catch (SocketTimeoutException e) {
			System.out.println("timeout on client");
			e.printStackTrace();
		}
		socket.close();
	}
	
}
