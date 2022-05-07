<?php
 //these are the server details
//the username is root by default in case of xampp
//password is nothing by default
//and lastly we have the database named android. if your database name is different you have to change it 
//$a=array("sdsds","angelika","fggjfgjf");
//echo $a[1];


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
 
 $response = array();
 
 /* Server receives from android the following:
   apicall  (GET METHOD): always login (signup is not implemented yet from the android side)
   username (POST METHOD):
   password (POST METHOD):
   */
 
 if(isset($_GET['apicall']))
 {
 
	switch($_GET['apicall'])
	{
 //This capability is not implemented from the android side
		case 'signup':
			if(isTheseParametersAvailable(array('username','email','password')))
			{
				$username = $_REQUEST['username']; 
				$email = $_REQUEST['email']; 
				$password = $_REQUEST['password'];
 
				$stmt = $conn->prepare("SELECT id FROM user WHERE username = ? OR email = ?");
				$stmt->bind_param("ss", $username, $email);
				$stmt->execute();
				$stmt->store_result();
 
				if($stmt->num_rows > 0)
				{
					$response['error'] = true;
					$response['message'] = 'User already registered';
					$stmt->close();
				}
				else
				{
					$stmt = $conn->prepare("INSERT INTO user (username, email, password) VALUES (?, ?, ?)");
					$stmt->bind_param("sss", $username, $email, $password);
 
					if($stmt->execute())
					{
						$stmt = $conn->prepare("SELECT id, username, email FROM user WHERE username = ?"); 
						$stmt->bind_param("s",$username);
						$stmt->execute();
						$stmt->bind_result($id, $username, $email);
						$stmt->fetch();
 
						$user = array(
							'id'=>$id, 
							'username'=>$username, 
							'email'=>$email
							);
 
						$stmt->close();
						
					//second the patients table is converted to a php table
					$stmt = $conn->prepare("SELECT * FROM patients");
					$stmt->execute();
					$stmt->store_result();
 
					$patients = array();//indexed array
					if($stmt->num_rows > 0)
					{
 
						$stmt->bind_result($id, $fullname, $address, $dateofexposure);
						while ($stmt->fetch())
						{
							$tmp=array(
								'fullname'=>$fullname,
								'address'=>$address,
								'dateofexposure'=>$dateofexposure
							);
							array_push($patients, $tmp);
						}
					}
						
						$response['error'] = false; 
						$response['message'] = 'User registered successfully';
						$response['user'] = $user;
						$response['patients'] = $patients;
					}
				}
 
			}
			else
			{
				$response['error'] = true; 
				$response['message'] = 'required parameters are not available'; 
			}
 
			break; 
 
		case 'login':
 /*in case of login then fill the assoc array $response with the following indeces/components:
 error ->true, indicates ok from the server part
 message-> a message to show to android screen if desred
 user-> an assoc array which holds the data of the user who logged in
 routes->an indexed array which contains the points the logged in user needs to visit
 */
 
 //First the 'user' component is made up
			if(isTheseParametersAvailable(array('password')))
			{
				//$password = md5($_REQUEST['password']); 
				$email = $_REQUEST['email'];
				$password = $_REQUEST['password']; 
				$stmt = $conn->prepare("SELECT id, username, email FROM user WHERE email = ? AND password = ?");
				$stmt->bind_param("ss", $email, $password);
				$stmt->execute();
				$stmt->store_result();
 
				if($stmt->num_rows > 0)
				{
					$stmt->bind_result($id, $username, $email);
					$stmt->fetch();
 
					$user = array(
						'id'=>$id,
						'username'=>$username,
						'email'=>$email
					);
					
					//second the patients table is converted to a php table
					$stmt = $conn->prepare("SELECT * FROM patients");
					$stmt->execute();
					$stmt->store_result();
 
					$patients = array();//indexed array
					if($stmt->num_rows > 0)
					{
 
						$stmt->bind_result($id, $fullname, $address, $dateofexposure);
						while ($stmt->fetch())
						{
							$tmp=array(
								'fullname'=>$fullname,
								'address'=>$address,
								'dateofexposure'=>$dateofexposure
							);
							array_push($patients, $tmp);
						}
					}
 
 //Now the $response array is made up from the four components
					$response['error'] = false;
					$response['message'] = 'Login successfull';
					$response['user'] = $user;
					$response['patients'] = $patients;
				}
				else
				{
					$response['error'] = false; 
					$response['message'] = 'Invalid username or password';
				}
			}
			break;
 
		default: 
			$response['error'] = true;
			$response['message'] = 'Invalid Operation Called';
	}
 
 }
 else
 {
	$response['error'] = true; 
	$response['message'] = 'Invalid API Call';
 }
 
// echo var_dump($response);
// echo "<br>";
//var_dump($response);
//var_dump($response);
 echo json_encode($response);
 
 function isTheseParametersAvailable($params){
 
	foreach($params as $param)
	{
		if(!isset($_REQUEST[$param]))
		{
			return false; 
		}
	}
	return true; 
 }
 ?>