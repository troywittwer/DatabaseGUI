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

-------------------------------------------------------------
Date: 12/10/2018

The project has taken one large change: rather than using the DBTableModel class, I've performed database connectivity in the DatabaseConnect class. ResultSet, it's metadata, and the TableView have all been moved to the Main class.

Aside from the performing some lambda expressions, the start method primarily consists of instatiating GUI components and adding them to the scene.

The Main class's start method holds some lambda expressions, such as those for the slider, the textfield associated with the slider, and the button changing the shape's color. Other handled events take place in the overriden handle method, such as the "Select Query" and "Reset Table" buttons. 

Upon being selected, "Select Query" button attempts to connect to the database, perform the user's query, and print out the total number of REQUIRED columns and rows. This is done dynamically since the compiler doesn't know exactly how many columns or rows are going to be printed out after a statement is executed. First, the program will collect the number of columns using the result set's metadata to find the total number of columns. Next, the program will retrieve the names of each column based on the values listed in each column of the 0th row. After the columns have been established, the program will add each column to the TableView and continue until no more rows exist. Each row is added to an ObservableList object as a collection. After each row has been accounted for, all rows are added to the TableView object. 

I made a few minor tweaks, but the primary logic behind this dynamic result set came from the following:
https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/comment-page-1/

The "Reset Table" button reverts the table back to an empty state. This is done by clearing the items and columns from the TableView object. The "Select Query" button also performs these actions at the beginning of it's steps.

The ColorPicker object is used to assign a color to the Circle object. Once a color is selected using the ColorPicker, the "Change Color" button can be selected and the Circle's color will change to that of the selected color in the ColorPicker.

The Line object was originally going to be something like a clock animation combined with the Circle object, which is why the two were grouped together. I wasn't able to figure this process out, so I just kind of left it there.

The slider and textfield are both functioning off of eachother's value. When the slider is moved, the the value shown in the textfield will adjust itself to that of the slider's current position. If the user enters a value into the textfield, the slider's position will adjust itself to the user's position. If the user enters something other than a character, a message will be submitted to the console stating that the user must enter a number. If the user enters a number below 0 (the minimum value), the slider will simply be set to 0. If the user enters a value above 200 (the maximum value of the slider), the slider will be set to 200. 

The slider was originally intended to be used as a distance that the Circle object would move, but, again, I couldn't grasp how to perform an animation. 

I did submit a CSS stylesheet and made sure the stylesheet was applied to the Scene object before setting the scene, however the community version of IntelliJ doesn't allow the use of CSS. I've based the styles listed in the CSS file off of standard naming conventions, so I would assume it works (I haven't been able to test it, though).

-------------------------------------------------------------------------------------------------
Adding some queries:

--SQL WHERE statement
SELECT *
FROM customers
WHERE firstName = 'Ellie'

--SQL Order By
SELECT *
FROM customers
Order By firstName

--SQL AND
SELECT *
FROM customers
WHERE lastName = 'Lock' AND age = 10

--SQL Multiple Joins (and I guess singular join, too)
SELECT cust.firstName, cust.lastName, prod.name, prod.pricePerUnit
FROM ((customers cust INNER JOIN orders ord ON cust.customerID = ord.customerID)
  INNER JOIN order_product op ON ord.orderNumber = op.orderNumber)
  INNER JOIN products prod ON op.productID = prod.productID

