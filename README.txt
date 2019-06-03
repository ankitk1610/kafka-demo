This project provides basic hand-on for Kafka. For Kafka Commands, please read the following cheat sheet.


******CHEAT SHEET*****

SET PATH : 
nano ~/.bash_profile
export PATH="$PATH:/Users/admin/Downloads/kafka_2.12-2.2.0/bin"



CHANGE LOCATION FOR METADATA STORAGE 
nano server.properties 
log.dirs=/Users/admin/Downloads/kafka_2.12-2.2.0/data/kafka

nano zookeeper.properties 
dataDir=/Users/admin/Downloads/kafka_2.12-2.2.0/data/zookeeper


CHECK IF SOME SERVICE IS RUNNING ON DEFAULT PORTS
lsof -n -i :9092 | grep LISTEN
lsof -n -i :2181 | grep LISTEN


STEP 1: START ZOOKEEPER
zookeeper-server-start.sh config/zookeeper.properties

Step 2: START KAFKA SERVER 
kafka-server-start.sh config/server.properties

CREATE A TOPIC:
kafka-topics.sh  --zookeeper 127.0.0.1:2181 --topic second_topic --create --partitions 3 --replication-factor 1

LIST OUT ALL THE TOPICS:
kafka-topics.sh  --zookeeper 127.0.0.1:2181 --list

DELETE A TOPIC:
kafka-topics.sh  --zookeeper 127.0.0.1:2181 --topic second_topic --delete

DESCRIBE A TOPIC: 
kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe


START A PRODUCER :
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic


START A CONSUMER: 
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic (only intercepts new entries)

kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning (for all messages)

CREATE A NEW CONSUMER GROUP:
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic  --group my-first-app


LIST OUT ALL CONSUMER GROUPS:
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

DESCRIBE A GROUP
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group  my-first-app

GET MESSEGAES: 
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic  --group my-first-app

RESET OFFSET
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-first-app --reset-offsets --to-earliest --execute --topic first_topic

SHIFT OFFSET
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-first-app --reset-offsets --shift-by -2  --execute --topic first_topic


CHANGE CONFIG SETTINGS : 
kafka-configs.sh --zookeeper 127.0.0.1:2181  --entity-type topics --entity-name  config-topic --add-config min.insync.replicas=2 --alter


see config/server.properties for default settings.
