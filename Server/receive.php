<?php


//echo $data->test;

$servername = "localhost";
$username = "root";
$password = "";
$database = "testandroid";
 
 
//creating a new connection object using mysqli 
$conn = new mysqli($servername, $username, $password, $database);
 
//if there is some error connecting to the database
//with die we will stop the further execution by displaying a message causing the error 
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Takes raw data from the request
	$json = file_get_contents('php://input');
	$data = json_decode($json);

//Drop if exists: Part 1 of 3
$sql = "DROP TABLE IF EXISTS patients"; //Drop table if it already exists.
if ($conn->query($sql) === TRUE) {
		//Create: Part 2 of 3
		$sql = "CREATE TABLE `patients` (
			`Id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
			`fullname` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
			`address` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
			`dateofexposure` date NOT NULL
			) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci";
		if ($conn->query($sql) === TRUE) {
			
			$sql = "INSERT INTO patients (fullname, address, dateofexposure) VALUES ";
			foreach($data->Data as $mydata) {
				//Insert: Part 3 of 3, query
				$sql .= "('".$mydata->fullname."', '".$mydata->address."' ,'".$mydata->dateofexposure."'),"; //append every element on values
			}

			$sql = substr($sql, 0, -1) . ";"; //Replace last comma with ; delimiter
			if ($conn->query($sql) === TRUE) {
				echo "Commit completed successfully!";
			} else {
				echo "Did not Insert!";
			}
			
		} else {
			echo "Failed to create table";
		}
	} else {
		echo "Failed to drop table";
	}

$conn->close();



?>