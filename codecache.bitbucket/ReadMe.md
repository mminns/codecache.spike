# JMX

# Experiment 1

Running Bitbucket using 

>atlas-run

gave the following from http://localhost:7990/bitbucket/plugins/servlet/codecachejmx

    Thu Aug 15 11:18:58 BST 2019
    
    Memory Pool: Code Cache
        Init : 2MB (2555904)
        Used: 80MB (82912384)
        Max : 245MB (251658240)
        Committed: 81MB (83427328)


# Experiment 2

Setting the code cache to arbitrarily very small

> atlas-run --jvmargs "-XX:ReservedCodeCacheSize=200k"

Fails to start

    [INFO] Invalid ReservedCodeCacheSize: 200K. Must be at least InitialCodeCacheSize=2496K.
    [INFO] Error: Could not create the Java Virtual Machine.
    [INFO] Error: A fatal exception has occurred. Program will exit.

# Experiment 3

Setting the code cache to arbitrarily very small and attempt to align it with _InitialCodeCacheSize_

> atlas-run --jvmargs "-XX:ReservedCodeCacheSize=200k -XX:InitialCodeCacheSize=200K"

Fails to start

    [INFO] Invalid ReservedCodeCacheSize=200K. Must be at least 900K.
    [INFO] Error: Could not create the Java Virtual Machine.
    [INFO] Error: A fatal exception has occurred. Program will exit.

# Experiment 4

Setting 

> atlas-run --jvmargs "-XX:ReservedCodeCacheSize=900k -XX:InitialCodeCacheSize=900K"

Starts 

Actually it appears to stall

    [INFO] Java HotSpot(TM) 64-Bit Server VM warning: C1 initialization failed. Shutting down all compilers
    [INFO] Java HotSpot(TM) 64-Bit Server VM warning: C2 initialization failed. Shutting down all compilers

but then slowly continues but eventuall fails

    [INFO] 2019-08-15 11:32:27,655 INFO  [spring-startup]  com.atlassian.plugin.util.WaitUntil Plugins that have yet to be enabled: (3): [com.atlassian.plugins.atlassian-connect-plugin, com.atlassian.bitbucket.server.bitbucket-search, com.atlassian.bitbucket.server.bitbucket-mirroring-upstream], 278 seconds remaining
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD FAILURE
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 10:08 min
    [INFO] Finished at: 2019-08-15T11:32:28+01:00
    [INFO] ------------------------------------------------------------------------
    [ERROR] Failed to execute goal com.atlassian.maven.plugins:amps-dispatcher-maven-plugin:8.0.2:run (default-cli) on project codecache.spike: Timed out waiting for Bitbucket Server to start -> [Help 1]

# Experiment 5

Setting 

> atlas-run --jvmargs "-XX:ReservedCodeCacheSize=2496k -XX:InitialCodeCacheSize=2496K"


Starts and appears to run OK..

    [INFO] CodeCache: size=2496Kb used=1974Kb max_used=1982Kb free=521Kb
    [INFO]  bounds [0x000000010cda3000, 0x000000010d013000, 0x000000010d013000]
    [INFO]  total_blobs=815 nmethods=469 adapters=253
    [INFO]  compilation: disabled (not enough contiguous free space left)
    [INFO] Java HotSpot(TM) 64-Bit Server VM warning: CodeCache is full. Compiler has been disabled.
    [INFO] Java HotSpot(TM) 64-Bit Server VM warning: Try increasing the code cache size using -XX:ReservedCodeCacheSize=

gave the following from http://localhost:7990/bitbucket/plugins/servlet/codecachejmx

    Thu Aug 15 14:09:10 BST 2019

    Memory Pool: Code Cache
        Init : 2MB (2555904)
        Used: 1MB (2006336)
        Max : 2MB (2555904)
        Committed: 2MB (2555904)

## Notes

Interestingly
* _Used_ < _Max_ 

whereas 
* _Committed_ == _Max_
    * __Perhaps this should be the test?__ 

Also the JVM is currently logging to stdout/stderr so how would we read/check the logs for the compiler error messages ?

# Experiment 6

> atlas-run --jvmargs "-XX:ReservedCodeCacheSize=2496k -XX:InitialCodeCacheSize=2496K -XX:+PrintCodeCacheOnCompilation"

