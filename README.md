## 6431 Course Project

**_Author: Tianxi (Frank) Li_**

**_Date: 12/01/2019_**

Course Project of 6431 Advanced Operating Systems from OSU.

The src directory contains all the source files of the project.

DiningService is the entrance and contains the main function.

### Build the project

Enter the project home directory

	cd 6431_OS_PROJECT

Create the directory for the class files.

	mkdir gen

Compile the java src

	javac -cp ./src -d ./gen ./src/DiningService.java 

### Run the program:

	java -cp ./gen DiningService ./data/project-sample-input-1.txt

Give the second argument to write the result to a file.

	java -cp ./gen DiningService ./data/project-sample-input-1.txt ../output/output-1.txt

##### Note: the messages at the same time may be out of order.
