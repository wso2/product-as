#!/usr/bin/expect

spawn sh ciphertool.sh -Dconfigure
expect "Please Enter Primary KeyStore Password of Carbon Server"
send "wso2carbon\r"
interact