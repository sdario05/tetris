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

// Consulta: convertir score a número, ordenar y limitar a 100
$sql = "SELECT name, score 
        FROM scores 
        ORDER BY CAST(score AS UNSIGNED) DESC 
        LIMIT 100";

$result = $conn->query($sql);

// Preparar array de resultados
$scores = [];
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $scores[] = $row;
    }
}

// Devolver como JSON
header('Content-Type: application/json');
echo json_encode($scores, JSON_UNESCAPED_UNICODE);

$conn->close();
?>