gave the following output

    [INFO] Starting bitbucket...
    [INFO] Configured Artifact: com.atlassian.bitbucket.server:bitbucket-it-resources:5.16.0:zip
    [INFO] com.atlassian.bitbucket.server:bitbucket-it-resources:5.16.0:zip already exists in /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket
    [INFO] Configured Artifact: com.atlassian.bitbucket.server:bitbucket-webapp:5.16.0:war
    [INFO] Unpacking /Users/mminns/.m2/repository/com/atlassian/bitbucket/server/bitbucket-webapp/5.16.0/bitbucket-webapp-5.16.0.war to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/app with includes "" and excludes ""
    [INFO] Configured Artifact: com.atlassian.pdkinstall:pdkinstall-plugin:0.6:jar
    [INFO] Copying pdkinstall-plugin-0.6.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/home/shared/plugins/installed-plugins/pdkinstall-plugin-0.6.jar
    [INFO] Configured Artifact: org.apache.felix:org.apache.felix.webconsole:1.2.8:jar
    [INFO] Copying org.apache.felix.webconsole-1.2.8.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/home/shared/plugins/installed-plugins/org.apache.felix.webconsole-1.2.8.jar
    [INFO] Configured Artifact: org.apache.felix:org.osgi.compendium:1.2.0:jar
    [INFO] Copying org.osgi.compendium-1.2.0.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/home/shared/plugins/installed-plugins/org.osgi.compendium-1.2.0.jar
    [INFO] Configured Artifact: com.atlassian.labs.httpservice:httpservice-bridge:0.6.2:jar
    [INFO] Copying httpservice-bridge-0.6.2.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/home/shared/plugins/installed-plugins/httpservice-bridge-0.6.2.jar
    [INFO] Configured Artifact: com.atlassian.devrel:developer-toolbox-plugin:2.0.17:jar
    [INFO] Copying developer-toolbox-plugin-2.0.17.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/home/shared/plugins/installed-plugins/developer-toolbox-plugin-2.0.17.jar
    [INFO] Configured Artifact: com.atlassian.labs:rest-api-browser:3.1.3:jar
    [INFO] Copying rest-api-browser-3.1.3.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/home/shared/plugins/installed-plugins/rest-api-browser-3.1.3.jar
    [INFO] Configured Artifact: com.atlassian.plugins:plugin-data-editor:1.2:jar
    [INFO] Copying plugin-data-editor-1.2.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/home/shared/plugins/installed-plugins/plugin-data-editor-1.2.jar
    [INFO] Configured Artifact: org.apache.servicemix.bundles:org.apache.servicemix.bundles.junit:4.12_1:jar
    [INFO] Copying org.apache.servicemix.bundles.junit-4.12_1.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/app/WEB-INF/atlassian-bundled-plugins/org.apache.servicemix.bundles.junit-4.12_1.jar
    [INFO] Configured Artifact: com.atlassian.plugins:atlassian-plugins-osgi-testrunner-bundle:2.0.1:jar
    [INFO] Copying atlassian-plugins-osgi-testrunner-bundle-2.0.1.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/app/WEB-INF/atlassian-bundled-plugins/atlassian-plugins-osgi-testrunner-bundle-2.0.1.jar
    [INFO] Configured Artifact: com.atlassian.bitbucket.search:embedded-elasticsearch-plugin:6.0.1:jar
    [INFO] Copying embedded-elasticsearch-plugin-6.0.1.jar to /Volumes/PROJECTS/projects/github.com/mminns/codecache-spike/codecache.spike/target/bitbucket/app/WEB-INF/atlassian-bundled-plugins/embedded-elasticsearch-plugin-6.0.1.jar
    [INFO] CodeCache: size=2496Kb used=1060Kb max_used=1060Kb free=1435Kb
    [INFO] CodeCache: size=2496Kb used=1057Kb max_used=1068Kb free=1438Kb
    [INFO] CodeCache: size=2496Kb used=1059Kb max_used=1069Kb free=1436Kb
    [INFO] CodeCache: size=2496Kb used=1072Kb max_used=1072Kb free=1423Kb
    [INFO] CodeCache: size=2496Kb used=1061Kb max_used=1072Kb free=1434Kb
    [INFO] CodeCache: size=2496Kb used=1062Kb max_used=1072Kb free=1433Kb
    [INFO] CodeCache: size=2496Kb used=1064Kb max_used=1072Kb free=1432Kb
    [INFO] CodeCache: size=2496Kb used=1064Kb max_used=1072Kb free=1431Kb
    [INFO] CodeCache: size=2496Kb used=1069Kb max_used=1072Kb free=1426Kb
    [INFO] CodeCache: size=2496Kb used=1071Kb max_used=1072Kb free=1424Kb
    [INFO] CodeCache: size=2496Kb used=1072Kb max_used=1072Kb free=1423Kb
    [INFO] CodeCache: size=2496Kb used=1073Kb max_used=1073Kb free=1422Kb
    [INFO] CodeCache: size=2496Kb used=1074Kb max_used=1074Kb free=1421Kb
    [INFO] CodeCache: size=2496Kb used=1075Kb max_used=1075Kb free=1420Kb
    [INFO] CodeCache: size=2496Kb used=1076Kb max_used=1076Kb free=1419Kb
    [INFO] CodeCache: size=2496Kb used=1078Kb max_used=1078Kb free=1418Kb
    [INFO] CodeCache: size=2496Kb used=1082Kb max_used=1082Kb free=1413Kb
    [INFO] CodeCache: size=2496Kb used=1083Kb max_used=1083Kb free=1413Kb
    [INFO] CodeCache: size=2496Kb used=1083Kb max_used=1083Kb free=1412Kb
    [INFO] CodeCache: size=2496Kb used=1092Kb max_used=1092Kb free=1403Kb
    [INFO] CodeCache: size=2496Kb used=1095Kb max_used=1095Kb free=1401Kb
    [INFO] CodeCache: size=2496Kb used=1097Kb max_used=1097Kb free=1398Kb
    [INFO] CodeCache: size=2496Kb used=1098Kb max_used=1098Kb free=1397Kb
    [INFO] CodeCache: size=2496Kb used=1098Kb max_used=1110Kb free=1397Kb
    [INFO] CodeCache: size=2496Kb used=1099Kb max_used=1110Kb free=1396Kb
    [INFO] CodeCache: size=2496Kb used=1100Kb max_used=1110Kb free=1395Kb
    [INFO] CodeCache: size=2496Kb used=1100Kb max_used=1110Kb free=1395Kb
    [INFO] CodeCache: size=2496Kb used=1102Kb max_used=1110Kb free=1393Kb
    [INFO] CodeCache: size=2496Kb used=1104Kb max_used=1110Kb free=1391Kb
    [INFO] CodeCache: size=2496Kb used=1105Kb max_used=1110Kb free=1390Kb
    [INFO] CodeCache: size=2496Kb used=1113Kb max_used=1113Kb free=1383Kb
    [INFO] CodeCache: size=2496Kb used=1113Kb max_used=1113Kb free=1382Kb
    [INFO] CodeCache: size=2496Kb used=1115Kb max_used=1115Kb free=1380Kb
    [INFO] CodeCache: size=2496Kb used=1118Kb max_used=1118Kb free=1377Kb
    [INFO] CodeCache: size=2496Kb used=1119Kb max_used=1119Kb free=1376Kb
    [INFO] CodeCache: size=2496Kb used=1120Kb max_used=1120Kb free=1375Kb
    [INFO] CodeCache: size=2496Kb used=1121Kb max_used=1121Kb free=1374Kb
    [INFO] CodeCache: size=2496Kb used=1121Kb max_used=1121Kb free=1374Kb
    [INFO] CodeCache: size=2496Kb used=1122Kb max_used=1122Kb free=1373Kb
    [INFO] CodeCache: size=2496Kb used=1123Kb max_used=1123Kb free=1372Kb
    [INFO] CodeCache: size=2496Kb used=1125Kb max_used=1125Kb free=1370Kb
    [INFO] CodeCache: size=2496Kb used=1126Kb max_used=1126Kb free=1369Kb
    [INFO] CodeCache: size=2496Kb used=1127Kb max_used=1127Kb free=1368Kb
    [INFO] CodeCache: size=2496Kb used=1131Kb max_used=1131Kb free=1364Kb
    [INFO] CodeCache: size=2496Kb used=1138Kb max_used=1138Kb free=1357Kb
    [INFO] CodeCache: size=2496Kb used=1140Kb max_used=1140Kb free=1355Kb
    [INFO] CodeCache: size=2496Kb used=1141Kb max_used=1141Kb free=1354Kb
    [INFO] CodeCache: size=2496Kb used=1142Kb max_used=1142Kb free=1353Kb
    [INFO] CodeCache: size=2496Kb used=1143Kb max_used=1143Kb free=1352Kb
    [INFO] CodeCache: size=2496Kb used=1144Kb max_used=1144Kb free=1351Kb
    [INFO] CodeCache: size=2496Kb used=1144Kb max_used=1144Kb free=1351Kb
    [INFO] CodeCache: size=2496Kb used=1146Kb max_used=1146Kb free=1349Kb
    [INFO] CodeCache: size=2496Kb used=1147Kb max_used=1147Kb free=1348Kb
    [INFO] CodeCache: size=2496Kb used=1148Kb max_used=1148Kb free=1347Kb
    [INFO] CodeCache: size=2496Kb used=1149Kb max_used=1149Kb free=1346Kb
    [INFO] CodeCache: size=2496Kb used=1150Kb max_used=1150Kb free=1345Kb
    [INFO] CodeCache: size=2496Kb used=1151Kb max_used=1151Kb free=1344Kb
    [INFO] CodeCache: size=2496Kb used=1159Kb max_used=1159Kb free=1336Kb
    [INFO] CodeCache: size=2496Kb used=1160Kb max_used=1160Kb free=1335Kb
    [INFO] CodeCache: size=2496Kb used=1161Kb max_used=1161Kb free=1334Kb
    [INFO] CodeCache: size=2496Kb used=1161Kb max_used=1161Kb free=1334Kb
    [INFO] CodeCache: size=2496Kb used=1162Kb max_used=1162Kb free=1333Kb
    [INFO] CodeCache: size=2496Kb used=1163Kb max_used=1163Kb free=1332Kb
    [INFO] CodeCache: size=2496Kb used=1164Kb max_used=1164Kb free=1332Kb
    [INFO] CodeCache: size=2496Kb used=1164Kb max_used=1164Kb free=1331Kb
    [INFO] CodeCache: size=2496Kb used=1166Kb max_used=1166Kb free=1329Kb
    [INFO] CodeCache: size=2496Kb used=1167Kb max_used=1167Kb free=1328Kb
    [INFO] CodeCache: size=2496Kb used=1168Kb max_used=1168Kb free=1327Kb
    [INFO] CodeCache: size=2496Kb used=1169Kb max_used=1169Kb free=1326Kb
    [INFO] CodeCache: size=2496Kb used=1170Kb max_used=1170Kb free=1325Kb
    [INFO] CodeCache: size=2496Kb used=1172Kb max_used=1172Kb free=1323Kb
    [INFO] CodeCache: size=2496Kb used=1173Kb max_used=1173Kb free=1322Kb
    [INFO] CodeCache: size=2496Kb used=1174Kb max_used=1185Kb free=1321Kb
    [INFO] CodeCache: size=2496Kb used=1177Kb max_used=1185Kb free=1318Kb
    [INFO] CodeCache: size=2496Kb used=1181Kb max_used=1185Kb free=1315Kb
    [INFO] CodeCache: size=2496Kb used=1183Kb max_used=1185Kb free=1312Kb
    [INFO] CodeCache: size=2496Kb used=1185Kb max_used=1185Kb free=1310Kb
    [INFO] CodeCache: size=2496Kb used=1187Kb max_used=1187Kb free=1308Kb
    [INFO] CodeCache: size=2496Kb used=1189Kb max_used=1189Kb free=1306Kb
    [INFO] CodeCache: size=2496Kb used=1190Kb max_used=1190Kb free=1305Kb
    [INFO] CodeCache: size=2496Kb used=1191Kb max_used=1191Kb free=1304Kb
    [INFO] CodeCache: size=2496Kb used=1192Kb max_used=1192Kb free=1303Kb
    [INFO] CodeCache: size=2496Kb used=1199Kb max_used=1199Kb free=1296Kb
    [INFO] CodeCache: size=2496Kb used=1200Kb max_used=1200Kb free=1295Kb
    [INFO] CodeCache: size=2496Kb used=1201Kb max_used=1201Kb free=1294Kb
    [INFO] CodeCache: size=2496Kb used=1203Kb max_used=1203Kb free=1292Kb
    [INFO] CodeCache: size=2496Kb used=1205Kb max_used=1205Kb free=1290Kb
    [INFO] CodeCache: size=2496Kb used=1206Kb max_used=1206Kb free=1289Kb
    [INFO] CodeCache: size=2496Kb used=1209Kb max_used=1209Kb free=1286Kb
    [INFO] CodeCache: size=2496Kb used=1210Kb max_used=1210Kb free=1285Kb
    [INFO] CodeCache: size=2496Kb used=1211Kb max_used=1211Kb free=1284Kb
    [INFO] CodeCache: size=2496Kb used=1212Kb max_used=1212Kb free=1283Kb
    [INFO] CodeCache: size=2496Kb used=1213Kb max_used=1213Kb free=1282Kb
    [INFO] CodeCache: size=2496Kb used=1214Kb max_used=1226Kb free=1281Kb
    [INFO] CodeCache: size=2496Kb used=1230Kb max_used=1230Kb free=1265Kb
    [INFO] CodeCache: size=2496Kb used=1244Kb max_used=1244Kb free=1251Kb
    [INFO] CodeCache: size=2496Kb used=1233Kb max_used=1244Kb free=1262Kb
    [INFO] CodeCache: size=2496Kb used=1236Kb max_used=1244Kb free=1259Kb
    [INFO] CodeCache: size=2496Kb used=1236Kb max_used=1248Kb free=1259Kb
    [INFO] CodeCache: size=2496Kb used=1241Kb max_used=1253Kb free=1254Kb
    [INFO] CodeCache: size=2496Kb used=1242Kb max_used=1253Kb free=1253Kb
    [INFO] CodeCache: size=2496Kb used=1243Kb max_used=1253Kb free=1252Kb
    [INFO] CodeCache: size=2496Kb used=1245Kb max_used=1253Kb free=1250Kb
    [INFO] CodeCache: size=2496Kb used=1256Kb max_used=1256Kb free=1239Kb
    [INFO] CodeCache: size=2496Kb used=1257Kb max_used=1257Kb free=1238Kb
    [INFO] CodeCache: size=2496Kb used=1258Kb max_used=1258Kb free=1237Kb
    [INFO] CodeCache: size=2496Kb used=1270Kb max_used=1270Kb free=1225Kb
    [INFO] CodeCache: size=2496Kb used=1260Kb max_used=1271Kb free=1235Kb
    [INFO] CodeCache: size=2496Kb used=1261Kb max_used=1271Kb free=1234Kb
    [INFO] CodeCache: size=2496Kb used=1264Kb max_used=1271Kb free=1231Kb
    [INFO] CodeCache: size=2496Kb used=1265Kb max_used=1271Kb free=1230Kb
    [INFO] CodeCache: size=2496Kb used=1266Kb max_used=1271Kb free=1229Kb
    [INFO] CodeCache: size=2496Kb used=1266Kb max_used=1271Kb free=1229Kb
    [INFO] CodeCache: size=2496Kb used=1270Kb max_used=1271Kb free=1225Kb
    [INFO] CodeCache: size=2496Kb used=1279Kb max_used=1290Kb free=1216Kb
    [INFO] CodeCache: size=2496Kb used=1280Kb max_used=1290Kb free=1215Kb
    [INFO] CodeCache: size=2496Kb used=1281Kb max_used=1290Kb free=1214Kb
    [INFO] CodeCache: size=2496Kb used=1281Kb max_used=1290Kb free=1214Kb
    [INFO] CodeCache: size=2496Kb used=1282Kb max_used=1290Kb free=1213Kb
    [INFO] CodeCache: size=2496Kb used=1283Kb max_used=1290Kb free=1212Kb
    [INFO] CodeCache: size=2496Kb used=1283Kb max_used=1290Kb free=1212Kb
    [INFO] CodeCache: size=2496Kb used=1284Kb max_used=1290Kb free=1211Kb
    [INFO] CodeCache: size=2496Kb used=1286Kb max_used=1290Kb free=1209Kb
    [INFO] CodeCache: size=2496Kb used=1291Kb max_used=1291Kb free=1204Kb
    [INFO] CodeCache: size=2496Kb used=1292Kb max_used=1292Kb free=1204Kb
    [INFO] CodeCache: size=2496Kb used=1293Kb max_used=1293Kb free=1202Kb
    [INFO] CodeCache: size=2496Kb used=1294Kb max_used=1294Kb free=1201Kb
    [INFO] CodeCache: size=2496Kb used=1295Kb max_used=1295Kb free=1200Kb
    [INFO] CodeCache: size=2496Kb used=1296Kb max_used=1296Kb free=1199Kb
    [INFO] CodeCache: size=2496Kb used=1302Kb max_used=1302Kb free=1193Kb
    [INFO] CodeCache: size=2496Kb used=1302Kb max_used=1302Kb free=1193Kb
    [INFO] CodeCache: size=2496Kb used=1305Kb max_used=1305Kb free=1190Kb
    [INFO] CodeCache: size=2496Kb used=1306Kb max_used=1306Kb free=1189Kb
    [INFO] CodeCache: size=2496Kb used=1307Kb max_used=1307Kb free=1188Kb
    [INFO] CodeCache: size=2496Kb used=1307Kb max_used=1307Kb free=1188Kb
    [INFO] CodeCache: size=2496Kb used=1308Kb max_used=1308Kb free=1187Kb
    [INFO] CodeCache: size=2496Kb used=1312Kb max_used=1312Kb free=1184Kb
    [INFO] CodeCache: size=2496Kb used=1314Kb max_used=1314Kb free=1181Kb
    [INFO] CodeCache: size=2496Kb used=1316Kb max_used=1316Kb free=1179Kb
    [INFO] CodeCache: size=2496Kb used=1317Kb max_used=1317Kb free=1178Kb
    [INFO] CodeCache: size=2496Kb used=1318Kb max_used=1330Kb free=1177Kb
    [INFO] CodeCache: size=2496Kb used=1320Kb max_used=1330Kb free=1175Kb
    [INFO] CodeCache: size=2496Kb used=1322Kb max_used=1330Kb free=1173Kb
    [INFO] CodeCache: size=2496Kb used=1324Kb max_used=1330Kb free=1171Kb
    [INFO] CodeCache: size=2496Kb used=1328Kb max_used=1330Kb free=1167Kb
    [INFO] CodeCache: size=2496Kb used=1329Kb max_used=1330Kb free=1166Kb
    [INFO] CodeCache: size=2496Kb used=1331Kb max_used=1331Kb free=1165Kb
    [INFO] CodeCache: size=2496Kb used=1333Kb max_used=1333Kb free=1162Kb
    [INFO] CodeCache: size=2496Kb used=1338Kb max_used=1338Kb free=1157Kb
    [INFO] CodeCache: size=2496Kb used=1340Kb max_used=1340Kb free=1155Kb
    [INFO] CodeCache: size=2496Kb used=1340Kb max_used=1340Kb free=1155Kb
    [INFO] CodeCache: size=2496Kb used=1341Kb max_used=1341Kb free=1154Kb
    [INFO] CodeCache: size=2496Kb used=1343Kb max_used=1343Kb free=1152Kb
    [INFO] CodeCache: size=2496Kb used=1346Kb max_used=1358Kb free=1149Kb
    [INFO] CodeCache: size=2496Kb used=1348Kb max_used=1359Kb free=1147Kb
    [INFO] CodeCache: size=2496Kb used=1350Kb max_used=1359Kb free=1145Kb
    [INFO] CodeCache: size=2496Kb used=1353Kb max_used=1359Kb free=1142Kb
    [INFO] CodeCache: size=2496Kb used=1354Kb max_used=1366Kb free=1141Kb
    [INFO] CodeCache: size=2496Kb used=1358Kb max_used=1366Kb free=1137Kb
    [INFO] CodeCache: size=2496Kb used=1359Kb max_used=1366Kb free=1136Kb
    [INFO] CodeCache: size=2496Kb used=1364Kb max_used=1366Kb free=1132Kb
    [INFO] CodeCache: size=2496Kb used=1365Kb max_used=1366Kb free=1130Kb
    [INFO] CodeCache: size=2496Kb used=1366Kb max_used=1377Kb free=1129Kb
    [INFO] CodeCache: size=2496Kb used=1366Kb max_used=1377Kb free=1129Kb
    [INFO] CodeCache: size=2496Kb used=1371Kb max_used=1377Kb free=1124Kb
    [INFO] CodeCache: size=2496Kb used=1373Kb max_used=1377Kb free=1122Kb
    [INFO] CodeCache: size=2496Kb used=1373Kb max_used=1377Kb free=1122Kb
    [INFO] CodeCache: size=2496Kb used=1374Kb max_used=1377Kb free=1121Kb
    [INFO] CodeCache: size=2496Kb used=1375Kb max_used=1377Kb free=1120Kb
    [INFO] CodeCache: size=2496Kb used=1376Kb max_used=1377Kb free=1119Kb
    [INFO] CodeCache: size=2496Kb used=1376Kb max_used=1377Kb free=1119Kb
    [INFO] CodeCache: size=2496Kb used=1377Kb max_used=1377Kb free=1118Kb
    [INFO] CodeCache: size=2496Kb used=1378Kb max_used=1378Kb free=1117Kb
    [INFO] CodeCache: size=2496Kb used=1379Kb max_used=1379Kb free=1117Kb
    [INFO] CodeCache: size=2496Kb used=1379Kb max_used=1379Kb free=1116Kb
    [INFO] CodeCache: size=2496Kb used=1381Kb max_used=1381Kb free=1114Kb
    [INFO] CodeCache: size=2496Kb used=1382Kb max_used=1382Kb free=1113Kb
    [INFO] CodeCache: size=2496Kb used=1383Kb max_used=1383Kb free=1112Kb
    [INFO] CodeCache: size=2496Kb used=1384Kb max_used=1384Kb free=1111Kb
    [INFO] CodeCache: size=2496Kb used=1385Kb max_used=1396Kb free=1111Kb
    [INFO] CodeCache: size=2496Kb used=1385Kb max_used=1397Kb free=1110Kb
    [INFO] CodeCache: size=2496Kb used=1387Kb max_used=1398Kb free=1108Kb
    [INFO] CodeCache: size=2496Kb used=1387Kb max_used=1399Kb free=1108Kb
    [INFO] CodeCache: size=2496Kb used=1388Kb max_used=1400Kb free=1107Kb
    [INFO] CodeCache: size=2496Kb used=1389Kb max_used=1401Kb free=1106Kb
    [INFO] CodeCache: size=2496Kb used=1390Kb max_used=1402Kb free=1105Kb
    [INFO] CodeCache: size=2496Kb used=1392Kb max_used=1404Kb free=1103Kb
    [INFO] CodeCache: size=2496Kb used=1393Kb max_used=1404Kb free=1102Kb
    [INFO] CodeCache: size=2496Kb used=1394Kb max_used=1407Kb free=1101Kb
    [INFO] CodeCache: size=2496Kb used=1396Kb max_used=1407Kb free=1099Kb
    [INFO] CodeCache: size=2496Kb used=1396Kb max_used=1407Kb free=1099Kb
    [INFO] CodeCache: size=2496Kb used=1397Kb max_used=1407Kb free=1098Kb
    [INFO] CodeCache: size=2496Kb used=1400Kb max_used=1407Kb free=1096Kb
    [INFO] CodeCache: size=2496Kb used=1401Kb max_used=1407Kb free=1094Kb
    [INFO] CodeCache: size=2496Kb used=1404Kb max_used=1416Kb free=1091Kb
    [INFO] CodeCache: size=2496Kb used=1405Kb max_used=1416Kb free=1090Kb
    [INFO] CodeCache: size=2496Kb used=1406Kb max_used=1417Kb free=1089Kb
    [INFO] CodeCache: size=2496Kb used=1421Kb max_used=1426Kb free=1075Kb
    [INFO] CodeCache: size=2496Kb used=1411Kb max_used=1426Kb free=1084Kb
    [INFO] CodeCache: size=2496Kb used=1413Kb max_used=1426Kb free=1082Kb
    [INFO] CodeCache: size=2496Kb used=1415Kb max_used=1427Kb free=1080Kb
    [INFO] CodeCache: size=2496Kb used=1416Kb max_used=1427Kb free=1079Kb
    [INFO] CodeCache: size=2496Kb used=1418Kb max_used=1430Kb free=1077Kb
    [INFO] CodeCache: size=2496Kb used=1422Kb max_used=1430Kb free=1073Kb
    [INFO] CodeCache: size=2496Kb used=1423Kb max_used=1430Kb free=1072Kb
    [INFO] CodeCache: size=2496Kb used=1426Kb max_used=1430Kb free=1069Kb
    [INFO] CodeCache: size=2496Kb used=1427Kb max_used=1430Kb free=1068Kb
    [INFO] CodeCache: size=2496Kb used=1428Kb max_used=1430Kb free=1067Kb
    [INFO] CodeCache: size=2496Kb used=1430Kb max_used=1430Kb free=1065Kb
    [INFO] CodeCache: size=2496Kb used=1431Kb max_used=1431Kb free=1064Kb
    [INFO] CodeCache: size=2496Kb used=1432Kb max_used=1443Kb free=1063Kb
    [INFO] CodeCache: size=2496Kb used=1433Kb max_used=1443Kb free=1062Kb
    [INFO] CodeCache: size=2496Kb used=1435Kb max_used=1447Kb free=1060Kb
    [INFO] CodeCache: size=2496Kb used=1437Kb max_used=1449Kb free=1058Kb
    [INFO] CodeCache: size=2496Kb used=1438Kb max_used=1449Kb free=1057Kb
    [INFO] CodeCache: size=2496Kb used=1440Kb max_used=1449Kb free=1055Kb
    [INFO] CodeCache: size=2496Kb used=1442Kb max_used=1449Kb free=1053Kb
    [INFO] CodeCache: size=2496Kb used=1443Kb max_used=1449Kb free=1052Kb
    [INFO] CodeCache: size=2496Kb used=1444Kb max_used=1449Kb free=1051Kb
    [INFO] CodeCache: size=2496Kb used=1446Kb max_used=1449Kb free=1049Kb
    [INFO] CodeCache: size=2496Kb used=1448Kb max_used=1459Kb free=1048Kb
    [INFO] CodeCache: size=2496Kb used=1448Kb max_used=1459Kb free=1047Kb
    [INFO] CodeCache: size=2496Kb used=1450Kb max_used=1463Kb free=1045Kb
    [INFO] CodeCache: size=2496Kb used=1451Kb max_used=1463Kb free=1044Kb
    [INFO] CodeCache: size=2496Kb used=1453Kb max_used=1463Kb free=1042Kb
    [INFO] CodeCache: size=2496Kb used=1455Kb max_used=1463Kb free=1040Kb
    [INFO] CodeCache: size=2496Kb used=1456Kb max_used=1463Kb free=1039Kb
    [INFO] CodeCache: size=2496Kb used=1458Kb max_used=1463Kb free=1037Kb
    [INFO] CodeCache: size=2496Kb used=1469Kb max_used=1469Kb free=1026Kb
    [INFO] CodeCache: size=2496Kb used=1471Kb max_used=1483Kb free=1024Kb
    [INFO] CodeCache: size=2496Kb used=1484Kb max_used=1484Kb free=1011Kb
    [INFO] CodeCache: size=2496Kb used=1486Kb max_used=1498Kb free=1009Kb
    [INFO] CodeCache: size=2496Kb used=1487Kb max_used=1498Kb free=1008Kb
    [INFO] CodeCache: size=2496Kb used=1488Kb max_used=1498Kb free=1007Kb
    [INFO] CodeCache: size=2496Kb used=1502Kb max_used=1502Kb free=993Kb
    [INFO] CodeCache: size=2496Kb used=1492Kb max_used=1504Kb free=1003Kb
    [INFO] CodeCache: size=2496Kb used=1493Kb max_used=1504Kb free=1002Kb
    [INFO] CodeCache: size=2496Kb used=1494Kb max_used=1504Kb free=1001Kb
    [INFO] CodeCache: size=2496Kb used=1494Kb max_used=1504Kb free=1001Kb
    [INFO] CodeCache: size=2496Kb used=1495Kb max_used=1504Kb free=1000Kb
    [INFO] CodeCache: size=2496Kb used=1496Kb max_used=1508Kb free=999Kb
    [INFO] CodeCache: size=2496Kb used=1497Kb max_used=1508Kb free=998Kb
    [INFO] CodeCache: size=2496Kb used=1498Kb max_used=1508Kb free=997Kb
    [INFO] CodeCache: size=2496Kb used=1499Kb max_used=1508Kb free=996Kb
    [INFO] CodeCache: size=2496Kb used=1500Kb max_used=1511Kb free=995Kb
    [INFO] CodeCache: size=2496Kb used=1501Kb max_used=1513Kb free=994Kb
    [INFO] CodeCache: size=2496Kb used=1502Kb max_used=1514Kb free=993Kb
    [INFO] CodeCache: size=2496Kb used=1503Kb max_used=1514Kb free=992Kb
    [INFO] CodeCache: size=2496Kb used=1504Kb max_used=1514Kb free=991Kb
    [INFO] CodeCache: size=2496Kb used=1506Kb max_used=1514Kb free=989Kb
    [INFO] CodeCache: size=2496Kb used=1507Kb max_used=1514Kb free=988Kb
    [INFO] CodeCache: size=2496Kb used=1509Kb max_used=1514Kb free=986Kb
    [INFO] CodeCache: size=2496Kb used=1510Kb max_used=1514Kb free=985Kb
    [INFO] CodeCache: size=2496Kb used=1511Kb max_used=1514Kb free=984Kb
    [INFO] CodeCache: size=2496Kb used=1512Kb max_used=1523Kb free=983Kb
    [INFO] CodeCache: size=2496Kb used=1513Kb max_used=1523Kb free=982Kb
    [INFO] CodeCache: size=2496Kb used=1515Kb max_used=1526Kb free=980Kb
    [INFO] CodeCache: size=2496Kb used=1523Kb max_used=1538Kb free=972Kb
    [INFO] CodeCache: size=2496Kb used=1524Kb max_used=1538Kb free=971Kb
    [INFO] CodeCache: size=2496Kb used=1525Kb max_used=1538Kb free=970Kb
    [INFO] CodeCache: size=2496Kb used=1526Kb max_used=1538Kb free=969Kb
    [INFO] CodeCache: size=2496Kb used=1527Kb max_used=1538Kb free=968Kb
    [INFO] CodeCache: size=2496Kb used=1528Kb max_used=1538Kb free=967Kb
    [INFO] CodeCache: size=2496Kb used=1529Kb max_used=1540Kb free=966Kb
    [INFO] CodeCache: size=2496Kb used=1529Kb max_used=1541Kb free=966Kb
    [INFO] CodeCache: size=2496Kb used=1530Kb max_used=1541Kb free=965Kb
    [INFO] CodeCache: size=2496Kb used=1531Kb max_used=1542Kb free=964Kb
    [INFO] CodeCache: size=2496Kb used=1532Kb max_used=1542Kb free=963Kb
    [INFO] CodeCache: size=2496Kb used=1534Kb max_used=1542Kb free=961Kb
    [INFO] CodeCache: size=2496Kb used=1534Kb max_used=1545Kb free=961Kb
    [INFO] CodeCache: size=2496Kb used=1536Kb max_used=1548Kb free=959Kb
    [INFO] CodeCache: size=2496Kb used=1538Kb max_used=1550Kb free=957Kb
    [INFO] CodeCache: size=2496Kb used=1542Kb max_used=1554Kb free=954Kb
    [INFO] CodeCache: size=2496Kb used=1542Kb max_used=1554Kb free=953Kb
    [INFO] CodeCache: size=2496Kb used=1544Kb max_used=1556Kb free=951Kb
    [INFO] CodeCache: size=2496Kb used=1552Kb max_used=1566Kb free=943Kb
    [INFO] CodeCache: size=2496Kb used=1554Kb max_used=1566Kb free=941Kb
    [INFO] CodeCache: size=2496Kb used=1555Kb max_used=1566Kb free=940Kb
    [INFO] CodeCache: size=2496Kb used=1556Kb max_used=1566Kb free=939Kb
    [INFO] CodeCache: size=2496Kb used=1557Kb max_used=1568Kb free=938Kb
    [INFO] CodeCache: size=2496Kb used=1563Kb max_used=1576Kb free=932Kb
    [INFO] CodeCache: size=2496Kb used=1564Kb max_used=1576Kb free=931Kb
    [INFO] CodeCache: size=2496Kb used=1567Kb max_used=1579Kb free=928Kb
    [INFO] CodeCache: size=2496Kb used=1569Kb max_used=1581Kb free=926Kb
    [INFO] CodeCache: size=2496Kb used=1570Kb max_used=1581Kb free=925Kb
    [INFO] CodeCache: size=2496Kb used=1570Kb max_used=1581Kb free=925Kb
    [INFO] CodeCache: size=2496Kb used=1574Kb max_used=1586Kb free=921Kb
    [INFO] CodeCache: size=2496Kb used=1577Kb max_used=1590Kb free=918Kb
    [INFO] CodeCache: size=2496Kb used=1579Kb max_used=1591Kb free=916Kb
    [INFO] CodeCache: size=2496Kb used=1580Kb max_used=1591Kb free=916Kb
    [INFO] CodeCache: size=2496Kb used=1584Kb max_used=1597Kb free=911Kb
    [INFO] CodeCache: size=2496Kb used=1586Kb max_used=1598Kb free=909Kb
    [INFO] CodeCache: size=2496Kb used=1586Kb max_used=1598Kb free=909Kb
    [INFO] CodeCache: size=2496Kb used=1589Kb max_used=1601Kb free=906Kb
    [INFO] CodeCache: size=2496Kb used=1590Kb max_used=1602Kb free=905Kb
    [INFO] CodeCache: size=2496Kb used=1604Kb max_used=1616Kb free=891Kb
    [INFO] CodeCache: size=2496Kb used=1593Kb max_used=1616Kb free=902Kb
    [INFO] CodeCache: size=2496Kb used=1595Kb max_used=1616Kb free=901Kb
    [INFO] CodeCache: size=2496Kb used=1595Kb max_used=1616Kb free=900Kb
    [INFO] CodeCache: size=2496Kb used=1596Kb max_used=1616Kb free=899Kb
    [INFO] CodeCache: size=2496Kb used=1597Kb max_used=1616Kb free=898Kb
    [INFO] CodeCache: size=2496Kb used=1597Kb max_used=1616Kb free=898Kb
    [INFO] CodeCache: size=2496Kb used=1599Kb max_used=1616Kb free=897Kb
    [INFO] CodeCache: size=2496Kb used=1599Kb max_used=1616Kb free=896Kb
    [INFO] CodeCache: size=2496Kb used=1605Kb max_used=1619Kb free=890Kb
    [INFO] CodeCache: size=2496Kb used=1606Kb max_used=1619Kb free=889Kb
    [INFO] CodeCache: size=2496Kb used=1607Kb max_used=1619Kb free=888Kb
    [INFO] CodeCache: size=2496Kb used=1610Kb max_used=1622Kb free=885Kb
    [INFO] CodeCache: size=2496Kb used=1610Kb max_used=1622Kb free=885Kb
    [INFO] CodeCache: size=2496Kb used=1611Kb max_used=1622Kb free=884Kb
    [INFO] CodeCache: size=2496Kb used=1612Kb max_used=1623Kb free=884Kb
    [INFO] CodeCache: size=2496Kb used=1619Kb max_used=1634Kb free=876Kb
    [INFO] CodeCache: size=2496Kb used=1620Kb max_used=1634Kb free=875Kb
    [INFO] CodeCache: size=2496Kb used=1620Kb max_used=1634Kb free=875Kb
    [INFO] CodeCache: size=2496Kb used=1621Kb max_used=1634Kb free=874Kb
    [INFO] CodeCache: size=2496Kb used=1622Kb max_used=1634Kb free=873Kb
    [INFO] CodeCache: size=2496Kb used=1623Kb max_used=1634Kb free=872Kb
    [INFO] CodeCache: size=2496Kb used=1641Kb max_used=1660Kb free=854Kb
    [INFO] CodeCache: size=2496Kb used=1652Kb max_used=1668Kb free=843Kb
    [INFO] CodeCache: size=2496Kb used=1660Kb max_used=1676Kb free=835Kb
    [INFO] CodeCache: size=2496Kb used=1661Kb max_used=1676Kb free=834Kb
    [INFO] CodeCache: size=2496Kb used=1662Kb max_used=1676Kb free=833Kb
    [INFO] CodeCache: size=2496Kb used=1662Kb max_used=1676Kb free=833Kb
    [INFO] CodeCache: size=2496Kb used=1664Kb max_used=1676Kb free=831Kb
    [INFO] CodeCache: size=2496Kb used=1665Kb max_used=1676Kb free=830Kb
    [INFO] CodeCache: size=2496Kb used=1690Kb max_used=1712Kb free=805Kb
    [INFO] CodeCache: size=2496Kb used=1691Kb max_used=1712Kb free=804Kb
    [INFO] CodeCache: size=2496Kb used=1692Kb max_used=1712Kb free=803Kb
    [INFO] CodeCache: size=2496Kb used=1692Kb max_used=1712Kb free=803Kb
    [INFO] CodeCache: size=2496Kb used=1693Kb max_used=1712Kb free=802Kb
    [INFO] CodeCache: size=2496Kb used=1694Kb max_used=1712Kb free=801Kb
    [INFO] CodeCache: size=2496Kb used=1695Kb max_used=1712Kb free=801Kb
    [INFO] CodeCache: size=2496Kb used=1695Kb max_used=1712Kb free=800Kb
    [INFO] CodeCache: size=2496Kb used=1696Kb max_used=1712Kb free=799Kb
    [INFO] CodeCache: size=2496Kb used=1696Kb max_used=1712Kb free=799Kb
    [INFO] CodeCache: size=2496Kb used=1697Kb max_used=1712Kb free=798Kb
    [INFO] CodeCache: size=2496Kb used=1700Kb max_used=1712Kb free=795Kb
    [INFO] CodeCache: size=2496Kb used=1700Kb max_used=1712Kb free=795Kb
    [INFO] CodeCache: size=2496Kb used=1703Kb max_used=1714Kb free=792Kb
    [INFO] CodeCache: size=2496Kb used=1703Kb max_used=1714Kb free=792Kb
    [INFO] CodeCache: size=2496Kb used=1704Kb max_used=1714Kb free=791Kb
    [INFO] CodeCache: size=2496Kb used=1705Kb max_used=1714Kb free=790Kb
    [INFO] CodeCache: size=2496Kb used=1706Kb max_used=1718Kb free=789Kb
    [INFO] CodeCache: size=2496Kb used=1707Kb max_used=1718Kb free=788Kb
    [INFO] CodeCache: size=2496Kb used=1734Kb max_used=1745Kb free=761Kb
    [INFO] CodeCache: size=2496Kb used=1765Kb max_used=1778Kb free=730Kb
    [INFO] CodeCache: size=2496Kb used=1755Kb max_used=1778Kb free=740Kb
    [INFO] CodeCache: size=2496Kb used=1757Kb max_used=1778Kb free=738Kb
    [INFO] CodeCache: size=2496Kb used=1762Kb max_used=1778Kb free=733Kb
    [INFO] CodeCache: size=2496Kb used=1763Kb max_used=1778Kb free=732Kb
    [INFO] CodeCache: size=2496Kb used=1763Kb max_used=1778Kb free=732Kb
    [INFO] CodeCache: size=2496Kb used=1764Kb max_used=1778Kb free=731Kb
    [INFO] CodeCache: size=2496Kb used=1765Kb max_used=1778Kb free=730Kb
    [INFO] CodeCache: size=2496Kb used=1766Kb max_used=1778Kb free=730Kb
    [INFO] CodeCache: size=2496Kb used=1768Kb max_used=1780Kb free=727Kb
    [INFO] CodeCache: size=2496Kb used=1769Kb max_used=1780Kb free=726Kb
    [INFO] CodeCache: size=2496Kb used=1770Kb max_used=1782Kb free=725Kb
    [INFO] CodeCache: size=2496Kb used=1771Kb max_used=1782Kb free=724Kb
    [INFO] CodeCache: size=2496Kb used=1780Kb max_used=1794Kb free=716Kb
    [INFO] CodeCache: size=2496Kb used=1780Kb max_used=1794Kb free=715Kb
    [INFO] CodeCache: size=2496Kb used=1788Kb max_used=1803Kb free=707Kb
    [INFO] CodeCache: size=2496Kb used=1801Kb max_used=1820Kb free=694Kb
    [INFO] CodeCache: size=2496Kb used=1801Kb max_used=1820Kb free=694Kb
    [INFO] CodeCache: size=2496Kb used=1801Kb max_used=1820Kb free=694Kb
    [INFO] CodeCache: size=2496Kb used=1802Kb max_used=1820Kb free=693Kb
    [INFO] CodeCache: size=2496Kb used=1803Kb max_used=1820Kb free=692Kb
    [INFO] CodeCache: size=2496Kb used=1804Kb max_used=1820Kb free=691Kb
    [INFO] CodeCache: size=2496Kb used=1804Kb max_used=1820Kb free=691Kb
    [INFO] CodeCache: size=2496Kb used=1805Kb max_used=1820Kb free=690Kb
    [INFO] CodeCache: size=2496Kb used=1806Kb max_used=1820Kb free=689Kb
    [INFO] CodeCache: size=2496Kb used=1807Kb max_used=1820Kb free=688Kb
    [INFO] CodeCache: size=2496Kb used=1807Kb max_used=1820Kb free=688Kb
    [INFO] CodeCache: size=2496Kb used=1808Kb max_used=1820Kb free=687Kb
    [INFO] CodeCache: size=2496Kb used=1809Kb max_used=1820Kb free=686Kb
    [INFO] CodeCache: size=2496Kb used=1809Kb max_used=1820Kb free=686Kb
    [INFO] CodeCache: size=2496Kb used=1811Kb max_used=1823Kb free=684Kb
    [INFO] CodeCache: size=2496Kb used=1816Kb max_used=1830Kb free=679Kb
    [INFO] CodeCache: size=2496Kb used=1817Kb max_used=1830Kb free=678Kb
    [INFO] CodeCache: size=2496Kb used=1818Kb max_used=1830Kb free=677Kb
    [INFO] CodeCache: size=2496Kb used=1824Kb max_used=1838Kb free=671Kb
    [INFO] CodeCache: size=2496Kb used=1825Kb max_used=1838Kb free=670Kb
    [INFO] CodeCache: size=2496Kb used=1826Kb max_used=1838Kb free=669Kb
    [INFO] CodeCache: size=2496Kb used=1826Kb max_used=1838Kb free=669Kb
    [INFO] CodeCache: size=2496Kb used=1827Kb max_used=1838Kb free=668Kb
    [INFO] CodeCache: size=2496Kb used=1843Kb max_used=1861Kb free=652Kb
    [INFO] CodeCache: size=2496Kb used=1847Kb max_used=1861Kb free=648Kb
    [INFO] CodeCache: size=2496Kb used=1876Kb max_used=1899Kb free=620Kb
    [INFO] CodeCache: size=2496Kb used=1876Kb max_used=1899Kb free=619Kb
    [INFO] CodeCache: size=2496Kb used=1877Kb max_used=1899Kb free=618Kb
    [INFO] CodeCache: size=2496Kb used=1878Kb max_used=1899Kb free=617Kb
    [INFO] CodeCache: size=2496Kb used=1878Kb max_used=1899Kb free=617Kb
    [INFO] CodeCache: size=2496Kb used=1903Kb max_used=1915Kb free=592Kb
    [INFO] CodeCache: size=2496Kb used=1906Kb max_used=1927Kb free=590Kb
    [INFO] CodeCache: size=2496Kb used=1906Kb max_used=1927Kb free=589Kb
    [INFO] CodeCache: size=2496Kb used=1907Kb max_used=1927Kb free=588Kb
    [INFO] CodeCache: size=2496Kb used=1912Kb max_used=1927Kb free=583Kb
    [INFO] CodeCache: size=2496Kb used=1919Kb max_used=1934Kb free=576Kb
    [INFO] CodeCache: size=2496Kb used=1925Kb max_used=1934Kb free=570Kb
    [INFO] CodeCache: size=2496Kb used=1919Kb max_used=1941Kb free=576Kb
    [INFO] CodeCache: size=2496Kb used=1920Kb max_used=1941Kb free=575Kb
    [INFO] CodeCache: size=2496Kb used=1921Kb max_used=1941Kb free=574Kb
    [INFO] CodeCache: size=2496Kb used=1935Kb max_used=1952Kb free=560Kb
    [INFO] CodeCache: size=2496Kb used=1935Kb max_used=1957Kb free=560Kb
    [INFO] CodeCache: size=2496Kb used=1936Kb max_used=1957Kb free=559Kb
    [INFO] CodeCache: size=2496Kb used=1940Kb max_used=1957Kb free=555Kb
    [INFO] CodeCache: size=2496Kb used=1941Kb max_used=1957Kb free=554Kb
    [INFO] CodeCache: size=2496Kb used=1942Kb max_used=1957Kb free=553Kb
    [INFO] CodeCache: size=2496Kb used=1942Kb max_used=1957Kb free=553Kb
    [INFO] CodeCache: size=2496Kb used=1943Kb max_used=1957Kb free=552Kb
    [INFO] CodeCache: size=2496Kb used=1944Kb max_used=1957Kb free=551Kb
    [INFO] CodeCache: size=2496Kb used=1945Kb max_used=1957Kb free=551Kb
    [INFO] CodeCache: size=2496Kb used=1945Kb max_used=1957Kb free=550Kb
    [INFO] CodeCache: size=2496Kb used=1946Kb max_used=1957Kb free=549Kb
    [INFO] CodeCache: size=2496Kb used=1946Kb max_used=1957Kb free=549Kb
    [INFO] CodeCache: size=2496Kb used=1951Kb max_used=1964Kb free=544Kb
    [INFO] CodeCache: size=2496Kb used=1951Kb max_used=1964Kb free=544Kb
    [INFO] CodeCache: size=2496Kb used=1952Kb max_used=1964Kb free=543Kb
    [INFO] CodeCache: size=2496Kb used=1952Kb max_used=1964Kb free=543Kb
    [INFO] CodeCache: size=2496Kb used=1953Kb max_used=1964Kb free=542Kb
    [INFO] CodeCache: size=2496Kb used=1953Kb max_used=1964Kb free=542Kb
    [INFO] CodeCache: size=2496Kb used=1959Kb max_used=1972Kb free=536Kb
    [INFO] CodeCache: size=2496Kb used=1959Kb max_used=1972Kb free=536Kb
    [INFO] CodeCache: size=2496Kb used=1959Kb max_used=1972Kb free=536Kb
    [INFO] CodeCache: size=2496Kb used=1929Kb max_used=1972Kb free=566Kb
    [INFO] CodeCache: size=2496Kb used=1929Kb max_used=1972Kb free=566Kb
    [INFO] CodeCache: size=2496Kb used=1929Kb max_used=1972Kb free=566Kb
    [INFO] CodeCache: size=2496Kb used=1936Kb max_used=1972Kb free=560Kb
    [INFO] CodeCache: size=2496Kb used=1930Kb max_used=1972Kb free=565Kb
    [INFO] CodeCache: size=2496Kb used=1931Kb max_used=1972Kb free=564Kb
    [INFO] CodeCache: size=2496Kb used=1932Kb max_used=1972Kb free=563Kb
    [INFO] CodeCache: size=2496Kb used=1933Kb max_used=1972Kb free=562Kb
    [INFO] CodeCache: size=2496Kb used=1933Kb max_used=1972Kb free=562Kb
    [INFO] CodeCache: size=2496Kb used=1933Kb max_used=1972Kb free=562Kb
    [INFO] CodeCache: size=2496Kb used=1935Kb max_used=1972Kb free=560Kb
    [INFO] CodeCache: size=2496Kb used=1936Kb max_used=1972Kb free=559Kb
    [INFO] CodeCache: size=2496Kb used=1936Kb max_used=1972Kb free=559Kb
    [INFO] CodeCache: size=2496Kb used=1937Kb max_used=1972Kb free=558Kb
    [INFO] CodeCache: size=2496Kb used=1854Kb max_used=1972Kb free=641Kb
    [INFO] CodeCache: size=2496Kb used=1855Kb max_used=1972Kb free=640Kb
    [INFO] CodeCache: size=2496Kb used=1856Kb max_used=1972Kb free=639Kb
    [INFO] CodeCache: size=2496Kb used=1857Kb max_used=1972Kb free=638Kb
    [INFO] CodeCache: size=2496Kb used=1857Kb max_used=1972Kb free=638Kb
    [INFO] CodeCache: size=2496Kb used=1858Kb max_used=1972Kb free=637Kb
    [INFO] CodeCache: size=2496Kb used=1858Kb max_used=1972Kb free=637Kb
    [INFO] CodeCache: size=2496Kb used=1859Kb max_used=1972Kb free=636Kb
    [INFO] CodeCache: size=2496Kb used=1859Kb max_used=1972Kb free=636Kb
    [INFO] CodeCache: size=2496Kb used=1861Kb max_used=1972Kb free=634Kb
    [INFO] CodeCache: size=2496Kb used=1862Kb max_used=1972Kb free=633Kb
    [INFO] CodeCache: size=2496Kb used=1862Kb max_used=1972Kb free=633Kb
    [INFO] CodeCache: size=2496Kb used=1863Kb max_used=1972Kb free=632Kb
    [INFO] CodeCache: size=2496Kb used=1863Kb max_used=1972Kb free=632Kb
    [INFO] CodeCache: size=2496Kb used=1867Kb max_used=1972Kb free=628Kb
    [INFO] CodeCache: size=2496Kb used=1877Kb max_used=1972Kb free=618Kb
    [INFO] CodeCache: size=2496Kb used=1791Kb max_used=1972Kb free=704Kb
    [INFO] CodeCache: size=2496Kb used=1821Kb max_used=1972Kb free=674Kb
    [INFO]  bounds [0x000000010e7e3000, 0x000000010ea53000, 0x000000010ea53000]
    [INFO]  total_blobs=785 nmethods=411 adapters=266
    [INFO]  compilation: disabled (not enough contiguous free space left)
    [INFO] CodeCache: size=2496Kb used=1816Kb max_used=1972Kb free=679Kb
    [INFO] CodeCache: size=2496Kb used=1791Kb max_used=1972Kb free=704Kb
    [INFO] Java HotSpot(TM) 64-Bit Server VM warning: CodeCache is full. Compiler has been disabled.
    [INFO] Java HotSpot(TM) 64-Bit Server VM warning: Try increasing the code cache size using -XX:ReservedCodeCacheSize=
    [INFO] CodeCache: size=2496Kb used=1791Kb max_used=1972Kb free=704Kb
    [INFO] CodeCache: size=2496Kb used=1796Kb max_used=1972Kb free=699Kb
    [INFO] CodeCache: size=2496Kb used=1797Kb max_used=1972Kb free=698Kb
    [INFO] CodeCache: size=2496Kb used=1798Kb max_used=1972Kb free=697Kb
    [INFO] CodeCache: size=2496Kb used=1799Kb max_used=1972Kb free=696Kb
    [INFO] CodeCache: size=2496Kb used=1802Kb max_used=1972Kb free=693Kb
    [INFO] CodeCache: size=2496Kb used=1803Kb max_used=1972Kb free=692Kb
    [INFO] CodeCache: size=2496Kb used=1806Kb max_used=1972Kb free=689Kb
    [INFO] CodeCache: size=2496Kb used=1809Kb max_used=1972Kb free=686Kb
    [INFO] CodeCache: size=2496Kb used=1810Kb max_used=1972Kb free=685Kb
    [INFO] CodeCache: size=2496Kb used=1813Kb max_used=1972Kb free=682Kb

