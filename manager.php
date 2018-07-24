<?php

include 'config.php';

$results=$conn->prepare("SELECT campaigncode FROM campaigns WHERE username = '$username'");
$results->execute();

if ($results->rowCount() >0){

   while($row = $results->fetchAll(PDO::FETCH_ASSOC)){
  $code = $row[0]['campaigncode'];
  }
  
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $stmt = $conn->prepare("SELECT pbname, calltotals FROM users WHERE pbuuid IN (SELECT pulled FROM '$code' WHERE pulled = pbuuid) AND calltotals != 0"); 
    $stmt->execute();

    $result = $stmt->setFetchMode(PDO::FETCH_ASSOC); 
}
$pbuuid = ($_POST['pbuuid']);
$campaigncode = ($_POST['campaigncode']);

$result = $conn->prepare("SELECT voterfirstname, voterlastname, phonenumber, voteruuid, gender FROM $campaigncode WHERE calldate IS NULL AND pulled IS NULL LIMIT 1");

$feedback = $conn->prepare("SELECT * FROM users WHERE pbuuid = '$pbuuid'");
$total_feedback = $conn->prepare("SELECT COUNT(calldate) FROM $campaigncode WHERE calldate is NOT NULL");

$total_feedback->execute();
$feedback->execute();
$result->execute();

if ($total_feedback->rowCount() >0){

    while($total_num = $total_feedback->fetchAll(PDO::FETCH_ASSOC)){
    $total_result = $total_num[0]['COUNT(calldate)'];
    }
}

if ($feedback->rowCount() >0){

    while($number = $feedback->fetchAll(PDO::FETCH_ASSOC)){
    $other = $number[0]['calltotals'];
    }
}

if ($result->rowCount() > 0) {

    while($row = $result->fetchAll(PDO::FETCH_ASSOC)) {

	echo $row[0]["voterfirstname"]. "," . $row[0]["voterlastname"] . "," . $row[0]["phonenumber"] . "," . $row[0]['voteruuid']. "," . $row[0]['gender'].",".$other.",".$total_result; 
  
    $voterid = $row[0]['voteruuid'];
    }
} 

else {  
	echo "False";
}

$pull = $conn->prepare("UPDATE $campaigncode SET pulled = '$pbuuid' WHERE voteruuid = $voterid");

$pull->execute();
 
?>
