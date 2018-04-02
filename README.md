# Distributed Backup Service

This project is based on the develop a distributed backup service for a local area network (LAN). The idea is to use the free disk space of the computers in a LAN for backing up files in other computers in the same LAN. The service is provided by servers in an environment that is assumed cooperative (rather than hostile). Nevertheless, each server retains control over its own disks and, if needed, may reclaim the space it made available for backing up other computers' files.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You need Java 8 to compile the software.

### Installing and running peer (AUTOMATIC)

Go to Peer/src/ and run:
```
chmod +x run_peers
./run_peers 8
```
this will cleanup any old processes, compile all java files, start local java rmiregistry, and start 8 peer-servers.

### Installing and running peers (MANUAL)

Go to Peer/src/ and, to clean up and compile, run:
```
rm -rf ./logs
rm *.class
javac *.java
```
Then, to start the local java rmiregistry, run:
```
rmiregistry
```
Then, to start a single server-peer, run:
```
java Peer <protocol_ver> <server_id> <access_point> <mcc_ip> <mcc_port> <mbc_ip> <mbc_port> <mrc_ip> <mrc_port>
```
*e.g.:*
```
java Peer 1.1 4 //localhost:1099/BackupPeer4 239.0.0.0 1234 239.1.0.0 1234 239.2.0.0 1234
```
### Running Client

After compiling and running peers, start Client by running:
```
java Client
```
If you prefer the CLI mode, run:
```
java Client <peer_ap> <operation> <param1> <param2>
```
*.e.g:*
```
java Client //localhost:1099/BackupPeer0 BACKUP myfile.pdf 3
```
To get help run:
```
java Client help
```

## Authors

* **André Machado** - [andremachado94](https://github.com/andremachado94)
* **Diogo Seca** - [diogoseca](https://github.com/diogoseca)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
