<?php
header("Content-Type: text/html; charset=UTF-8");

$db_host="localhost";
$db_user="root";
$db_passwd="apmsetup";
$db_name="gpsdata";

$conn = mysqli_connect($db_host, $db_user, $db_passwd, $db_name);
mysqli_query($conn, "set session character_set_connection=utf8;");
mysqli_query($conn, "set session character_set_results=utf8;");
mysqli_query($conn, "set session character_set_client=utf8;");
$query = "SELECT * FROM gpsshare";
$result = mysqli_query($conn, $query);
while($row = mysqli_fetch_array($result)){
	$data = "$row[0],$row[1],$row[2]";
	echo $data;
	echo "<br>";
}   
mysqli_close($conn);
?>