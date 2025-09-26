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
$score  = $_REQUEST['score'] ?? '';

if ($name && $score) {
    // Preparar sentencia segura
    $stmt = $conn->prepare("INSERT INTO scores (name, score) VALUES (?, ?)");
    $stmt->bind_param("si", $name, $score);

    if ($stmt->execute()) {
        header('Content-Type: application/json; charset=utf-8');
        $response = array("response" => "CORRECTO");
        echo json_encode($response);
    } else {
        header('Content-Type: application/json; charset=utf-8');
        $response = array("response" => "error: " . $stmt->error);
        echo json_encode($response);
    }

    $stmt->close();
} else {
    header('Content-Type: application/json; charset=utf-8');
    $response = array("response" => "error: Faltan datos (nombre y puntaje)");
    echo json_encode($response);
}

$conn->close();
?>