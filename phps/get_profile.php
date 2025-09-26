<?php
// Datos de conexión
$servername = "sql100.infinityfree.com";
$username   = "if0_40026302";   // el que creaste en tu hosting
$password   = "kctoCljOkH";  // la contraseña
$database   = "if0_40026302_tetromino_db";      // el nombre de tu base

// Crear conexión
$conn = new mysqli($servername, $username, $password, $database);

// Verificar conexión
if ($conn->connect_error) {
    die("Conexión fallida: " . $conn->connect_error);
}

// Recibir datos por POST
$name = $_REQUEST['name'] ?? '';
$fbid  = $_REQUEST['fbid'] ?? '';

if ($name && $fbid) {

    $stmt = $conn->prepare("SELECT stage FROM users WHERE fbid = ?");
    $stmt->bind_param("s", $fbid); // "s" = string
    $stmt->execute();
    $result = $stmt->get_result();
    $stmt->close();

    if ($result && $result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $stage = $row['stage'];
        header('Content-Type: application/json; charset=utf-8');
        $response = array("response" => $stage);
        echo json_encode($response);
    
    } else {
        $stmt = $conn->prepare("INSERT INTO users (name, fbid, stage) VALUES (?, ?, ?)");
        $stage = 1;
        $stmt->bind_param("ssi", $name, $fbid, $stage);
        if ($stmt->execute()) {
            header('Content-Type: application/json; charset=utf-8');
            $response = array("response" => "NEW_USER");
            echo json_encode($response);
        } else {
            header('Content-Type: application/json; charset=utf-8');
            $response = array("response" => "ERROR: " . $stmt->error);
            echo json_encode($response);
        }

        $stmt->close();
    }

} else {
    header('Content-Type: application/json; charset=utf-8');
    $response = array("response" => "ERROR: Faltan datos (nombre y facebook id)");
    echo json_encode($response);
}

$conn->close();
?>