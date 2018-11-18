# DatabaseGUI

Date: 11/17/2018

The initial version of the database GUI has been completed. Functionality includes successfully connecting to a database as well
as a very primitive GUI interface consisting of four components:

1.) TextArea - where users will type their query.
2.) Button - users will click this to run their query.
3.) Label - displays the column name of the information being returned.
4.) Label - displays the output of the query. If multiple columns are returned, it only displays the last column.

A lot of the code used was based on the JFrame example provided by Oracle's BooksDB project. I was required to change a
good chunk of it simply because JavaFX isn't compatible with JFrame classes. Nevertheless, the logic is more or less the same
in a lot of areas. As an example, JavaFX cannot use ActionListener (or at least I couldn't get it to work). I had to use the
EventHandler class to assign actions to my button, instead.

Despite basing my DBTableModel class on Oracle's ResultSetTableModel class, this submission did not require all methods
introduced. I chose to only include the constructor and two methods: setQuery and disconnectFromDatabase. By this point I felt
knowledgable enough to create my own methods, so I created getQueryColumnName and getQueryAnswer (I'm not sure if Oracle's
example project had these, I just did them on my own).

getQueryColumnName is a method that returns a String object representing the actual name of the database variable holding the
answer being returned. That's why the returned value has no spaces.

getQueryAnswer returns the last row's answer to the query. If the user were to enter the following query:
  SELECT lastName FROM employees WHERE employeeID = 1
the output would display the first employee's last name since they're the only one specified. If the user entered another query:
  SELECT lastName FROM employees
the output would display the last row's result (employeeID = 4). 
