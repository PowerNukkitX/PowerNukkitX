<?php
$fd = fopen(__DIR__ . "/../src/main/java/cn/nukkit/network/process/PacketHandler.java", "wb+");

fwrite($fd, "package cn.nukkit.network.process;\n");
fwrite($fd, "\n");
fwrite($fd, "import cn.nukkit.network.protocol.*;\n");
fwrite($fd, "\n");
fwrite($fd, "public interface PacketHandler {\n");

$dir = scandir(__DIR__ . "/../src/main/java/cn/nukkit/network/protocol/");
$nMax = count($dir) - 1;
$suffix = 'Packet.java';
foreach ($dir as $n => $file) {
	if (str_ends_with($file, $suffix)) {
		$qualifiedName = substr($file, 0, -strlen($suffix)) . 'Packet';
		if(
		    $qualifiedName === 'DataPacket' ||
		    $qualifiedName === 'AbstractResourcePackDataPacket'
		){
			continue;
		}
		fwrite($fd, "\tdefault void handle($qualifiedName pk) {\n\t}");
		fwrite($fd, "\n\n");
	}
}
fwrite($fd, "}\n");

fclose($fd);