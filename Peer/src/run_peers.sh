# this script receives as argument the number of peers to start,
# kills previous instances, starts rmiregistry and start $1 peers

#!/bin/bash
set -e

# parameters check
die () {
    echo >&2 "$@"
    exit 1
}
[ "$#" -eq 1 ] || die "1 argument required, $# provided"
echo $1 | grep -E -q '^[0-9]+$' || die "Numeric argument required, $1 provided"

# clean up previous instances
echo "Cleaning up previous instances..."
pkill rmiregistry && pkill -9 -f Peer
mkdir -p logs
rm -f ./logs/*.log
rm -f *.class

# compile
echo "Compiling..."
javac *.java

# start rmiregistry
echo "Starting RMI Registry..."
rmiregistry &
sleep 1

# start peers
for ((i=0;i<$1;i++)); do
    echo "Starting peer //localhost/BackupPeer$i..."
	java -Djava.net.preferIPv4Stack=true Peer > logs/peer$i.log &
	sleep 1
done
#syntax: java Peer protocol_version server_id access_point, ip:port, ip:port, ip:port 
#exampl: java Peer 1.0 3 0 239.0.0.0 1234 239.1.0.0 1234 239.2.0.0 1234 > logs/peer0.log &

echo "Done. Peers at the ready."