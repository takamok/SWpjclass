<?php
header("Content-Type: text/html; charset=UTF-8");

$db_host="localhost";
$db_user="root";
$db_passwd="apmsetup";
$db_name="TestDatabase";

$id = $_POST["id"];
$name = $_POST["name"];
$age = $_POST["age"];
$gender = $_POST["gender"];

$conn = mysqli_connect($db_host, $db_user, $db_passwd, $db_name);
mysqli_query($conn, "set session character_set_connection=utf8;");
mysqli_query($conn, "set session character_set_results=utf8;");
mysqli_query($conn, "set session character_set_client=utf8;");
$query = "INSERT INTO test (id, name, age, gender) VALUES ('$id', '$name', '$age', '$gender');";
$result = mysqli_query($conn, $query);

if($result)
      echo " Result: OK";
    else
      echo " Result: Error";

mysqli_close($conn);
?>


