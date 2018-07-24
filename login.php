<?php

include 'config.php';

$pbuuid = ($_POST['pbuuid']);
$campaigncode = ($_POST['campaigncode']);

$result = $conn->prepare("SELECT * FROM campaigns WHERE campaigncode = '$campaigncode'");
$result->execute();

$userdata = $conn->prepare("SELECT * FROM users WHERE pbuuid = '$pbuuid'");
$userdata->execute();

$clean_code = str_replace(' ', '', $campaigncode);

while ($row_userdata = $userdata->fetchAll(PDO::FETCH_ASSOC)){

    $blocked_flag = $row_userdata[0]["$clean_code"];
}


if (($result->rowCount() > 0) && ($campaigncode !== "") && ($blocked_flag !== "Y")){

    while ($row = $result->fetchAll(PDO::FETCH_ASSOC))
    {

        echo "True" . "," . $row[0]["q1"] . "," .$row[0]["q2"] . "," .$row[0]["q3"] . "," .$row[0]["q4"] .",".$row[0]["script"].",".$row[0]["rtype"] ;

    }

}
else {
    echo "False";
}

?>