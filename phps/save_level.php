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

$fbid = $_REQUEST['fbid'] ?? '';

$stmt = $conn->prepare("UPDATE users SET stage = stage + 1 WHERE fbid = ?");
$stmt->bind_param("s", $fbid);
$stmt->execute();

$conn->close();
?>