# Experiment 7

Setting 

> atlas-run --jvmargs "-XX:ReservedCodeCacheSize=10000K -XX:InitialCodeCacheSize=10000K  -XX:+PrintCodeCacheOnCompilation"> jvm.log 2> jvm-error.log

Starts and runs

    [INFO] CodeCache: size=10000Kb used=8288Kb max_used=8604Kb free=1711Kb
    [INFO]  bounds [0x000000010945e000, 0x0000000109e22000, 0x0000000109e22000]
    [INFO]  total_blobs=2619 nmethods=2060 adapters=469
    [INFO]  compilation: disabled (not enough contiguous free space left)
    [INFO] CodeCache: size=10000Kb used=8288Kb max_used=8604Kb free=1711Kb
    [INFO] CodeCache: size=10000Kb used=8249Kb max_used=8604Kb free=1750Kb
    [INFO] CodeCache: size=10000Kb used=8249Kb max_used=8604Kb free=1750Kb
    [INFO] Java HotSpot(TM) 64-Bit Server VM warning: CodeCache is full. Compiler has been disabled.
    [INFO] Java HotSpot(TM) 64-Bit Server VM warning: Try increasing the code cache size using -XX:ReservedCodeCacheSize=
    [INFO] CodeCache: size=10000Kb used=8249Kb max_used=8604Kb free=1750Kb
    [INFO] CodeCache: size=10000Kb used=7669Kb max_used=8604Kb free=2330Kb
    [INFO] CodeCache: size=10000Kb used=7670Kb max_used=8604Kb free=2329Kb

giving

    Thu Aug 15 18:30:16 BST 2019

    Memory Pool: Code Cache
        Init : 10MB (10240000)
        Used: 6MB (6405056)
        Max : 10MB (10240000)
        Committed: 10MB (10240000)


## Notes

At higher values e.g. 

> atlas-run --jvmargs "-XX:ReservedCodeCacheSize=14000K -XX:InitialCodeCacheSize=14000K"

The compiler never fails.

Only seemed to fail when free < 2000kb and/or < 20% of max

No obvious indication of when compiler failure happened from the JMX info.