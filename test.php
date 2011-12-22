<?php 
$output = shell_exec("javac code.java 2>&1");
echo $output;
?>