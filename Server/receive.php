<?php


//echo $data->test;

$servername = "localhost";
$username = "root";
$password = "";
$database = "androidtest";
 
 
//creating a new connection object using mysqli 
$conn = new mysqli($servername, $username, $password, $database);
 
//if there is some error connecting to the database
//with die we will stop the further execution by displaying a message causing the error 
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
$sql = "DELETE  FROM patients";
if ($conn->query($sql) === TRUE) {
	
	} else {
	
	}
	
	
	
	
	// Takes raw data from the request
$json = file_get_contents('php://input');
$data = json_decode($json);

foreach($data->Data as $mydata) {
    

	$sql = "INSERT INTO test (descrip,categ)
	VALUES ('".$mydata->descrip."', '".$mydata->categ."')";

	if ($conn->query($sql) === TRUE) {
	
	} else {
	
	}
}

$conn->close();



?>